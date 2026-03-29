package com.yarixer.marketplace.buyer.api;

import java.time.LocalDateTime;

public record BuyerLibraryItemResponse(
        Long entitlementId,
        Long productId,
        Long currentLiveRevisionId,
        String slug,
        String title,
        String shortDescription,
        long priceMinor,
        String currency,
        String sellerPublicName,
        boolean archiveAttached,
        boolean downloadAvailable,
        LocalDateTime grantedAt
) {
}