package com.yarixer.marketplace.admin.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yarixer.marketplace.support.AbstractIntegrationTest;
import com.yarixer.marketplace.user.domain.AppUser;
import com.yarixer.marketplace.user.domain.RoleType;
import com.yarixer.marketplace.user.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Sql(scripts = "classpath:sql/cleanup.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/tag-seed.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class AdminModerationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void adminCanListPendingAndApproveRevisionMakingProductPublic() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-mod1@test.local", "Seller Mod 1", "Forge Alpha");
        Long productId = createPendingProduct(
                sellerAccessToken,
                "Approved Crystal Pack",
                "Short approved description.",
                "Long approved description.",
                999,
                List.of("easter", "voxel")
        );

        String adminAccessToken = ensureAdminAndLogin("admin1@test.local", "Admin One");

        mockMvc.perform(
                        get("/api/admin/moderation/revisions/pending")
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(productId))
                .andExpect(jsonPath("$[0].title").value("Approved Crystal Pack"));

        MvcResult pendingResult = mockMvc.perform(
                        get("/api/admin/moderation/revisions/pending")
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode pendingJson = objectMapper.readTree(pendingResult.getResponse().getContentAsString());
        long revisionId = pendingJson.get(0).get("revisionId").asLong();

        mockMvc.perform(
                        get("/api/admin/moderation/revisions/{revisionId}", revisionId)
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_REVIEW"))
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.sellerPublicName").value("Forge Alpha"));

        mockMvc.perform(
                        post("/api/admin/moderation/revisions/{revisionId}/approve", revisionId)
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.revisionId").value(revisionId))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(get("/api/public/products").param("q", "approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].slug").value("approved-crystal-pack"));
    }

    @Test
    void adminCanRejectRevisionAndProductStaysHidden() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-mod2@test.local", "Seller Mod 2", "Forge Beta");

        createPendingProduct(
                sellerAccessToken,
                "Rejected Crystal Pack",
                "Short rejected description.",
                "Long rejected description.",
                799,
                List.of("seasonal")
        );

        String adminAccessToken = ensureAdminAndLogin("admin2@test.local", "Admin Two");

        MvcResult pendingResult = mockMvc.perform(
                        get("/api/admin/moderation/revisions/pending")
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode pendingJson = objectMapper.readTree(pendingResult.getResponse().getContentAsString());
        long revisionId = pendingJson.get(0).get("revisionId").asLong();

        String rejectBody = """
            {
              "rejectionReason": "Preview and metadata need improvement"
            }
            """;

        mockMvc.perform(
                        post("/api/admin/moderation/revisions/{revisionId}/reject", revisionId)
                                .header("Authorization", "Bearer " + adminAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(rejectBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        mockMvc.perform(get("/api/public/products").param("q", "rejected"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        mockMvc.perform(
                        get("/api/admin/moderation/revisions/{revisionId}", revisionId)
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void sellerCannotAccessAdminModerationEndpoints() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-mod3@test.local", "Seller Mod 3", "Forge Gamma");

        mockMvc.perform(
                        get("/api/admin/moderation/revisions/pending")
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isForbidden());
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

    private Long createPendingProduct(
            String sellerAccessToken,
            String title,
            String shortDescription,
            String description,
            long priceMinor,
            List<String> tagSlugs
    ) throws Exception {
        String createBody = """
            {
              "title": "%s"
            }
            """.formatted(title);

        MvcResult createResult = mockMvc.perform(
                        post("/api/seller/products")
                                .header("Authorization", "Bearer " + sellerAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(createBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createdJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long productId = createdJson.get("productId").asLong();
        assertThat(productId).isPositive();

        String tagsJson = tagSlugs.stream()
                .map(slug -> "\"" + slug + "\"")
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        String updateBody = """
            {
              "shortDescription": "%s",
              "description": "%s",
              "priceMinor": %d,
              "tagSlugs": [%s]
            }
            """.formatted(shortDescription, description, priceMinor, tagsJson);

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

        return productId;
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