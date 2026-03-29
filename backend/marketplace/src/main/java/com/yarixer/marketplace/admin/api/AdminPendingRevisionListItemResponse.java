package com.yarixer.marketplace.admin.api;

import java.time.OffsetDateTime;

public record AdminPendingRevisionListItemResponse(
        Long revisionId,
        Long productId,
        String productSlug,
        String sellerPublicName,
        int revisionNumber,
        String title,
        long priceMinor,
        String currency,
        OffsetDateTime submittedAt
) {
}