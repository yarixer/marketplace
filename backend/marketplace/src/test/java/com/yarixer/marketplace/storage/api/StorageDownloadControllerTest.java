package com.yarixer.marketplace.storage.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yarixer.marketplace.support.AbstractIntegrationTest;
import com.yarixer.marketplace.user.domain.AppUser;
import com.yarixer.marketplace.user.domain.RoleType;
import com.yarixer.marketplace.user.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Sql(scripts = "classpath:sql/cleanup.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/tag-seed.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class StorageDownloadControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void sellerCanUploadArchiveAndImagesToDraft() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-storage1@test.local", "Seller Storage 1", "Forge Media");
        long productId = createSellerProduct(sellerAccessToken, "Media Pack");

        MockMultipartFile archive = new MockMultipartFile(
                "file",
                "media-pack.zip",
                "application/zip",
                "zip-content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                        multipart("/api/seller/products/{productId}/draft/archive", productId)
                                .file(archive)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.draftRevision.archiveAttached").value(true))
                .andExpect(jsonPath("$.draftRevision.archiveOriginalFilename").value("media-pack.zip"));

        MockMultipartFile image1 = new MockMultipartFile(
                "files",
                "cover.png",
                "image/png",
                "png-1".getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile image2 = new MockMultipartFile(
                "files",
                "detail.webp",
                "image/webp",
                "webp-2".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                        multipart("/api/seller/products/{productId}/draft/images", productId)
                                .file(image1)
                                .file(image2)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.draftRevision.images.length()").value(2))
                .andExpect(jsonPath("$.draftRevision.images[0].cover").value(true))
                .andExpect(jsonPath("$.draftRevision.images[0].url").value(org.hamcrest.Matchers.containsString("test-storage.local")));
    }

    @Test
    void buyerWithEntitlementGetsLatestApprovedRevisionDownloadLink() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-storage2@test.local", "Seller Storage 2", "Forge Versions");
        long productId = createApprovedProductWithStorage(
                sellerAccessToken,
                "Versioned Pack",
                "Versioned Pack",
                "First short description.",
                "First long description.",
                799,
                List.of("easter", "voxel"),
                "versioned-pack-v1.zip"
        );

        String buyerAccessToken = registerBuyer("buyer-storage1@test.local", "Buyer Storage 1");
        long orderId = createAndCompleteOrder(buyerAccessToken, productId);

        MvcResult firstDownload = mockMvc.perform(
                        get("/api/buyer/library/{productId}/download-link", productId)
                                .header("Authorization", "Bearer " + buyerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename").value("versioned-pack-v1.zip"))
                .andReturn();

        JsonNode firstJson = objectMapper.readTree(firstDownload.getResponse().getContentAsString());
        long firstRevisionId = firstJson.get("revisionId").asLong();

        // create new draft from live, upload new archive, resubmit and approve
        String updateBody = """
            {
              "shortDescription": "Second short description.",
              "description": "Second long description.",
              "priceMinor": 899,
              "tagSlugs": ["easter", "voxel"]
            }
            """;

        mockMvc.perform(
                        put("/api/seller/products/{productId}/draft", productId)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(updateBody)
                )
                .andExpect(status().isOk());

        MockMultipartFile archiveV2 = new MockMultipartFile(
                "file",
                "versioned-pack-v2.zip",
                "application/zip",
                "zip-v2".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                        multipart("/api/seller/products/{productId}/draft/archive", productId)
                                .file(archiveV2)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/seller/products/{productId}/submit-review", productId)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_REVIEW"));

        String adminAccessToken = ensureAdminAndLogin("admin-storage@test.local", "Admin Storage");
        long pendingRevisionId = findPendingRevisionIdForProduct(adminAccessToken, productId);

        mockMvc.perform(
                        post("/api/admin/moderation/revisions/{revisionId}/approve", pendingRevisionId)
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(
                        get("/api/buyer/library/{productId}/download-link", productId)
                                .header("Authorization", "Bearer " + buyerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename").value("versioned-pack-v2.zip"))
                .andExpect(jsonPath("$.revisionId").value(org.hamcrest.Matchers.not((int) firstRevisionId)))
                .andExpect(jsonPath("$.url").value(org.hamcrest.Matchers.containsString("test-storage.local")));

        mockMvc.perform(
                        get("/api/buyer/orders")
                                .header("Authorization", "Bearer " + buyerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(orderId));
    }

    @Test
    void buyerWithoutEntitlementCannotGetDownloadLink() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-storage3@test.local", "Seller Storage 3", "Forge Locked");
        long productId = createApprovedProductWithStorage(
                sellerAccessToken,
                "Locked Pack",
                "Locked Pack",
                "Locked short description.",
                "Locked long description.",
                499,
                List.of("seasonal"),
                "locked-pack.zip"
        );

        String buyerAccessToken = registerBuyer("buyer-storage2@test.local", "Buyer Storage 2");

        mockMvc.perform(
                        get("/api/buyer/library/{productId}/download-link", productId)
                                .header("Authorization", "Bearer " + buyerAccessToken)
                )
                .andExpect(status().isNotFound());
    }

    private String registerBuyer(String email, String displayName) throws Exception {
        String registerBody = """
            {
              "email": "%s",
              "displayName": "%s",
              "password": "StrongPass123"
            }
            """.formatted(email, displayName);

        MvcResult registerResult = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(APPLICATION_JSON)
                                .content(registerBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        return json.get("accessToken").asText();
    }

    private String registerAndBecomeSeller(String email, String displayName, String publicName) throws Exception {
        String accessToken = registerBuyer(email, displayName);

        String becomeSellerBody = """
            {
              "publicName": "%s"
            }
            """.formatted(publicName);

        mockMvc.perform(
                        post("/api/account/become-seller")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(APPLICATION_JSON)
                                .content(becomeSellerBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").value(true));

        return accessToken;
    }

    private long createSellerProduct(String accessToken, String title) throws Exception {
        String createBody = """
            {
              "title": "%s"
            }
            """.formatted(title);

        MvcResult createResult = mockMvc.perform(
                        post("/api/seller/products")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(APPLICATION_JSON)
                                .content(createBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long productId = json.get("productId").asLong();
        assertThat(productId).isPositive();
        return productId;
    }

    private long createApprovedProductWithStorage(
            String sellerAccessToken,
            String title,
            String expectedSlugTitle,
            String shortDescription,
            String description,
            long priceMinor,
            List<String> tagSlugs,
            String archiveFilename
    ) throws Exception {
        long productId = createSellerProduct(sellerAccessToken, title);

        MockMultipartFile archive = new MockMultipartFile(
                "file",
                archiveFilename,
                "application/zip",
                "zip-binary".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                        multipart("/api/seller/products/{productId}/draft/archive", productId)
                                .file(archive)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk());

        MockMultipartFile image = new MockMultipartFile(
                "files",
                "cover.png",
                "image/png",
                "png-cover".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                        multipart("/api/seller/products/{productId}/draft/images", productId)
                                .file(image)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk());

        String tagsJson = tagSlugs.stream()
                .map(slug -> "\"" + slug + "\"")
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        String updateBody = """
            {
              "title": "%s",
              "shortDescription": "%s",
              "description": "%s",
              "priceMinor": %d,
              "tagSlugs": [%s]
            }
            """.formatted(expectedSlugTitle, shortDescription, description, priceMinor, tagsJson);

        mockMvc.perform(
                        put("/api/seller/products/{productId}/draft", productId)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(updateBody)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/seller/products/{productId}/submit-review", productId)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_REVIEW"));

        String adminAccessToken = ensureAdminAndLogin("admin-storage@test.local", "Admin Storage");
        long revisionId = findPendingRevisionIdForProduct(adminAccessToken, productId);

        mockMvc.perform(
                        post("/api/admin/moderation/revisions/{revisionId}/approve", revisionId)
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        return productId;
    }

    private long createAndCompleteOrder(String buyerAccessToken, long productId) throws Exception {
        String createOrderBody = """
            {
              "productId": %d
            }
            """.formatted(productId);

        MvcResult createOrderResult = mockMvc.perform(
                        post("/api/buyer/orders")
                                .header("Authorization", "Bearer " + buyerAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(createOrderBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createdOrderJson = objectMapper.readTree(createOrderResult.getResponse().getContentAsString());
        long orderId = createdOrderJson.get("orderId").asLong();

        mockMvc.perform(
                        post("/api/buyer/orders/{orderId}/mock-complete", orderId)
                                .header("Authorization", "Bearer " + buyerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        return orderId;
    }

    private long findPendingRevisionIdForProduct(String adminAccessToken, long productId) throws Exception {
        MvcResult pendingResult = mockMvc.perform(
                        get("/api/admin/moderation/revisions/pending")
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode pendingJson = objectMapper.readTree(pendingResult.getResponse().getContentAsString());

        for (JsonNode node : pendingJson) {
            if (node.get("productId").asLong() == productId) {
                return node.get("revisionId").asLong();
            }
        }

        throw new IllegalStateException("Pending revision not found for product " + productId);
    }

    private String ensureAdminAndLogin(String email, String displayName) throws Exception {
        if (userRepository.findByEmail(email).isEmpty()) {
            AppUser admin = new AppUser();
            admin.setEmail(email);
            admin.setDisplayName(displayName);
            admin.setPasswordHash(passwordEncoder.encode("StrongPass123"));
            admin.setEnabled(true);
            admin.setRoles(new HashSet<>(List.of(RoleType.ADMIN)));
            userRepository.save(admin);
        }

        String loginBody = """
            {
              "email": "%s",
              "password": "StrongPass123"
            }
            """.formatted(email);

        MvcResult loginResult = mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(APPLICATION_JSON)
                                .content(loginBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return json.get("accessToken").asText();
    }
}