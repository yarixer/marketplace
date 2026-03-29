package com.yarixer.marketplace.seller.repository;

import com.yarixer.marketplace.seller.domain.SellerProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    boolean existsBySlug(String slug);
    boolean existsByUser_Id(Long userId);
    Optional<SellerProfile> findByUser_Id(Long userId);
}