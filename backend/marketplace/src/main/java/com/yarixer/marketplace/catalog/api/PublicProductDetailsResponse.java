package com.yarixer.marketplace.catalog.api;

import java.util.List;

public record PublicProductDetailsResponse(
        Long productId,
        Long currentLiveRevisionId,
        String slug,
        String title,
        String shortDescription,
        String description,
        long priceMinor,
        String currency,
        PublicProductSellerResponse seller,
        List<PublicProductTagResponse> tags,
        List<PublicProductImageResponse> images,
        PublicProductVoteSummaryResponse voteSummary,
        boolean archiveAttached
) {
}