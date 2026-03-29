package com.yarixer.marketplace.seller.api;

import com.yarixer.marketplace.catalog.api.PublicProductVoteSummaryResponse;

public record SellerProductDetailsResponse(
        Long productId,
        String slug,
        String state,
        SellerProductRevisionResponse draftRevision,
        SellerProductRevisionResponse pendingRevision,
        SellerProductRevisionResponse liveRevision,
        PublicProductVoteSummaryResponse voteSummary
) {
}