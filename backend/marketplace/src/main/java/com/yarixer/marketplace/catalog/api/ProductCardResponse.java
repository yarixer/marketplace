package com.yarixer.marketplace.catalog.api;

public record ProductCardResponse(
        Long productId,
        String slug,
        String title,
        String shortDescription,
        long priceMinor,
        String currency,
        String sellerName,
        String coverImageUrl,
        long ratingScore,
        long positiveVotes,
        long negativeVotes
) {
}