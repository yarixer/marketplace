package com.yarixer.marketplace.catalog.api;

public record PublicProductImageResponse(
        String url,
        int sortOrder,
        boolean cover
) {
}