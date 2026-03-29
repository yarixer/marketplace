package com.yarixer.marketplace.bootstrap;

import com.yarixer.marketplace.catalog.domain.Product;
import com.yarixer.marketplace.catalog.domain.ProductRevision;
import com.yarixer.marketplace.catalog.domain.ProductRevisionStatus;
import com.yarixer.marketplace.catalog.domain.ProductState;
import com.yarixer.marketplace.catalog.domain.Tag;
import com.yarixer.marketplace.seller.domain.SellerProfile;
import com.yarixer.marketplace.seller.repository.SellerProfileRepository;
import com.yarixer.marketplace.tag.repository.TagRepository;
import com.yarixer.marketplace.user.domain.AppUser;
import com.yarixer.marketplace.user.domain.RoleType;
import com.yarixer.marketplace.user.repository.UserRepository;
import com.yarixer.marketplace.wallet.domain.WalletAccount;
import com.yarixer.marketplace.wallet.repository.WalletAccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final WalletAccountRepository walletAccountRepository;
    private final TagRepository tagRepository;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        AppUser admin = createUser(
                "admin@local.test",
                "Admin",
                "admin123",
                EnumSet.of(RoleType.ADMIN)
        );

        AppUser sellerUser = createUser(
                "seller@local.test",
                "VoxelMaster",
                "seller123",
                EnumSet.of(RoleType.SELLER, RoleType.BUYER)
        );

        AppUser buyerUser = createUser(
                "buyer@local.test",
                "Buyer",
                "buyer123",
                EnumSet.of(RoleType.BUYER)
        );

        createWallet(admin);
        createWallet(sellerUser);
        createWallet(buyerUser);

        SellerProfile sellerProfile = new SellerProfile();
        sellerProfile.setUser(sellerUser);
        sellerProfile.setPublicName("VoxelMaster");
        sellerProfile.setSlug("voxelmaster");
        sellerProfile.setBio("Stylized voxel packs and event content.");
        sellerProfile = sellerProfileRepository.save(sellerProfile);

        Tag easter = new Tag();
        easter.setName("Easter");
        easter.setSlug("easter");

        Tag voxel = new Tag();
        voxel.setName("Voxel");
        voxel.setSlug("voxel");

        Tag seasonal = new Tag();
        seasonal.setName("Seasonal");
        seasonal.setSlug("seasonal");

        List<Tag> savedTags = tagRepository.saveAll(List.of(easter, voxel, seasonal));

        Product product = new Product();
        product.setSellerProfile(sellerProfile);
        product.setSlug("easter-eggs-2026");
        product.setState(ProductState.ACTIVE);
        entityManager.persist(product);

        ProductRevision revision = new ProductRevision();
        revision.setProduct(product);
        revision.setRevisionNumber(1);
        revision.setStatus(ProductRevisionStatus.APPROVED);
        revision.setTitle("Easter Eggs 2026");
        revision.setShortDescription("Seasonal voxel egg pack for marketplace MVP.");
        revision.setDescription("""
            Demo product for local development.
            Real archive upload will be added in the backend implementation stage.
            """);
        revision.setPriceMinor(499);
        revision.setCurrency("USD");
        revision.setArchiveObjectKey("demo/easter-eggs-2026-v1.zip");
        revision.setArchiveOriginalFilename("easter-eggs-2026.zip");
        revision.setArchiveSizeBytes(1024L);
        revision.setChecksumSha256("demo-checksum");
        revision.setSubmittedAt(OffsetDateTime.now());
        revision.setReviewedAt(OffsetDateTime.now());
        revision.setReviewedByUserId(admin.getId());
        revision.setTags(Set.copyOf(savedTags));

        entityManager.persist(revision);

        product.setCurrentLiveRevision(revision);
        entityManager.merge(product);
        entityManager.flush();
    }

    private AppUser createUser(String email, String displayName, String rawPassword, Set<RoleType> roles) {
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    private void createWallet(AppUser user) {
        WalletAccount wallet = new WalletAccount();
        wallet.setUser(user);
        wallet.setCurrency("USD");
        wallet.setAvailableMinor(0L);
        wallet.setPendingMinor(0L);
        walletAccountRepository.save(wallet);
    }
}