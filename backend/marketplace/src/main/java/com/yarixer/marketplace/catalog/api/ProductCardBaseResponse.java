package com.yarixer.marketplace.catalog.api;

public record ProductCardBaseResponse(
        Long productId,
        Long revisionId,
        String slug,
        String title,
        String shortDescription,
        long priceMinor,
        String currency,
        String sellerName
) {
}