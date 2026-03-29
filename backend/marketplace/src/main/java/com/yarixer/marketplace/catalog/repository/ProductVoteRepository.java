package com.yarixer.marketplace.catalog.repository;

import com.yarixer.marketplace.catalog.api.PublicProductVoteSummaryResponse;
import com.yarixer.marketplace.catalog.domain.ProductVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductVoteRepository extends JpaRepository<ProductVote, Long> {

    @Query("""
        select new com.yarixer.marketplace.catalog.api.PublicProductVoteSummaryResponse(
            coalesce(sum(case when pv.voteValue = 1 then 1 else 0 end), 0),
            coalesce(sum(case when pv.voteValue = -1 then 1 else 0 end), 0),
            coalesce(sum(pv.voteValue), 0)
        )
        from ProductVote pv
        where pv.product.id = :productId
    """)
    PublicProductVoteSummaryResponse getVoteSummaryByProductId(Long productId);
}