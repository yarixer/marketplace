package com.yarixer.marketplace.admin.api;

import java.time.OffsetDateTime;
import java.util.List;

public record AdminProductRevisionDetailsResponse(
        Long revisionId,
        int revisionNumber,
        String status,
        String title,
        String shortDescription,
        long priceMinor,
        String currency,
        String archiveOriginalFilename,
        Long archiveSizeBytes,
        boolean archiveAttached,
        String archiveUrl,
        List<AdminRevisionImageResponse> images,
        OffsetDateTime submittedAt,
        OffsetDateTime reviewedAt,
        String rejectionReason
) {
}