package com.yarixer.marketplace.seller.domain;

import com.yarixer.marketplace.common.model.AuditableEntity;
import com.yarixer.marketplace.user.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "seller_profiles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_seller_profiles_user_id", columnNames = "user_id"),
        @UniqueConstraint(name = "uk_seller_profiles_slug", columnNames = "slug")
})
public class SellerProfile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "public_name", nullable = false, length = 120)
    private String publicName;

    @Column(name = "slug", nullable = false, length = 160)
    private String slug;

    @Column(name = "bio", length = 2000)
    private String bio;
}