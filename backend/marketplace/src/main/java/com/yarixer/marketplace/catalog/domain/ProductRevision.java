package com.yarixer.marketplace.catalog.domain;

import com.yarixer.marketplace.common.model.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Length;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_revisions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_revisions_product_rev", columnNames = {"product_id", "revision_number"})
})
public class ProductRevision extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "revision_number", nullable = false)
    private int revisionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ProductRevisionStatus status = ProductRevisionStatus.DRAFT;

    @Column(name = "title", nullable = false, length = 160)
    private String title;

    @Column(name = "short_description", nullable = false, length = 300)
    private String shortDescription;

    @Column(name = "description", nullable = false, length = Length.LONG32)
    private String description;

    @Column(name = "price_minor", nullable = false)
    private long priceMinor;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "archive_object_key", length = 255)
    private String archiveObjectKey;

    @Column(name = "archive_original_filename", length = 255)
    private String archiveOriginalFilename;

    @Column(name = "archive_size_bytes")
    private Long archiveSizeBytes;

    @Column(name = "checksum_sha256", length = 64)
    private String checksumSha256;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    @Column(name = "reviewed_by_user_id")
    private Long reviewedByUserId;

    @Column(name = "rejection_reason", length = 2000)
    private String rejectionReason;

    @ManyToMany
    @JoinTable(
            name = "product_revision_tags",
            joinColumns = @JoinColumn(name = "revision_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}