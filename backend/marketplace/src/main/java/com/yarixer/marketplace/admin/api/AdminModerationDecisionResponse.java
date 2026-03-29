package com.yarixer.marketplace.admin.api;

import java.time.OffsetDateTime;

public record AdminModerationDecisionResponse(
        Long revisionId,
        Long productId,
        String status,
        OffsetDateTime reviewedAt,
        Long reviewedByUserId
) {
}