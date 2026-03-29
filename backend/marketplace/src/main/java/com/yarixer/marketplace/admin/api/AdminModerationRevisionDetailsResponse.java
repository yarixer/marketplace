package com.yarixer.marketplace.admin.api;

import java.time.OffsetDateTime;
import java.util.List;

public record AdminModerationRevisionDetailsResponse(
        Long revisionId,
        Long productId,
        String productSlug,
        String productState,
        String sellerPublicName,
        int revisionNumber,
        String status,
        String title,
        String shortDescription,
        String description,
        long priceMinor,
        String currency,
        List<AdminModerationTagResponse> tags,
        OffsetDateTime submittedAt,
        OffsetDateTime reviewedAt,
        Long reviewedByUserId,
        String rejectionReason,
        boolean archiveAttached,
        Long currentLiveRevisionId
) {
}