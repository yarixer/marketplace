package com.yarixer.marketplace.catalog.domain;

import com.yarixer.marketplace.common.model.AuditableEntity;
import com.yarixer.marketplace.user.domain.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_votes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_votes_product_user", columnNames = {"product_id", "user_id"})
})
public class ProductVote extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "vote_value", nullable = false)
    private byte voteValue;
}