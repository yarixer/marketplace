package com.yarixer.marketplace.catalog.service;

import com.yarixer.marketplace.catalog.api.*;
import com.yarixer.marketplace.catalog.domain.Product;
import com.yarixer.marketplace.catalog.domain.ProductRevision;
import com.yarixer.marketplace.catalog.domain.ProductRevisionStatus;
import com.yarixer.marketplace.catalog.domain.ProductState;
import com.yarixer.marketplace.catalog.domain.Tag;
import com.yarixer.marketplace.catalog.repository.ProductRepository;
import com.yarixer.marketplace.catalog.repository.ProductRevisionImageRepository;
import com.yarixer.marketplace.catalog.repository.ProductVoteRepository;
import com.yarixer.marketplace.common.api.PageResponse;
import com.yarixer.marketplace.common.exception.ResourceNotFoundException;
import com.yarixer.marketplace.storage.StorageProperties;
import com.yarixer.marketplace.storage.StorageService;
import com.yarixer.marketplace.tag.repository.TagRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCatalogService {

    private final ProductRepository productRepository;
    private final ProductVoteRepository productVoteRepository;
    private final ProductRevisionImageRepository productRevisionImageRepository;
    private final StorageService storageService;
    private final StorageProperties storageProperties;
    private final TagRepository tagRepository;

    public List<ProductCardResponse> getProductCards() {
        return enrichCards(productRepository.findPublicProductCardBases());
    }

    public PageResponse<ProductCardResponse> searchProducts(PublicProductSearchRequest request) {
        List<ProductCardResponse> cards = enrichCards(
                productRepository.searchPublicProductCardBases(request.getQ(), request.getTags())
        );

        List<ProductCardResponse> sorted = sortCards(cards, request.getSort());

        int fromIndex = Math.min(request.getPage() * request.getSize(), sorted.size());
        int toIndex = Math.min(fromIndex + request.getSize(), sorted.size());
        List<ProductCardResponse> pageItems = sorted.subList(fromIndex, toIndex);

        int totalPages = sorted.isEmpty() ? 0 : (int) Math.ceil((double) sorted.size() / request.getSize());

        return new PageResponse<>(
                pageItems,
                request.getPage(),
                request.getSize(),
                sorted.size(),
                totalPages,
                request.getPage() == 0,
                request.getPage() >= Math.max(totalPages - 1, 0)
        );
    }

    public List<TagFilterResponse> getPublicTags() {
        return tagRepository.findPublicFilterTags();
    }

    public PublicProductDetailsResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlugAndStateAndCurrentLiveRevision_Status(
                        slug,
                        ProductState.ACTIVE,
                        ProductRevisionStatus.APPROVED
                )
                .orElseThrow(() -> new ResourceNotFoundException("Published product not found"));

        ProductRevision liveRevision = product.getCurrentLiveRevision();

        PublicProductVoteSummaryResponse voteSummary =
                productVoteRepository.getVoteSummaryByProductId(product.getId());

        List<PublicProductTagResponse> tags = liveRevision.getTags().stream()
                .sorted(Comparator.comparing(Tag::getName))
                .map(tag -> new PublicProductTagResponse(tag.getId(), tag.getName(), tag.getSlug()))
                .toList();

        List<PublicProductImageResponse> images = productRevisionImageRepository
                .findAllByRevision_IdOrderBySortOrderAscIdAsc(liveRevision.getId())
                .stream()
                .map(image -> new PublicProductImageResponse(
                        storageService.createPresignedGetUrl(
                                image.getObjectKey(),
                                storageProperties.getImageUrlTtl()
                        ),
                        image.getSortOrder(),
                        image.isCover()
                ))
                .toList();

        return new PublicProductDetailsResponse(
                product.getId(),
                liveRevision.getId(),
                product.getSlug(),
                liveRevision.getTitle(),
                liveRevision.getShortDescription(),
                liveRevision.getDescription(),
                liveRevision.getPriceMinor(),
                liveRevision.getCurrency(),
                new PublicProductSellerResponse(
                        product.getSellerProfile().getPublicName(),
                        product.getSellerProfile().getSlug()
                ),
                tags,
                images,
                voteSummary,
                StringUtils.hasText(liveRevision.getArchiveObjectKey())
        );
    }

    private List<ProductCardResponse> enrichCards(List<ProductCardBaseResponse> baseCards) {
        return baseCards.stream()
                .map(base -> {
                    String coverImageUrl = productRevisionImageRepository
                            .findFirstByRevision_IdOrderByCoverDescSortOrderAscIdAsc(base.revisionId())
                            .map(image -> storageService.createPresignedGetUrl(
                                    image.getObjectKey(),
                                    storageProperties.getImageUrlTtl()
                            ))
                            .orElse(null);

                    PublicProductVoteSummaryResponse voteSummary =
                            productVoteRepository.getVoteSummaryByProductId(base.productId());

                    return new ProductCardResponse(
                            base.productId(),
                            base.slug(),
                            base.title(),
                            base.shortDescription(),
                            base.priceMinor(),
                            base.currency(),
                            base.sellerName(),
                            coverImageUrl,
                            voteSummary.score(),
                            voteSummary.positiveCount(),
                            voteSummary.negativeCount()
                    );
                })
                .toList();
    }

    private List<ProductCardResponse> sortCards(List<ProductCardResponse> cards, String sort) {
        return switch (sort) {
            case "price_up" -> cards.stream()
                    .sorted(Comparator.comparingLong(ProductCardResponse::priceMinor)
                            .thenComparing(ProductCardResponse::productId))
                    .toList();

            case "price_down" -> cards.stream()
                    .sorted(Comparator.comparingLong(ProductCardResponse::priceMinor).reversed()
                            .thenComparing(ProductCardResponse::productId))
                    .toList();

            case "rating" -> cards.stream()
                    .sorted(Comparator.comparingLong(ProductCardResponse::ratingScore).reversed()
                            .thenComparingLong(ProductCardResponse::positiveVotes).reversed()
                            .thenComparing(ProductCardResponse::productId))
                    .toList();

            default -> cards;
        };
    }
}