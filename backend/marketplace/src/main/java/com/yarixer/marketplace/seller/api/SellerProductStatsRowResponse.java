package com.yarixer.marketplace.seller.api;

public record SellerProductStatsRowResponse(
        Long productId,
        String slug,
        String title,
        String workflowStatus,
        long currentPriceMinor,
        long salesCount,
        long grossRevenueMinor,
        long ratingScore,
        long positiveVotes,
        long negativeVotes
) {
}