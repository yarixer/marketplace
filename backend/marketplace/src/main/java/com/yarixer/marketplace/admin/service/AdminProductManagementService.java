package com.yarixer.marketplace.admin.service;

import com.yarixer.marketplace.admin.api.*;
import com.yarixer.marketplace.catalog.domain.Product;
import com.yarixer.marketplace.catalog.domain.ProductRevision;
import com.yarixer.marketplace.catalog.domain.ProductRevisionImage;
import com.yarixer.marketplace.catalog.domain.ProductRevisionStatus;
import com.yarixer.marketplace.catalog.domain.ProductState;
import com.yarixer.marketplace.catalog.repository.ProductRepository;
import com.yarixer.marketplace.catalog.repository.ProductRevisionImageRepository;
import com.yarixer.marketplace.catalog.repository.ProductRevisionRepository;
import com.yarixer.marketplace.common.exception.ResourceNotFoundException;
import com.yarixer.marketplace.storage.StorageProperties;
import com.yarixer.marketplace.storage.StorageService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductManagementService {

    private final ProductRepository productRepository;
    private final ProductRevisionRepository productRevisionRepository;
    private final ProductRevisionImageRepository productRevisionImageRepository;
    private final StorageService storageService;
    private final StorageProperties storageProperties;

    @Transactional(readOnly = true)
    public List<AdminProductListItemResponse> listProducts() {
        return productRepository.findAllByOrderByIdDesc().stream()
                .map(product -> {
                    List<ProductRevision> revisions = productRevisionRepository
                            .findByProduct_IdOrderByRevisionNumberDesc(product.getId());

                    ProductRevision draft = findByStatus(revisions, ProductRevisionStatus.DRAFT);
                    ProductRevision pending = findByStatus(revisions, ProductRevisionStatus.PENDING_REVIEW);
                    ProductRevision live = product.getCurrentLiveRevision();

                    String title = draft != null
                            ? draft.getTitle()
                            : pending != null
                            ? pending.getTitle()
                            : live != null
                            ? live.getTitle()
                            : "(untitled)";

                    String workflowStatus = draft != null
                            ? "DRAFT"
                            : pending != null
                            ? "PENDING_REVIEW"
                            : live != null
                            ? "LIVE"
                            : "EMPTY";

                    return new AdminProductListItemResponse(
                            product.getId(),
                            product.getSlug(),
                            product.getSellerProfile().getPublicName(),
                            product.getState().name(),
                            workflowStatus,
                            title
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminProductDetailsResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<AdminProductRevisionDetailsResponse> revisions = productRevisionRepository
                .findByProduct_IdOrderByRevisionNumberDesc(productId)
                .stream()
                .map(this::toRevisionDetails)
                .toList();

        return new AdminProductDetailsResponse(
                product.getId(),
                product.getSlug(),
                product.getState().name(),
                product.getSellerProfile().getPublicName(),
                product.getCurrentLiveRevision() != null ? product.getCurrentLiveRevision().getId() : null,
                revisions
        );
    }

    public AdminProductDetailsResponse updateProductState(Long productId, AdminProductStateUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setState(ProductState.valueOf(request.state()));
        productRepository.save(product);

        return getProduct(productId);
    }

    private AdminProductRevisionDetailsResponse toRevisionDetails(ProductRevision revision) {
        List<AdminRevisionImageResponse> images = productRevisionImageRepository
                .findAllByRevision_IdOrderBySortOrderAscIdAsc(revision.getId())
                .stream()
                .map(this::toImageResponse)
                .toList();

        String archiveUrl = StringUtils.hasText(revision.getArchiveObjectKey())
                ? storageService.createPresignedGetUrl(
                revision.getArchiveObjectKey(),
                storageProperties.getDownloadUrlTtl()
        )
                : null;

        return new AdminProductRevisionDetailsResponse(
                revision.getId(),
                revision.getRevisionNumber(),
                revision.getStatus().name(),
                revision.getTitle(),
                revision.getShortDescription(),
                revision.getPriceMinor(),
                revision.getCurrency(),
                revision.getArchiveOriginalFilename(),
                revision.getArchiveSizeBytes(),
                StringUtils.hasText(revision.getArchiveObjectKey()),
                archiveUrl,
                images,
                revision.getSubmittedAt(),
                revision.getReviewedAt(),
                revision.getRejectionReason()
        );
    }

    private AdminRevisionImageResponse toImageResponse(ProductRevisionImage image) {
        return new AdminRevisionImageResponse(
                image.getId(),
                image.getOriginalFilename(),
                image.getMimeType(),
                image.getSizeBytes(),
                image.getSortOrder(),
                image.isCover(),
                storageService.createPresignedGetUrl(
                        image.getObjectKey(),
                        storageProperties.getImageUrlTtl()
                )
        );
    }

    private ProductRevision findByStatus(List<ProductRevision> revisions, ProductRevisionStatus status) {
        return revisions.stream()
                .filter(revision -> revision.getStatus() == status)
                .max(Comparator.comparingInt(ProductRevision::getRevisionNumber))
                .orElse(null);
    }
}