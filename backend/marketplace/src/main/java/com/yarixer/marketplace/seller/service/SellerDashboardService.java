package com.yarixer.marketplace.seller.service;

import com.yarixer.marketplace.catalog.domain.ProductRevisionStatus;
import com.yarixer.marketplace.catalog.domain.ProductState;
import com.yarixer.marketplace.catalog.repository.ProductRepository;
import com.yarixer.marketplace.catalog.repository.ProductRevisionRepository;
import com.yarixer.marketplace.order.domain.OrderStatus;
import com.yarixer.marketplace.order.repository.OrderItemRepository;
import com.yarixer.marketplace.seller.api.SellerDashboardResponse;
import com.yarixer.marketplace.wallet.api.WalletBalanceResponse;
import com.yarixer.marketplace.wallet.service.WalletLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerDashboardService {

    private final WalletLedgerService walletLedgerService;
    private final ProductRepository productRepository;
    private final ProductRevisionRepository productRevisionRepository;
    private final OrderItemRepository orderItemRepository;

    public SellerDashboardResponse getDashboard(Long sellerUserId) {
        WalletBalanceResponse wallet = walletLedgerService.getCurrentWallet(sellerUserId);

        long totalProducts = productRepository.countBySellerProfile_User_Id(sellerUserId);

        long publishedProducts = productRepository.countBySellerProfile_User_IdAndStateAndCurrentLiveRevision_Status(
                sellerUserId,
                ProductState.ACTIVE,
                ProductRevisionStatus.APPROVED
        );

        long pendingReviewCount = productRevisionRepository
                .countByProduct_SellerProfile_User_IdAndStatus(sellerUserId, ProductRevisionStatus.PENDING_REVIEW);

        long totalSalesCount = orderItemRepository
                .countBySellerProfile_User_IdAndOrder_Status(sellerUserId, OrderStatus.PAID);

        long grossRevenueMinor = orderItemRepository
                .sumRevenueBySellerUserIdAndOrderStatus(sellerUserId, OrderStatus.PAID);

        return new SellerDashboardResponse(
                wallet,
                totalProducts,
                publishedProducts,
                pendingReviewCount,
                totalSalesCount,
                grossRevenueMinor
        );
    }
}