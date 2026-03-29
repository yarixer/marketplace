package com.yarixer.marketplace.seller.api;

import java.time.OffsetDateTime;
import java.util.List;

public record SellerProductRevisionResponse(
        Long revisionId,
        int revisionNumber,
        String status,
        String title,
        String shortDescription,
        String description,
        long priceMinor,
        String currency,
        List<SellerProductTagResponse> tags,
        String archiveOriginalFilename,
        Long archiveSizeBytes,
        boolean archiveAttached,
        List<SellerProductImageResponse> images,
        OffsetDateTime submittedAt,
        OffsetDateTime reviewedAt,
        String rejectionReason
) {
}