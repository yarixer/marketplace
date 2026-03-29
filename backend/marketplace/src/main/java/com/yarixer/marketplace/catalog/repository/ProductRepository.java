package com.yarixer.marketplace.catalog.repository;

import com.yarixer.marketplace.catalog.api.ProductCardBaseResponse;
import com.yarixer.marketplace.catalog.domain.Product;
import com.yarixer.marketplace.catalog.domain.ProductRevisionStatus;
import com.yarixer.marketplace.catalog.domain.ProductState;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository
        extends JpaRepository<Product, Long>, ProductCatalogQueryRepository {

    @Query("""
        select new com.yarixer.marketplace.catalog.api.ProductCardBaseResponse(
            p.id,
            r.id,
            p.slug,
            r.title,
            r.shortDescription,
            r.priceMinor,
            r.currency,
            s.publicName
        )
        from Product p
        join p.sellerProfile s
        join p.currentLiveRevision r
        where p.state = com.yarixer.marketplace.catalog.domain.ProductState.ACTIVE
          and r.status = com.yarixer.marketplace.catalog.domain.ProductRevisionStatus.APPROVED
        order by p.id desc
    """)
    List<ProductCardBaseResponse> findPublicProductCardBases();

    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = {"sellerProfile", "currentLiveRevision"})
    List<Product> findAllBySellerProfile_User_IdOrderByIdDesc(Long userId);

    @EntityGraph(attributePaths = {"sellerProfile", "currentLiveRevision"})
    Optional<Product> findByIdAndSellerProfile_User_Id(Long productId, Long userId);

    @EntityGraph(attributePaths = {
            "sellerProfile",
            "currentLiveRevision",
            "currentLiveRevision.tags"
    })
    Optional<Product> findBySlugAndStateAndCurrentLiveRevision_Status(
            String slug,
            ProductState state,
            ProductRevisionStatus status
    );

    @EntityGraph(attributePaths = {
            "sellerProfile",
            "currentLiveRevision",
            "currentLiveRevision.tags"
    })
    Optional<Product> findByIdAndStateAndCurrentLiveRevision_Status(
            Long id,
            ProductState state,
            ProductRevisionStatus status
    );

    long countBySellerProfile_User_Id(Long userId);

    long countBySellerProfile_User_IdAndStateAndCurrentLiveRevision_Status(
            Long userId,
            ProductState state,
            ProductRevisionStatus status
    );

    @EntityGraph(attributePaths = {"sellerProfile", "currentLiveRevision"})
    List<Product> findAllByOrderByIdDesc();
}