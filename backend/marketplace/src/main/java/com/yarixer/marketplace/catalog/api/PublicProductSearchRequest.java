package com.yarixer.marketplace.catalog.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicProductSearchRequest {

    @Size(max = 100, message = "q must be at most 100 characters")
    private String q;

    private List<
            @Pattern(
                    regexp = "^[a-z0-9-]{1,80}$",
                    message = "tag must match slug format"
            )
                    String
            > tags = new ArrayList<>();

    @Pattern(
            regexp = "^(relevance|price_down|price_up|rating)$",
            message = "sort must be one of relevance, price_down, price_up, rating"
    )
    private String sort = "relevance";

    @Min(value = 0, message = "page must be >= 0")
    private int page = 0;

    @Min(value = 1, message = "size must be >= 1")
    @Max(value = 60, message = "size must be <= 60")
    private int size = 20;
}