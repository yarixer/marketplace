package com.yarixer.marketplace.buyer.api;

import java.time.OffsetDateTime;

public record BuyerDownloadLinkResponse(
        Long productId,
        Long revisionId,
        String filename,
        String url,
        OffsetDateTime expiresAt
) {
}