package com.yarixer.marketplace.wallet.api;

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
class WalletLedgerControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void mockCompletePurchaseCreditsSellerWalletAndDashboard() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-wallet1@test.local", "Seller Wallet 1", "Forge Wallet");
        long productId = createApprovedProduct(
                sellerAccessToken,
                "Wallet Pack",
                "Wallet Pack",
                "Short wallet description.",
                "Long wallet description.",
                899,
                List.of("easter", "voxel")
        );

        String buyerAccessToken = registerBuyer("buyer-wallet1@test.local", "Buyer Wallet 1");

        long orderId = createAndCompleteOrder(buyerAccessToken, productId);

        mockMvc.perform(
                        get("/api/account/wallet")
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.availableMinor").value(899))
                .andExpect(jsonPath("$.pendingMinor").value(0));

        mockMvc.perform(
                        get("/api/seller/dashboard")
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wallet.availableMinor").value(899))
                .andExpect(jsonPath("$.totalProducts").value(1))
                .andExpect(jsonPath("$.publishedProducts").value(1))
                .andExpect(jsonPath("$.pendingReviewCount").value(0))
                .andExpect(jsonPath("$.totalSalesCount").value(1))
                .andExpect(jsonPath("$.grossRevenueMinor").value(899));

        mockMvc.perform(
                        get("/api/buyer/orders")
                                .header("Authorization", "Bearer " + buyerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(orderId))
                .andExpect(jsonPath("$[0].status").value("PAID"));
    }

    @Test
    void adminCanCreditBuyerWallet() throws Exception {
        String buyerAccessToken = registerBuyer("buyer-wallet2@test.local", "Buyer Wallet 2");
        String adminAccessToken = ensureAdminAndLogin("admin-wallet@test.local", "Admin Wallet");

        String creditBody = """
            {
              "userEmail": "buyer-wallet2@test.local",
              "amountMinor": 1500,
              "note": "Manual support credit"
            }
            """;

        mockMvc.perform(
                        post("/api/admin/wallet/credit")
                                .header("Authorization", "Bearer " + adminAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(creditBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetUserEmail").value("buyer-wallet2@test.local"))
                .andExpect(jsonPath("$.creditedAmountMinor").value(1500))
                .andExpect(jsonPath("$.newAvailableMinor").value(1500));

        mockMvc.perform(
                        get("/api/account/wallet")
                                .header("Authorization", "Bearer " + buyerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableMinor").value(1500))
                .andExpect(jsonPath("$.pendingMinor").value(0));
    }

    @Test
    void sellerCannotUseAdminWalletCreditEndpoint() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-wallet3@test.local", "Seller Wallet 3", "Forge Blocked");

        String creditBody = """
            {
              "userEmail": "nobody@test.local",
              "amountMinor": 500,
              "note": "Not allowed"
            }
            """;

        mockMvc.perform(
                        post("/api/admin/wallet/credit")
                                .header("Authorization", "Bearer " + sellerAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(creditBody)
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

    private long createApprovedProduct(
            String sellerAccessToken,
            String title,
            String expectedSlugTitle,
            String shortDescription,
            String description,
            long priceMinor,
            List<String> tagSlugs
    ) throws Exception {
        long productId = createSellerProduct(sellerAccessToken, title);

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

        String adminAccessToken = ensureAdminAndLogin("admin-wallet@test.local", "Admin Wallet");
        long revisionId = findPendingRevisionIdForProduct(adminAccessToken, productId);

        mockMvc.perform(
                        post("/api/admin/moderation/revisions/{revisionId}/approve", revisionId)
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        return productId;
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