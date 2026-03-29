package com.yarixer.marketplace.seller.api;

public record SellerProductSubmitReviewResponse(
        Long productId,
        Long revisionId,
        String status
) {
}