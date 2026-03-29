package com.yarixer.marketplace.catalog.repository;

import com.yarixer.marketplace.catalog.api.ProductCardBaseResponse;
import java.util.List;

public interface ProductCatalogQueryRepository {
    List<ProductCardBaseResponse> searchPublicProductCardBases(
            String q,
            List<String> tagSlugs
    );
}