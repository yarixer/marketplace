package com.yarixer.marketplace.buyer.service;

import com.yarixer.marketplace.buyer.api.*;
import com.yarixer.marketplace.catalog.domain.Product;
import com.yarixer.marketplace.catalog.domain.ProductRevision;
import com.yarixer.marketplace.catalog.domain.ProductRevisionStatus;
import com.yarixer.marketplace.catalog.domain.ProductState;
import com.yarixer.marketplace.catalog.repository.ProductRepository;
import com.yarixer.marketplace.common.exception.ResourceNotFoundException;
import com.yarixer.marketplace.order.domain.CustomerOrder;
import com.yarixer.marketplace.order.domain.OrderItem;
import com.yarixer.marketplace.order.domain.OrderStatus;
import com.yarixer.marketplace.order.domain.ProductEntitlement;
import com.yarixer.marketplace.order.repository.CustomerOrderRepository;
import com.yarixer.marketplace.order.repository.ProductEntitlementRepository;
import com.yarixer.marketplace.storage.StorageProperties;
import com.yarixer.marketplace.storage.StorageService;
import com.yarixer.marketplace.user.domain.AppUser;
import com.yarixer.marketplace.user.repository.UserRepository;
import com.yarixer.marketplace.wallet.service.WalletLedgerService;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class BuyerOrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final ProductEntitlementRepository productEntitlementRepository;
    private final StorageService storageService;
    private final StorageProperties storageProperties;
    private final WalletLedgerService walletLedgerService;

    public BuyerOrderResponse createOrder(Long buyerUserId, BuyerCreateOrderRequest request) {
        if (productEntitlementRepository.existsByBuyer_IdAndProduct_Id(buyerUserId, request.productId())) {
            throw new IllegalArgumentException("Product is already owned");
        }

        AppUser buyer = userRepository.findById(buyerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        Product product = productRepository.findByIdAndStateAndCurrentLiveRevision_Status(
                        request.productId(),
                        ProductState.ACTIVE,
                        ProductRevisionStatus.APPROVED
                )
                .orElseThrow(() -> new ResourceNotFoundException("Published product not found"));

        ProductRevision liveRevision = product.getCurrentLiveRevision();

        CustomerOrder order = new CustomerOrder();
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.DRAFT);
        order.setSubtotalMinor(liveRevision.getPriceMinor());
        order.setCurrency(liveRevision.getCurrency());

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setRevision(liveRevision);
        orderItem.setSellerProfile(product.getSellerProfile());
        orderItem.setPriceMinor(liveRevision.getPriceMinor());

        order.addItem(orderItem);

        CustomerOrder savedOrder = customerOrderRepository.save(order);
        return toOrderResponse(savedOrder);
    }

    public BuyerOrderResponse mockCompleteOrder(Long buyerUserId, Long orderId) {
        CustomerOrder order = customerOrderRepository.findByIdAndBuyer_Id(orderId, buyerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalArgumentException("Order is already paid");
        }

        for (OrderItem item : order.getItems()) {
            if (productEntitlementRepository.existsByBuyer_IdAndProduct_Id(buyerUserId, item.getProduct().getId())) {
                throw new IllegalArgumentException("Product is already owned");
            }
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now(ZoneOffset.UTC));
        CustomerOrder savedOrder = customerOrderRepository.save(order);

        for (OrderItem item : savedOrder.getItems()) {
            ProductEntitlement entitlement = new ProductEntitlement();
            entitlement.setBuyer(savedOrder.getBuyer());
            entitlement.setProduct(item.getProduct());
            entitlement.setGrantedByOrder(savedOrder);
            productEntitlementRepository.save(entitlement);

            walletLedgerService.creditSaleToSeller(
                    item.getSellerProfile().getUser().getId(),
                    item.getPriceMinor(),
                    "ORDER_ITEM",
                    item.getId(),
                    "Sale credit for product " + item.getProduct().getId()
            );
        }

        return toOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<BuyerOrderResponse> listOrders(Long buyerUserId) {
        return customerOrderRepository.findAllByBuyer_IdOrderByCreatedAtDesc(buyerUserId)
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BuyerLibraryItemResponse> getLibrary(Long buyerUserId) {
        return productEntitlementRepository.findAllByBuyer_IdOrderByCreatedAtDesc(buyerUserId)
                .stream()
                .map(entitlement -> {
                    Product product = entitlement.getProduct();
                    ProductRevision liveRevision = product.getCurrentLiveRevision();
                    boolean archiveAttached = liveRevision != null && StringUtils.hasText(liveRevision.getArchiveObjectKey());

                    return new BuyerLibraryItemResponse(
                            entitlement.getId(),
                            product.getId(),
                            liveRevision != null ? liveRevision.getId() : null,
                            product.getSlug(),
                            liveRevision != null ? liveRevision.getTitle() : "(unavailable)",
                            liveRevision != null ? liveRevision.getShortDescription() : "",
                            liveRevision != null ? liveRevision.getPriceMinor() : 0L,
                            liveRevision != null ? liveRevision.getCurrency() : "USD",
                            product.getSellerProfile().getPublicName(),
                            archiveAttached,
                            archiveAttached,
                            entitlement.getCreatedAt()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public BuyerDownloadLinkResponse createDownloadLink(Long buyerUserId, Long productId) {
        ProductEntitlement entitlement = productEntitlementRepository.findByBuyer_IdAndProduct_Id(buyerUserId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchased product not found"));

        Product product = entitlement.getProduct();
        ProductRevision liveRevision = product.getCurrentLiveRevision();

        if (liveRevision == null) {
            throw new ResourceNotFoundException("Current downloadable revision not found");
        }

        if (!StringUtils.hasText(liveRevision.getArchiveObjectKey())) {
            throw new IllegalArgumentException("No downloadable archive is attached to the current live revision");
        }

        OffsetDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC).plus(storageProperties.getDownloadUrlTtl());

        String url = storageService.createPresignedGetUrl(
                liveRevision.getArchiveObjectKey(),
                storageProperties.getDownloadUrlTtl()
        );

        return new BuyerDownloadLinkResponse(
                product.getId(),
                liveRevision.getId(),
                liveRevision.getArchiveOriginalFilename(),
                url,
                expiresAt
        );
    }

    @Transactional(readOnly = true)
    public List<BuyerPurchaseHistoryRowResponse> getPurchaseHistory(Long buyerUserId) {
        return customerOrderRepository.findAllByBuyer_IdOrderByCreatedAtDesc(buyerUserId)
                .stream()
                .filter(order -> !order.getItems().isEmpty())
                .map(order -> {
                    var item = order.getItems().get(0);
                    return new BuyerPurchaseHistoryRowResponse(
                            order.getId(),
                            String.valueOf(order.getId()),
                            item.getRevision().getTitle(),
                            item.getProduct().getSlug(),
                            order.getPaidAt() != null ? order.getPaidAt() : order.getCreatedAt(),
                            order.getStatus().name()
                    );
                })
                .toList();
    }

    private BuyerOrderResponse toOrderResponse(CustomerOrder order) {
        List<BuyerOrderItemResponse> items = order.getItems().stream()
                .map(item -> new BuyerOrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getSlug(),
                        item.getRevision().getId(),
                        item.getRevision().getTitle(),
                        item.getPriceMinor(),
                        order.getCurrency(),
                        item.getSellerProfile().getPublicName()
                ))
                .toList();

        return new BuyerOrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getSubtotalMinor(),
                order.getCurrency(),
                order.getCreatedAt(),
                order.getPaidAt(),
                items
        );
    }
}