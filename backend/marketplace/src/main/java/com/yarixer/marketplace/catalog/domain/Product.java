package com.yarixer.marketplace.catalog.domain;

import com.yarixer.marketplace.common.model.AuditableEntity;
import com.yarixer.marketplace.seller.domain.SellerProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(name = "uk_products_slug", columnNames = "slug")
})
public class Product extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerProfile sellerProfile;

    @Column(name = "slug", nullable = false, length = 180)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 32)
    private ProductState state = ProductState.ACTIVE;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_live_revision_id", foreignKey = @jakarta.persistence.ForeignKey(name = "fk_products_current_live_revision"))
    private ProductRevision currentLiveRevision;
}