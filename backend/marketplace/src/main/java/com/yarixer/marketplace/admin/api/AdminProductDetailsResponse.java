package com.yarixer.marketplace.admin.api;

import java.util.List;

public record AdminProductDetailsResponse(
        Long productId,
        String slug,
        String state,
        String sellerPublicName,
        Long currentLiveRevisionId,
        List<AdminProductRevisionDetailsResponse> revisions
) {
}