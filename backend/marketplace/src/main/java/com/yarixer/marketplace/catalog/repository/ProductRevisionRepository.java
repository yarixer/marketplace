package com.yarixer.marketplace.catalog.repository;

import com.yarixer.marketplace.catalog.domain.ProductRevision;
import com.yarixer.marketplace.catalog.domain.ProductRevisionStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRevisionRepository extends JpaRepository<ProductRevision, Long> {

    @EntityGraph(attributePaths = {"tags"})
    List<ProductRevision> findByProduct_IdOrderByRevisionNumberDesc(Long productId);

    @EntityGraph(attributePaths = {"tags"})
    Optional<ProductRevision> findFirstByProduct_IdOrderByRevisionNumberDesc(Long productId);

    @EntityGraph(attributePaths = {"product", "product.sellerProfile", "tags"})
    List<ProductRevision> findAllByStatusOrderBySubmittedAtAsc(ProductRevisionStatus status);

    @EntityGraph(attributePaths = {"product", "product.sellerProfile", "tags"})
    Optional<ProductRevision> findByIdAndStatus(Long id, ProductRevisionStatus status);

    long countByProduct_SellerProfile_User_IdAndStatus(Long userId, ProductRevisionStatus status);
}