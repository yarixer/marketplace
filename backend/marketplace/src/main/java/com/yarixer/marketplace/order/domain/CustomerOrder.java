package com.yarixer.marketplace.order.domain;

import com.yarixer.marketplace.common.model.AuditableEntity;
import com.yarixer.marketplace.user.domain.AppUser;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class CustomerOrder extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    private AppUser buyer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OrderStatus status = OrderStatus.DRAFT;

    @Column(name = "subtotal_minor", nullable = false)
    private long subtotalMinor;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "payment_provider", length = 40)
    private String paymentProvider;

    @Column(name = "payment_reference", length = 255)
    private String paymentReference;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
    }
}