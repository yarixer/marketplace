package com.yarixer.marketplace.admin.service;

import com.yarixer.marketplace.admin.api.AdminUserDetailsResponse;
import com.yarixer.marketplace.admin.api.AdminUserListItemResponse;
import com.yarixer.marketplace.admin.api.AdminUserUpdateRequest;
import com.yarixer.marketplace.auth.api.SellerProfileResponse;
import com.yarixer.marketplace.common.exception.ResourceNotFoundException;
import com.yarixer.marketplace.common.util.SlugUtils;
import com.yarixer.marketplace.seller.domain.SellerProfile;
import com.yarixer.marketplace.seller.repository.SellerProfileRepository;
import com.yarixer.marketplace.user.domain.AppUser;
import com.yarixer.marketplace.user.domain.RoleType;
import com.yarixer.marketplace.user.repository.UserRepository;
import com.yarixer.marketplace.wallet.api.WalletBalanceResponse;
import com.yarixer.marketplace.wallet.repository.WalletAccountRepository;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserManagementService {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final WalletAccountRepository walletAccountRepository;

    @Transactional(readOnly = true)
    public List<AdminUserListItemResponse> listUsers() {
        return userRepository.findAllByOrderByIdAsc().stream()
                .map(this::toListItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminUserDetailsResponse getUser(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return toDetails(user);
    }

    public AdminUserDetailsResponse updateUser(Long userId, AdminUserUpdateRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.displayName() != null) {
            user.setDisplayName(request.displayName().trim());
        }

        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }

        if (request.roles() != null) {
            Set<RoleType> roles = request.roles().stream()
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(RoleType::valueOf)
                    .collect(Collectors.toSet());

            user.setRoles(roles);

            if (roles.contains(RoleType.SELLER) && sellerProfileRepository.findByUser_Id(user.getId()).isEmpty()) {
                SellerProfile sellerProfile = new SellerProfile();
                sellerProfile.setUser(user);
                sellerProfile.setPublicName(user.getDisplayName());
                sellerProfile.setSlug(generateUniqueSellerSlug(user.getDisplayName()));
                sellerProfile.setBio(null);
                sellerProfileRepository.save(sellerProfile);
            }
        }

        userRepository.save(user);
        return toDetails(user);
    }

    private AdminUserListItemResponse toListItem(AppUser user) {
        var wallet = walletAccountRepository.findByUser_Id(user.getId()).orElse(null);
        boolean seller = sellerProfileRepository.findByUser_Id(user.getId()).isPresent();

        return new AdminUserListItemResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.isEnabled(),
                user.getRoles().stream().map(Enum::name).sorted().toList(),
                seller,
                wallet != null ? wallet.getAvailableMinor() : 0L,
                wallet != null ? wallet.getPendingMinor() : 0L
        );
    }

    private AdminUserDetailsResponse toDetails(AppUser user) {
        var wallet = walletAccountRepository.findByUser_Id(user.getId()).orElse(null);
        var sellerProfile = sellerProfileRepository.findByUser_Id(user.getId()).orElse(null);

        return new AdminUserDetailsResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.isEnabled(),
                user.getRoles().stream().map(Enum::name).sorted().toList(),
                sellerProfile == null ? null : new SellerProfileResponse(
                        sellerProfile.getPublicName(),
                        sellerProfile.getSlug()
                ),
                new WalletBalanceResponse(
                        wallet != null ? wallet.getId() : null,
                        wallet != null ? wallet.getCurrency() : "USD",
                        wallet != null ? wallet.getAvailableMinor() : 0L,
                        wallet != null ? wallet.getPendingMinor() : 0L
                )
        );
    }

    private String generateUniqueSellerSlug(String publicName) {
        String baseSlug = SlugUtils.toSlug(publicName);
        String candidate = baseSlug;
        int index = 2;

        while (sellerProfileRepository.existsBySlug(candidate)) {
            candidate = baseSlug + "-" + index;
            index++;
        }

        return candidate;
    }
}