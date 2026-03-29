package com.yarixer.marketplace.catalog.repository;

import com.yarixer.marketplace.catalog.api.ProductCardBaseResponse;
import com.yarixer.marketplace.catalog.domain.ProductRevisionStatus;
import com.yarixer.marketplace.catalog.domain.ProductState;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class ProductCatalogQueryRepositoryImpl implements ProductCatalogQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ProductCardBaseResponse> searchPublicProductCardBases(String q, List<String> tagSlugs) {
        String normalizedQuery = normalizeQuery(q);
        List<String> normalizedTags = normalizeTags(tagSlugs);

        boolean filterByQuery = StringUtils.hasText(normalizedQuery);
        boolean filterByTags = !normalizedTags.isEmpty();

        StringBuilder jpql = new StringBuilder("""
            select distinct new com.yarixer.marketplace.catalog.api.ProductCardBaseResponse(
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
            left join r.tags t
            where p.state = :activeState
              and r.status = :approvedStatus
            """);

        if (filterByQuery) {
            jpql.append("""
                  and (
                      lower(r.title) like :titlePrefix
                      or lower(r.title) like :wordPrefix
                  )
                """);
        }

        if (filterByTags) {
            jpql.append("""
                  and t.slug in :tagSlugs
                """);
        }

        jpql.append("""
            order by p.id desc
            """);

        TypedQuery<ProductCardBaseResponse> query =
                entityManager.createQuery(jpql.toString(), ProductCardBaseResponse.class);

        query.setParameter("activeState", ProductState.ACTIVE);
        query.setParameter("approvedStatus", ProductRevisionStatus.APPROVED);

        if (filterByQuery) {
            query.setParameter("titlePrefix", normalizedQuery + "%");
            query.setParameter("wordPrefix", "% " + normalizedQuery + "%");
        }

        if (filterByTags) {
            query.setParameter("tagSlugs", normalizedTags);
        }

        return query.getResultList();
    }

    private String normalizeQuery(String q) {
        if (!StringUtils.hasText(q)) {
            return null;
        }
        return q.trim().toLowerCase();
    }

    private List<String> normalizeTags(List<String> tagSlugs) {
        if (tagSlugs == null || tagSlugs.isEmpty()) {
            return List.of();
        }

        return tagSlugs.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .toList();
    }
}