package com.yarixer.marketplace.tag.repository;

import com.yarixer.marketplace.catalog.api.TagFilterResponse;
import com.yarixer.marketplace.catalog.domain.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("""
        select new com.yarixer.marketplace.catalog.api.TagFilterResponse(
            t.id,
            t.name,
            t.slug
        )
        from Tag t
        order by t.name asc
    """)
    List<TagFilterResponse> findPublicFilterTags();
}