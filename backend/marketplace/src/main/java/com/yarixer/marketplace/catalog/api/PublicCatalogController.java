package com.yarixer.marketplace.catalog.api;

import com.yarixer.marketplace.catalog.service.PublicCatalogService;
import com.yarixer.marketplace.common.api.PageResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Validated
public class PublicCatalogController {

    private final PublicCatalogService publicCatalogService;

    @GetMapping("/products/cards")
    public List<ProductCardResponse> getCards() {
        return publicCatalogService.getProductCards();
    }

    @GetMapping("/products")
    public PageResponse<ProductCardResponse> searchProducts(
            @Valid @ModelAttribute PublicProductSearchRequest request
    ) {
        return publicCatalogService.searchProducts(request);
    }

    @GetMapping("/products/{slug}")
    public PublicProductDetailsResponse getProductBySlug(@PathVariable String slug) {
        return publicCatalogService.getProductBySlug(slug);
    }

    @GetMapping("/tags")
    public List<TagFilterResponse> getTags() {
        return publicCatalogService.getPublicTags();
    }
}