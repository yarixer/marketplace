package com.yarixer.marketplace.order.domain;

import com.yarixer.marketplace.catalog.domain.Product;
import com.yarixer.marketplace.user.domain.AppUser;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_entitlements", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_entitlements_buyer_product", columnNames = {"buyer_id", "product_id"})
})
public class ProductEntitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    private AppUser buyer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "granted_by_order_id", nullable = false)
    private CustomerOrder grantedByOrder;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}