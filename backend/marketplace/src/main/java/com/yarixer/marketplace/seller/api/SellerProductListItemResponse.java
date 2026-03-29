package com.yarixer.marketplace.seller.api;

import java.time.LocalDateTime;

public record SellerProductListItemResponse(
        Long productId,
        String slug,
        String title,
        String workflowStatus,
        LocalDateTime updatedAt,
        long ratingScore,
        long positiveVotes,
        long negativeVotes
) {
}