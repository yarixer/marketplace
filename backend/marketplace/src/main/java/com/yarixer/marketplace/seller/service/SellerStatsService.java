package com.yarixer.marketplace.seller.service;

import com.yarixer.marketplace.catalog.api.PublicProductVoteSummaryResponse;
import com.yarixer.marketplace.catalog.domain.Product;
import com.yarixer.marketplace.catalog.domain.ProductRevision;
import com.yarixer.marketplace.catalog.domain.ProductRevisionStatus;
import com.yarixer.marketplace.catalog.repository.ProductRepository;
import com.yarixer.marketplace.catalog.repository.ProductRevisionRepository;
import com.yarixer.marketplace.catalog.repository.ProductVoteRepository;
import com.yarixer.marketplace.order.domain.OrderItem;
import com.yarixer.marketplace.order.domain.OrderStatus;
import com.yarixer.marketplace.order.repository.OrderItemRepository;
import com.yarixer.marketplace.seller.api.SellerMonthlySalesPointResponse;
import com.yarixer.marketplace.seller.api.SellerProductStatsRowResponse;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerStatsService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductRevisionRepository productRevisionRepository;
    private final ProductVoteRepository productVoteRepository;

    public List<SellerMonthlySalesPointResponse> getMonthlySales(Long sellerUserId) {
        List<OrderItem> paidItems = orderItemRepository
                .findAllBySellerProfile_User_IdAndOrder_Status(sellerUserId, OrderStatus.PAID);

        Map<YearMonth, long[]> bucket = new HashMap<>();

        YearMonth current = YearMonth.now(ZoneOffset.UTC);
        List<YearMonth> months = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            months.add(ym);
            bucket.put(ym, new long[]{0L, 0L});
        }

        for (OrderItem item : paidItems) {
            if (item.getOrder().getPaidAt() == null) {
                continue;
            }
            YearMonth ym = YearMonth.from(item.getOrder().getPaidAt());
            if (bucket.containsKey(ym)) {
                bucket.get(ym)[0] += 1;
                bucket.get(ym)[1] += item.getPriceMinor();
            }
        }

        return months.stream()
                .map(ym -> new SellerMonthlySalesPointResponse(
                        ym.toString(),
                        bucket.get(ym)[0],
                        bucket.get(ym)[1]
                ))
                .toList();
    }

    public List<SellerProductStatsRowResponse> getProductStats(Long sellerUserId) {
        List<Product> products = productRepository.findAllBySellerProfile_User_IdOrderByIdDesc(sellerUserId);
        List<OrderItem> paidItems = orderItemRepository
                .findAllBySellerProfile_User_IdAndOrder_Status(sellerUserId, OrderStatus.PAID);

        Map<Long, long[]> salesByProduct = new HashMap<>();
        for (OrderItem item : paidItems) {
            salesByProduct.computeIfAbsent(item.getProduct().getId(), ignored -> new long[]{0L, 0L});
            salesByProduct.get(item.getProduct().getId())[0] += 1;
            salesByProduct.get(item.getProduct().getId())[1] += item.getPriceMinor();
        }

        return products.stream()
                .map(product -> {
                    List<ProductRevision> revisions = productRevisionRepository
                            .findByProduct_IdOrderByRevisionNumberDesc(product.getId());

                    ProductRevision draft = findByStatus(revisions, ProductRevisionStatus.DRAFT);
                    ProductRevision pending = findByStatus(revisions, ProductRevisionStatus.PENDING_REVIEW);
                    ProductRevision live = product.getCurrentLiveRevision();

                    String title = draft != null
                            ? draft.getTitle()
                            : pending != null
                            ? pending.getTitle()
                            : live != null
                            ? live.getTitle()
                            : "(untitled)";

                    String workflowStatus = draft != null
                            ? "DRAFT"
                            : pending != null
                            ? "PENDING_REVIEW"
                            : live != null
                            ? "LIVE"
                            : "EMPTY";

                    long currentPriceMinor = draft != null
                            ? draft.getPriceMinor()
                            : pending != null
                            ? pending.getPriceMinor()
                            : live != null
                            ? live.getPriceMinor()
                            : 0L;

                    long[] sales = salesByProduct.getOrDefault(product.getId(), new long[]{0L, 0L});
                    PublicProductVoteSummaryResponse votes =
                            productVoteRepository.getVoteSummaryByProductId(product.getId());

                    return new SellerProductStatsRowResponse(
                            product.getId(),
                            product.getSlug(),
                            title,
                            workflowStatus,
                            currentPriceMinor,
                            sales[0],
                            sales[1],
                            votes.score(),
                            votes.positiveCount(),
                            votes.negativeCount()
                    );
                })
                .sorted(Comparator.comparing(SellerProductStatsRowResponse::productId).reversed())
                .toList();
    }

    private ProductRevision findByStatus(List<ProductRevision> revisions, ProductRevisionStatus status) {
        return revisions.stream()
                .filter(revision -> revision.getStatus() == status)
                .max(Comparator.comparingInt(ProductRevision::getRevisionNumber))
                .orElse(null);
    }
}