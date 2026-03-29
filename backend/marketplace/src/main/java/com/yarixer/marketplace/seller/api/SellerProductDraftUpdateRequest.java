package com.yarixer.marketplace.seller.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SellerProductDraftUpdateRequest(
        @Size(max = 160)
        String title,

        @Size(max = 300)
        String shortDescription,

        @Size(max = 100000)
        String description,

        @Min(value = 0, message = "priceMinor must be >= 0")
        Long priceMinor,

        List<
                @Pattern(
                        regexp = "^[a-z0-9-]{1,80}$",
                        message = "tag slug must match slug format"
                )
                        String
                > tagSlugs
) {
}