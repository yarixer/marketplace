package com.yarixer.marketplace.catalog.repository;

import com.yarixer.marketplace.catalog.domain.ProductRevisionImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRevisionImageRepository extends JpaRepository<ProductRevisionImage, Long> {

    List<ProductRevisionImage> findAllByRevision_IdOrderBySortOrderAscIdAsc(Long revisionId);

    Optional<ProductRevisionImage> findByIdAndRevision_Id(Long imageId, Long revisionId);

    Optional<ProductRevisionImage> findFirstByRevision_IdOrderByCoverDescSortOrderAscIdAsc(Long revisionId);
}