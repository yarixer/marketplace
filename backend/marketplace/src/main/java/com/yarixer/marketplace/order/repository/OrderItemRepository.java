package com.yarixer.marketplace.order.repository;

import com.yarixer.marketplace.order.domain.OrderItem;
import com.yarixer.marketplace.order.domain.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    long countBySellerProfile_User_IdAndOrder_Status(Long sellerUserId, OrderStatus status);

    @Query("""
        select coalesce(sum(oi.priceMinor), 0)
        from OrderItem oi
        where oi.sellerProfile.user.id = :sellerUserId
          and oi.order.status = :status
    """)
    Long sumRevenueBySellerUserIdAndOrderStatus(Long sellerUserId, OrderStatus status);

    @EntityGraph(attributePaths = {"product", "order", "product.currentLiveRevision"})
    List<OrderItem> findAllBySellerProfile_User_IdAndOrder_Status(Long sellerUserId, OrderStatus status);
}