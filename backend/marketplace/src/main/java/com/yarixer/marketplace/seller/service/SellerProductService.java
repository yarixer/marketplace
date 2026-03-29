package com.yarixer.marketplace.seller.service;

import com.yarixer.marketplace.catalog.domain.Product;
import com.yarixer.marketplace.catalog.domain.ProductRevision;
import com.yarixer.marketplace.catalog.domain.ProductRevisionImage;
import com.yarixer.marketplace.catalog.domain.ProductRevisionStatus;
import com.yarixer.marketplace.catalog.domain.ProductState;
import com.yarixer.marketplace.catalog.domain.Tag;
import com.yarixer.marketplace.catalog.repository.ProductRepository;
import com.yarixer.marketplace.catalog.repository.ProductRevisionImageRepository;
import com.yarixer.marketplace.catalog.repository.ProductRevisionRepository;
import com.yarixer.marketplace.common.exception.ResourceNotFoundException;
import com.yarixer.marketplace.common.util.SlugUtils;
import com.yarixer.marketplace.seller.api.SellerProductCreateRequest;
import com.yarixer.marketplace.seller.api.SellerProductDetailsResponse;
import com.yarixer.marketplace.seller.api.SellerProductDraftUpdateRequest;
import com.yarixer.marketplace.seller.api.SellerProductImageResponse;
import com.yarixer.marketplace.seller.api.SellerProductListItemResponse;
import com.yarixer.marketplace.seller.api.SellerProductRevisionResponse;
import com.yarixer.marketplace.seller.api.SellerProductSubmitReviewResponse;
import com.yarixer.marketplace.seller.api.SellerProductTagResponse;
import com.yarixer.marketplace.seller.domain.SellerProfile;
import com.yarixer.marketplace.seller.repository.SellerProfileRepository;
import com.yarixer.marketplace.storage.StorageProperties;
import com.yarixer.marketplace.storage.StorageService;
import com.yarixer.marketplace.tag.repository.TagRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.yarixer.marketplace.catalog.api.PublicProductVoteSummaryResponse;
import com.yarixer.marketplace.catalog.repository.ProductVoteRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerProductService {

    private final ProductRepository productRepository;
    private final ProductRevisionRepository productRevisionRepository;
    private final ProductRevisionImageRepository productRevisionImageRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final TagRepository tagRepository;
    private final StorageService storageService;
    private final StorageProperties storageProperties;
    private final ProductVoteRepository productVoteRepository;

    public SellerProductDetailsResponse createProduct(Long userId, SellerProductCreateRequest request) {
        SellerProfile sellerProfile = sellerProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));

        Product product = new Product();
        product.setSellerProfile(sellerProfile);
        product.setSlug(generateUniqueProductSlug(request.title()));
        product.setState(ProductState.ACTIVE);
        product = productRepository.save(product);

        ProductRevision draft = new ProductRevision();
        draft.setProduct(product);
        draft.setRevisionNumber(1);
        draft.setStatus(ProductRevisionStatus.DRAFT);
        draft.setTitle(request.title().trim());
        draft.setShortDescription("");
        draft.setDescription("");
        draft.setPriceMinor(0L);
        draft.setCurrency("USD");
        draft.setArchiveObjectKey(null);
        draft.setArchiveOriginalFilename(null);
        draft = productRevisionRepository.save(draft);

        return toDetailsResponse(product, List.of(draft));
    }

    @Transactional(readOnly = true)
    public List<SellerProductListItemResponse> listProducts(Long userId) {
        List<Product> products = productRepository.findAllBySellerProfile_User_IdOrderByIdDesc(userId);

        return products.stream()
                .map(product -> {
                    List<ProductRevision> revisions = productRevisionRepository.findByProduct_IdOrderByRevisionNumberDesc(product.getId());

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
                            ? ProductRevisionStatus.DRAFT.name()
                            : pending != null
                            ? ProductRevisionStatus.PENDING_REVIEW.name()
                            : live != null
                            ? "LIVE"
                            : "EMPTY";

                    PublicProductVoteSummaryResponse votes =
                            productVoteRepository.getVoteSummaryByProductId(product.getId());

                    return new SellerProductListItemResponse(
                            product.getId(),
                            product.getSlug(),
                            title,
                            workflowStatus,
                            product.getUpdatedAt(),
                            votes.score(),
                            votes.positiveCount(),
                            votes.negativeCount()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public SellerProductDetailsResponse getProductDetails(Long userId, Long productId) {
        Product product = getOwnedProduct(userId, productId);
        List<ProductRevision> revisions = productRevisionRepository.findByProduct_IdOrderByRevisionNumberDesc(product.getId());
        return toDetailsResponse(product, revisions);
    }

    public SellerProductDetailsResponse updateDraft(
            Long userId,
            Long productId,
            SellerProductDraftUpdateRequest request
    ) {
        Product product = getOwnedProduct(userId, productId);
        List<ProductRevision> revisions = productRevisionRepository.findByProduct_IdOrderByRevisionNumberDesc(product.getId());

        ProductRevision draft = getOrCreateEditableDraft(product, revisions);
        applyDraftUpdate(draft, request);
        productRevisionRepository.save(draft);

        List<ProductRevision> updatedRevisions = productRevisionRepository.findByProduct_IdOrderByRevisionNumberDesc(product.getId());
        return toDetailsResponse(product, updatedRevisions);
    }

    public SellerProductDetailsResponse uploadArchive(Long userId, Long productId, MultipartFile file) {
        validateArchiveFile(file);

        Product product = getOwnedProduct(userId, productId);
        List<ProductRevision> revisions = productRevisionRepository.findByProduct_IdOrderByRevisionNumberDesc(product.getId());

        ProductRevision draft = getOrCreateEditableDraft(product, revisions);

        String previousObjectKey = draft.getArchiveObjectKey();
        String objectKey = buildArchiveObjectKey(draft.getId(), file.getOriginalFilename());

        try {
            storageService.putObject(
                    objectKey,
                    file.getInputStream(),
                    file.getSize(),
                    contentTypeOrDefault(file.getContentType(), "application/zip")
            );
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to read uploaded archive", ex);
        }

        draft.setArchiveObjectKey(objectKey);
        draft.setArchiveOriginalFilename(file.getOriginalFilename());
        draft.setArchiveSizeBytes(file.getSize());
        draft.setChecksumSha256(null);
        productRevisionRepository.save(draft);

        if (previousObjectKey != null && !previousObjectKey.equals(objectKey)) {
            storageService.deleteObject(previousObjectKey);
        }

        List<ProductRevision> updatedRevisions = productRevisionRepository.findByProduct_IdOrderByRevisionNumberDesc(product.getId());
        return toDetailsResponse(product, updatedRevisions);
    }

    public SellerProductDetailsResponse uploadImages(Long userId, Long productId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("At least one image file is required");
        }

        Product product = getOwnedProduct(userId, productId);
        List<ProductRevision> revisions = productRevisionRepository.findByProduct_IdOrderByRevisionNumberDesc(product.getId());

        ProductRevision draft = getOrCreateEditableDraft(product, revisions);

        List<ProductRevisionImage> existingImages =
                productRevisionImageRepository.findAllByRevision_IdOrderBySortOrderAscIdAsc(draft.getId());

        boolean hasCover = existingImages.stream().anyMatch(ProductRevisionImage::isCover);
        int nextSortOrder = existingImages.stream()
                .mapToInt(ProductRevisionImage::getSortOrder)
                .max()
                .orElse(-1) + 1;

        boolean firstNewShouldBeCover = !hasCover;

        for (MultipartFile file : files) {
            validateImageFile(file);

            String objectKey = buildImageObjectKey(draft.getId(), file.getOriginalFilename());

            try {
                storageService.putObject(
                        objectKey,
                        file.getInputStream(),
                        file.getSize(),
                        contentTypeOrDefault(file.getContentType(), "application/octet-stream")
                );
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to read uploaded image", ex);
            }

            ProductRevisionImage image = new ProductRevisionImage();
            image.setRevision(draft);
            image.setObjectKey(objectKey);
            image.setOriginalFilename(file.getOriginalFilename());
            image.setMimeType(contentTypeOrDefault(file.getContentType(), "application/octet-stream"));
            image.setSizeBytes(file.getSize());
            image.setSortOrder(nextSortOrder++);
            image.setCover(firstNewShouldBeCover);

            productRevisionImageRepository.save(image);
            firstNewShouldBeCover = false;
        }

        List<ProductRevision> updatedRevisions = productRevisionRepository.findByProduct_IdOrderByRevisionNumberDesc(product.getId());
        return toDetailsResponse(product, updatedRevisions);
    }

    public SellerProductDetailsResponse deleteDraftImage(Long userId, Long productId, Long imageId) {
        Product product = getOwnedProduct(userId, productId);
        List<ProductRevision> revisions = productRevisionRepository.findByProduct_IdOrderByRevisionNumberDesc(product.getId());

        ProductRevision draft = findByStatus(revisions, ProductRevisionStatus.DRAFT);
        if (draft == null) {
            throw new IllegalArgumentException("No editable draft revision found");
        }

        ProductRevisionImage image = productRevisionImageRepository.findByIdAndRevision_Id(imageId, draft.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Draft image not found"));

        boolean deletedCover = image.isCover();
        String objectKey = image.getObjectKey();

        productRevisionImageRepository.delete(image);
        storageService.deleteObject(objectKey);

        if (deletedCover) {
            List<ProductRevisionImage> remaining =
                    productRevisionImageRepository.findAllByRevision_IdOrderBySortOrderAscIdAsc(draft.getId());

            if (!remaining.isEmpty() && remaining.stream().noneMatch(ProductRevisionImage::isCover)) {
                ProductRevisionImage first = remaining.get(0);
                first.setCover(true);
                productRevisionImageRepository.save(first);
            }
        }

        List<ProductRevision> updatedRevisions = productRevisionRepository.findByProduct_IdOrderByRevisionNumberDesc(product.getId());
        return toDetailsResponse(product, updatedRevisions);
    }

    public SellerProductSubmitReviewResponse submitReview(Long userId, Long productId) {
        Product product = getOwnedProduct(userId, productId);
        List<ProductRevision> revisions = productRevisionRepository.findByProduct_IdOrderByRevisionNumberDesc(product.getId());

        ProductRevision draft = findByStatus(revisions, ProductRevisionStatus.DRAFT);
        if (draft == null) {
            throw new IllegalArgumentException("No draft revision available for submission");
        }

        long pendingCount = productRevisionRepository.countByProduct_SellerProfile_User_IdAndStatus(userId, ProductRevisionStatus.PENDING_REVIEW);
        if (pendingCount >= 2) {
            throw new IllegalArgumentException("Pending review limit reached (max 2)");
        }

        validateDraftForReview(draft);

        draft.setStatus(ProductRevisionStatus.PENDING_REVIEW);
        draft.setSubmittedAt(OffsetDateTime.now(ZoneOffset.UTC));
        productRevisionRepository.save(draft);

        return new SellerProductSubmitReviewResponse(
                product.getId(),
                draft.getId(),
                draft.getStatus().name()
        );
    }

    private Product getOwnedProduct(Long userId, Long productId) {
        return productRepository.findByIdAndSellerProfile_User_Id(productId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private ProductRevision getOrCreateEditableDraft(Product product, List<ProductRevision> revisions) {
        ProductRevision draft = findByStatus(revisions, ProductRevisionStatus.DRAFT);
        if (draft != null) {
            return draft;
        }

        ProductRevision pending = findByStatus(revisions, ProductRevisionStatus.PENDING_REVIEW);
        if (pending != null) {
            throw new IllegalArgumentException("Product already has a revision pending review");
        }

        return createDraftFromBestSource(product, revisions);
    }

    private ProductRevision createDraftFromBestSource(Product product, List<ProductRevision> revisions) {
        ProductRevision source = product.getCurrentLiveRevision();

        if (source == null) {
            source = revisions.stream()
                    .filter(revision -> revision.getStatus() == ProductRevisionStatus.REJECTED)
                    .max(Comparator.comparingInt(ProductRevision::getRevisionNumber))
                    .orElse(null);
        }

        int nextRevisionNumber = revisions.stream()
                .mapToInt(ProductRevision::getRevisionNumber)
                .max()
                .orElse(0) + 1;

        ProductRevision draft = new ProductRevision();
        draft.setProduct(product);
        draft.setRevisionNumber(nextRevisionNumber);
        draft.setStatus(ProductRevisionStatus.DRAFT);
        draft.setCurrency("USD");

        if (source != null) {
            draft.setTitle(source.getTitle());
            draft.setShortDescription(source.getShortDescription());
            draft.setDescription(source.getDescription());
            draft.setPriceMinor(source.getPriceMinor());
            draft.setCurrency(source.getCurrency());
            draft.setTags(new HashSet<>(source.getTags()));
        } else {
            draft.setTitle("");
            draft.setShortDescription("");
            draft.setDescription("");
            draft.setPriceMinor(0L);
            draft.setTags(new HashSet<>());
        }

        draft.setArchiveObjectKey(null);
        draft.setArchiveOriginalFilename(null);
        draft.setArchiveSizeBytes(null);
        draft.setChecksumSha256(null);

        return productRevisionRepository.save(draft);
    }

    private void applyDraftUpdate(ProductRevision draft, SellerProductDraftUpdateRequest request) {
        if (request.title() != null) {
            draft.setTitle(request.title().trim());
        }

        if (request.shortDescription() != null) {
            draft.setShortDescription(request.shortDescription().trim());
        }

        if (request.description() != null) {
            draft.setDescription(request.description().trim());
        }

        if (request.priceMinor() != null) {
            draft.setPriceMinor(request.priceMinor());
        }

        if (request.tagSlugs() != null) {
            List<String> normalizedTagSlugs = request.tagSlugs().stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .distinct()
                    .toList();

            List<Tag> tags = normalizedTagSlugs.isEmpty()
                    ? List.of()
                    : tagRepository.findAll().stream()
                    .filter(tag -> normalizedTagSlugs.contains(tag.getSlug()))
                    .toList();

            if (tags.size() != normalizedTagSlugs.size()) {
                throw new IllegalArgumentException("One or more tags do not exist");
            }

            draft.setTags(new HashSet<>(tags));
        }
    }

    private void validateDraftForReview(ProductRevision draft) {
        if (!StringUtils.hasText(draft.getTitle())) {
            throw new IllegalArgumentException("Draft title is required before review");
        }

        if (!StringUtils.hasText(draft.getShortDescription())) {
            throw new IllegalArgumentException("Draft short description is required before review");
        }

        if (!StringUtils.hasText(draft.getDescription())) {
            throw new IllegalArgumentException("Draft description is required before review");
        }

        if (draft.getPriceMinor() <= 0) {
            throw new IllegalArgumentException("Draft price must be greater than 0 before review");
        }

        if (draft.getTags() == null || draft.getTags().isEmpty()) {
            throw new IllegalArgumentException("At least one tag is required before review");
        }
    }

    private SellerProductDetailsResponse toDetailsResponse(Product product, List<ProductRevision> revisions) {
        ProductRevision draft = findByStatus(revisions, ProductRevisionStatus.DRAFT);
        ProductRevision pending = findByStatus(revisions, ProductRevisionStatus.PENDING_REVIEW);
        ProductRevision live = product.getCurrentLiveRevision();
        PublicProductVoteSummaryResponse voteSummary =
                productVoteRepository.getVoteSummaryByProductId(product.getId());

        return new SellerProductDetailsResponse(
                product.getId(),
                product.getSlug(),
                product.getState().name(),
                toRevisionResponse(draft),
                toRevisionResponse(pending),
                toRevisionResponse(live),
                voteSummary
        );
    }

    private ProductRevision findByStatus(List<ProductRevision> revisions, ProductRevisionStatus status) {
        return revisions.stream()
                .filter(revision -> revision.getStatus() == status)
                .max(Comparator.comparingInt(ProductRevision::getRevisionNumber))
                .orElse(null);
    }

    private SellerProductRevisionResponse toRevisionResponse(ProductRevision revision) {
        if (revision == null) {
            return null;
        }

        List<SellerProductTagResponse> tags = revision.getTags().stream()
                .sorted(Comparator.comparing(Tag::getName))
                .map(tag -> new SellerProductTagResponse(tag.getId(), tag.getName(), tag.getSlug()))
                .toList();

        List<SellerProductImageResponse> images = productRevisionImageRepository
                .findAllByRevision_IdOrderBySortOrderAscIdAsc(revision.getId())
                .stream()
                .map(image -> new SellerProductImageResponse(
                        image.getId(),
                        storageService.createPresignedGetUrl(
                                image.getObjectKey(),
                                storageProperties.getImageUrlTtl()
                        ),
                        image.getOriginalFilename(),
                        image.getMimeType(),
                        image.getSortOrder(),
                        image.isCover()
                ))
                .toList();

        return new SellerProductRevisionResponse(
                revision.getId(),
                revision.getRevisionNumber(),
                revision.getStatus().name(),
                revision.getTitle(),
                revision.getShortDescription(),
                revision.getDescription(),
                revision.getPriceMinor(),
                revision.getCurrency(),
                tags,
                revision.getArchiveOriginalFilename(),
                revision.getArchiveSizeBytes(),
                StringUtils.hasText(revision.getArchiveObjectKey()),
                images,
                revision.getSubmittedAt(),
                revision.getReviewedAt(),
                revision.getRejectionReason()
        );
    }

    private String generateUniqueProductSlug(String title) {
        String baseSlug = SlugUtils.toSlug(title);
        String candidate = baseSlug;
        int index = 2;

        while (productRepository.existsBySlug(candidate)) {
            candidate = baseSlug + "-" + index;
            index++;
        }

        return candidate;
    }

    private void validateArchiveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archive file is required");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase(Locale.ROOT).endsWith(".zip")) {
            throw new IllegalArgumentException("Archive must be a .zip file");
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();

        boolean validExtension = filename != null && (
                filename.toLowerCase(Locale.ROOT).endsWith(".png")
                        || filename.toLowerCase(Locale.ROOT).endsWith(".jpg")
                        || filename.toLowerCase(Locale.ROOT).endsWith(".jpeg")
                        || filename.toLowerCase(Locale.ROOT).endsWith(".webp")
        );

        boolean validContentType = contentType != null && contentType.startsWith("image/");

        if (!validExtension || !validContentType) {
            throw new IllegalArgumentException("Only PNG, JPG, JPEG and WEBP images are allowed");
        }
    }

    private String buildArchiveObjectKey(Long revisionId, String originalFilename) {
        return "revisions/" + revisionId + "/archive/" + UUID.randomUUID() + "-" + sanitizeFilename(originalFilename);
    }

    private String buildImageObjectKey(Long revisionId, String originalFilename) {
        return "revisions/" + revisionId + "/images/" + UUID.randomUUID() + "-" + sanitizeFilename(originalFilename);
    }

    private String sanitizeFilename(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "file";
        }

        return filename.trim()
                .replaceAll("[^A-Za-z0-9._-]", "-");
    }

    private String contentTypeOrDefault(String contentType, String fallback) {
        return StringUtils.hasText(contentType) ? contentType : fallback;
    }
}