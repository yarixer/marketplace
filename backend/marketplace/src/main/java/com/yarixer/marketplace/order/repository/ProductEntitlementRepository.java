package com.yarixer.marketplace.order.repository;

import com.yarixer.marketplace.order.domain.ProductEntitlement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductEntitlementRepository extends JpaRepository<ProductEntitlement, Long> {

    boolean existsByBuyer_IdAndProduct_Id(Long buyerId, Long productId);

    Optional<ProductEntitlement> findByBuyer_IdAndProduct_Id(Long buyerId, Long productId);

    @EntityGraph(attributePaths = {
            "product",
            "product.sellerProfile",
            "product.currentLiveRevision"
    })
    List<ProductEntitlement> findAllByBuyer_IdOrderByCreatedAtDesc(Long buyerId);
}