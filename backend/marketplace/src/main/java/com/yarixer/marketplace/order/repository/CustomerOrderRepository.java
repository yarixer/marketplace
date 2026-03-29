package com.yarixer.marketplace.order.repository;

import com.yarixer.marketplace.order.domain.CustomerOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    @EntityGraph(attributePaths = {
            "items",
            "items.product",
            "items.revision",
            "items.sellerProfile"
    })
    List<CustomerOrder> findAllByBuyer_IdOrderByCreatedAtDesc(Long buyerId);

    @EntityGraph(attributePaths = {
            "items",
            "items.product",
            "items.revision",
            "items.sellerProfile"
    })
    Optional<CustomerOrder> findByIdAndBuyer_Id(Long orderId, Long buyerId);
}