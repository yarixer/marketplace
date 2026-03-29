package com.yarixer.marketplace.auth.service;

import com.yarixer.marketplace.auth.api.AuthTokensResponse;
import com.yarixer.marketplace.auth.api.AuthUserResponse;
import com.yarixer.marketplace.auth.api.BecomeSellerRequest;
import com.yarixer.marketplace.auth.api.LoginRequest;
import com.yarixer.marketplace.auth.api.LogoutRequest;
import com.yarixer.marketplace.auth.api.RefreshTokenRequest;
import com.yarixer.marketplace.auth.api.RegisterRequest;
import com.yarixer.marketplace.auth.api.SellerProfileResponse;
import com.yarixer.marketplace.auth.security.JwtService;
import com.yarixer.marketplace.common.util.SlugUtils;
import com.yarixer.marketplace.seller.domain.SellerProfile;
import com.yarixer.marketplace.seller.repository.SellerProfileRepository;
import com.yarixer.marketplace.user.domain.AppUser;
import com.yarixer.marketplace.user.domain.RoleType;
import com.yarixer.marketplace.user.repository.UserRepository;
import com.yarixer.marketplace.wallet.domain.WalletAccount;
import com.yarixer.marketplace.wallet.repository.WalletAccountRepository;
import com.yarixer.marketplace.auth.api.ChangePasswordRequest;
import com.yarixer.marketplace.auth.api.UpdateProfileRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final WalletAccountRepository walletAccountRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthTokensResponse register(RegisterRequest request, String clientIp, String userAgent) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        AppUser user = new AppUser();
        user.setEmail(normalizedEmail);
        user.setDisplayName(request.displayName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEnabled(true);
        user.setRoles(new HashSet<>(List.of(RoleType.BUYER)));
        user = userRepository.save(user);

        WalletAccount wallet = new WalletAccount();
        wallet.setUser(user);
        wallet.setCurrency("USD");
        wallet.setAvailableMinor(0L);
        wallet.setPendingMinor(0L);
        walletAccountRepository.save(wallet);

        AuthTokenPair tokenPair = issueTokens(user, clientIp, userAgent);
        return toAuthTokensResponse(tokenPair, user);
    }

    @Transactional
    public AuthTokensResponse login(LoginRequest request, String clientIp, String userAgent) {
        String normalizedEmail = normalizeEmail(request.email());

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
        );

        UserDetails principal = (UserDetails) authentication.getPrincipal();

        AppUser user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        AuthTokenPair tokenPair = issueTokens(user, clientIp, userAgent);
        return toAuthTokensResponse(tokenPair, user);
    }

    @Transactional
    public AuthTokensResponse refresh(RefreshTokenRequest request, String clientIp, String userAgent) {
        RefreshRotationResult rotation = refreshTokenService.rotate(
                request.refreshToken(),
                clientIp,
                userAgent
        );

        IssuedAccessToken accessToken = jwtService.issueAccessToken(rotation.user());

        AuthTokenPair pair = new AuthTokenPair(
                accessToken.token(),
                accessToken.expiresAt(),
                rotation.refreshToken().token(),
                rotation.refreshToken().expiresAt()
        );

        return toAuthTokensResponse(pair, rotation.user());
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenService.revokeIfPresent(request.refreshToken());
    }

    @Transactional(readOnly = true)
    public AuthUserResponse getCurrentUser(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        return toAuthUserResponse(user);
    }

    @Transactional
    public AuthUserResponse becomeSeller(Long userId, BecomeSellerRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (sellerProfileRepository.existsByUser_Id(userId)) {
            throw new IllegalArgumentException("User is already a seller");
        }

        user.getRoles().add(RoleType.SELLER);
        user = userRepository.save(user);

        SellerProfile sellerProfile = new SellerProfile();
        sellerProfile.setUser(user);
        sellerProfile.setPublicName(request.publicName().trim());
        sellerProfile.setSlug(generateUniqueSellerSlug(request.publicName()));
        sellerProfile.setBio(null);

        SellerProfile savedProfile = sellerProfileRepository.save(sellerProfile);

        return toAuthUserResponse(user, savedProfile);
    }

    @Transactional
    public AuthUserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        user.setDisplayName(request.displayName().trim());
        userRepository.save(user);

        return toAuthUserResponse(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private AuthTokenPair issueTokens(AppUser user, String clientIp, String userAgent) {
        IssuedAccessToken accessToken = jwtService.issueAccessToken(user);
        IssuedRefreshToken refreshToken = refreshTokenService.issueToken(user, clientIp, userAgent);

        return new AuthTokenPair(
                accessToken.token(),
                accessToken.expiresAt(),
                refreshToken.token(),
                refreshToken.expiresAt()
        );
    }

    private AuthTokensResponse toAuthTokensResponse(AuthTokenPair tokenPair, AppUser user) {
        return new AuthTokensResponse(
                tokenPair.accessToken(),
                tokenPair.accessTokenExpiresAt(),
                tokenPair.refreshToken(),
                tokenPair.refreshTokenExpiresAt(),
                toAuthUserResponse(user)
        );
    }

    private AuthUserResponse toAuthUserResponse(AppUser user) {
        return sellerProfileRepository.findByUser_Id(user.getId())
                .map(profile -> toAuthUserResponse(user, profile))
                .orElseGet(() -> toAuthUserResponse(user, null));
    }

    private AuthUserResponse toAuthUserResponse(AppUser user, SellerProfile sellerProfile) {
        List<String> roles = new ArrayList<>(user.getRoles().stream().map(Enum::name).sorted().toList());

        SellerProfileResponse sellerProfileResponse = sellerProfile == null
                ? null
                : new SellerProfileResponse(sellerProfile.getPublicName(), sellerProfile.getSlug());

        return new AuthUserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                roles,
                sellerProfile != null,
                sellerProfileResponse
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
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