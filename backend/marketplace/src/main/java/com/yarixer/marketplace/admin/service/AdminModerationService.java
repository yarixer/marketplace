package com.yarixer.marketplace.admin.service;

import com.yarixer.marketplace.admin.api.AdminModerationDecisionResponse;
import com.yarixer.marketplace.admin.api.AdminModerationRejectRequest;
import com.yarixer.marketplace.admin.api.AdminModerationRevisionDetailsResponse;
import com.yarixer.marketplace.admin.api.AdminModerationTagResponse;
import com.yarixer.marketplace.admin.api.AdminPendingRevisionListItemResponse;
import com.yarixer.marketplace.catalog.domain.Product;
import com.yarixer.marketplace.catalog.domain.ProductRevision;
import com.yarixer.marketplace.catalog.domain.ProductRevisionStatus;
import com.yarixer.marketplace.catalog.domain.Tag;
import com.yarixer.marketplace.catalog.repository.ProductRepository;
import com.yarixer.marketplace.catalog.repository.ProductRevisionRepository;
import com.yarixer.marketplace.common.exception.ResourceNotFoundException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminModerationService {

    private final ProductRevisionRepository productRevisionRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<AdminPendingRevisionListItemResponse> listPendingRevisions() {
        return productRevisionRepository.findAllByStatusOrderBySubmittedAtAsc(ProductRevisionStatus.PENDING_REVIEW)
                .stream()
                .map(this::toPendingListItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminModerationRevisionDetailsResponse getPendingRevisionDetails(Long revisionId) {
        ProductRevision revision = getPendingRevision(revisionId);
        return toDetailsResponse(revision);
    }

    public AdminModerationDecisionResponse approveRevision(Long adminUserId, Long revisionId) {
        ProductRevision revision = getPendingRevision(revisionId);
        Product product = revision.getProduct();

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        revision.setStatus(ProductRevisionStatus.APPROVED);
        revision.setReviewedAt(now);
        revision.setReviewedByUserId(adminUserId);
        revision.setRejectionReason(null);
        productRevisionRepository.save(revision);

        product.setCurrentLiveRevision(revision);
        productRepository.save(product);

        return new AdminModerationDecisionResponse(
                revision.getId(),
                product.getId(),
                revision.getStatus().name(),
                revision.getReviewedAt(),
                revision.getReviewedByUserId()
        );
    }

    public AdminModerationDecisionResponse rejectRevision(
            Long adminUserId,
            Long revisionId,
            AdminModerationRejectRequest request
    ) {
        ProductRevision revision = getPendingRevision(revisionId);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        revision.setStatus(ProductRevisionStatus.REJECTED);
        revision.setReviewedAt(now);
        revision.setReviewedByUserId(adminUserId);
        revision.setRejectionReason(request.rejectionReason().trim());
        productRevisionRepository.save(revision);

        return new AdminModerationDecisionResponse(
                revision.getId(),
                revision.getProduct().getId(),
                revision.getStatus().name(),
                revision.getReviewedAt(),
                revision.getReviewedByUserId()
        );
    }

    private ProductRevision getPendingRevision(Long revisionId) {
        return productRevisionRepository.findByIdAndStatus(revisionId, ProductRevisionStatus.PENDING_REVIEW)
                .orElseThrow(() -> new ResourceNotFoundException("Pending revision not found"));
    }

    private AdminPendingRevisionListItemResponse toPendingListItem(ProductRevision revision) {
        return new AdminPendingRevisionListItemResponse(
                revision.getId(),
                revision.getProduct().getId(),
                revision.getProduct().getSlug(),
                revision.getProduct().getSellerProfile().getPublicName(),
                revision.getRevisionNumber(),
                revision.getTitle(),
                revision.getPriceMinor(),
                revision.getCurrency(),
                revision.getSubmittedAt()
        );
    }

    private AdminModerationRevisionDetailsResponse toDetailsResponse(ProductRevision revision) {
        List<AdminModerationTagResponse> tags = revision.getTags().stream()
                .sorted(Comparator.comparing(Tag::getName))
                .map(tag -> new AdminModerationTagResponse(tag.getId(), tag.getName(), tag.getSlug()))
                .toList();

        return new AdminModerationRevisionDetailsResponse(
                revision.getId(),
                revision.getProduct().getId(),
                revision.getProduct().getSlug(),
                revision.getProduct().getState().name(),
                revision.getProduct().getSellerProfile().getPublicName(),
                revision.getRevisionNumber(),
                revision.getStatus().name(),
                revision.getTitle(),
                revision.getShortDescription(),
                revision.getDescription(),
                revision.getPriceMinor(),
                revision.getCurrency(),
                tags,
                revision.getSubmittedAt(),
                revision.getReviewedAt(),
                revision.getReviewedByUserId(),
                revision.getRejectionReason(),
                StringUtils.hasText(revision.getArchiveObjectKey()),
                revision.getProduct().getCurrentLiveRevision() != null
                        ? revision.getProduct().getCurrentLiveRevision().getId()
                        : null
        );
    }
}