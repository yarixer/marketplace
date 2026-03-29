package com.yarixer.marketplace.seller.api;

import com.yarixer.marketplace.wallet.api.WalletBalanceResponse;

public record SellerDashboardResponse(
        WalletBalanceResponse wallet,
        long totalProducts,
        long publishedProducts,
        long pendingReviewCount,
        long totalSalesCount,
        long grossRevenueMinor
) {
}