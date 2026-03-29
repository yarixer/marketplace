package com.yarixer.marketplace.catalog.api;

public record PublicProductTagResponse(
        Long id,
        String name,
        String slug
) {
}