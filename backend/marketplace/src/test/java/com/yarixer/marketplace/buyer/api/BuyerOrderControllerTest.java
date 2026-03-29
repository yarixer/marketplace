package com.yarixer.marketplace.buyer.api;

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
class BuyerOrderControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void buyerCanCreateOrderCompleteItAndSeeLibrary() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-order1@test.local", "Seller Order 1", "Forge Orders");
        Long productId = createApprovedProduct(
                sellerAccessToken,
                "Order Crystal Pack",
                "Short order description.",
                "Long order description.",
                899,
                List.of("easter", "voxel")
        );

        String buyerAccessToken = registerBuyer("buyer-order1@test.local", "Buyer Order 1");

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
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(productId))
                .andExpect(jsonPath("$.items[0].title").value("Order Crystal Pack"))
                .andReturn();

        JsonNode createdOrderJson = objectMapper.readTree(createOrderResult.getResponse().getContentAsString());
        long orderId = createdOrderJson.get("orderId").asLong();
        assertThat(orderId).isPositive();

        mockMvc.perform(
                        get("/api/buyer/orders")
                                .header("Authorization", "Bearer " + buyerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(orderId))
                .andExpect(jsonPath("$[0].status").value("DRAFT"));

        mockMvc.perform(
                        post("/api/buyer/orders/{orderId}/mock-complete", orderId)
                                .header("Authorization", "Bearer " + buyerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        mockMvc.perform(
                        get("/api/buyer/library")
                                .header("Authorization", "Bearer " + buyerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(productId))
                .andExpect(jsonPath("$[0].slug").value("order-crystal-pack"))
                .andExpect(jsonPath("$[0].title").value("Order Crystal Pack"));
    }

    @Test
    void buyerCannotPurchaseSameProductTwiceAfterEntitlementExists() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-order2@test.local", "Seller Order 2", "Forge Repeat");
        Long productId = createApprovedProduct(
                sellerAccessToken,
                "Repeat Pack",
                "Short repeat description.",
                "Long repeat description.",
                499,
                List.of("seasonal")
        );

        String buyerAccessToken = registerBuyer("buyer-order2@test.local", "Buyer Order 2");

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
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/buyer/orders")
                                .header("Authorization", "Bearer " + buyerAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(createOrderBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product is already owned"));
    }

    @Test
    void buyerCannotCreateOrderForUnpublishedProduct() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-order3@test.local", "Seller Order 3", "Forge Hidden");
        Long productId = createPendingOnlyProduct(
                sellerAccessToken,
                "Hidden Pack",
                "Short hidden description.",
                "Long hidden description.",
                399,
                List.of("easter")
        );

        String buyerAccessToken = registerBuyer("buyer-order3@test.local", "Buyer Order 3");

        String createOrderBody = """
            {
              "productId": %d
            }
            """.formatted(productId);

        mockMvc.perform(
                        post("/api/buyer/orders")
                                .header("Authorization", "Bearer " + buyerAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(createOrderBody)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void buyerCannotCompleteForeignOrder() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller-order4@test.local", "Seller Order 4", "Forge Foreign");
        Long productId = createApprovedProduct(
                sellerAccessToken,
                "Foreign Pack",
                "Short foreign description.",
                "Long foreign description.",
                599,
                List.of("voxel")
        );

        String buyerOneAccessToken = registerBuyer("buyer-order4a@test.local", "Buyer Order 4A");
        String buyerTwoAccessToken = registerBuyer("buyer-order4b@test.local", "Buyer Order 4B");

        String createOrderBody = """
            {
              "productId": %d
            }
            """.formatted(productId);

        MvcResult createOrderResult = mockMvc.perform(
                        post("/api/buyer/orders")
                                .header("Authorization", "Bearer " + buyerOneAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(createOrderBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createdOrderJson = objectMapper.readTree(createOrderResult.getResponse().getContentAsString());
        long orderId = createdOrderJson.get("orderId").asLong();

        mockMvc.perform(
                        post("/api/buyer/orders/{orderId}/mock-complete", orderId)
                                .header("Authorization", "Bearer " + buyerTwoAccessToken)
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

    private Long createPendingOnlyProduct(
            String sellerAccessToken,
            String title,
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

    private Long createApprovedProduct(
            String sellerAccessToken,
            String title,
            String shortDescription,
            String description,
            long priceMinor,
            List<String> tagSlugs
    ) throws Exception {
        long productId = createPendingOnlyProduct(
                sellerAccessToken,
                title,
                shortDescription,
                description,
                priceMinor,
                tagSlugs
        );

        String adminAccessToken = ensureAdminAndLogin("admin-order@test.local", "Admin Order");

        MvcResult pendingResult = mockMvc.perform(
                        get("/api/admin/moderation/revisions/pending")
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode pendingJson = objectMapper.readTree(pendingResult.getResponse().getContentAsString());

        long revisionId = -1L;
        for (JsonNode node : pendingJson) {
            if (node.get("productId").asLong() == productId) {
                revisionId = node.get("revisionId").asLong();
                break;
            }
        }

        assertThat(revisionId).isPositive();

        mockMvc.perform(
                        post("/api/admin/moderation/revisions/{revisionId}/approve", revisionId)
                                .header("Authorization", "Bearer " + adminAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        return productId;
    }

    private Long createSellerProduct(String accessToken, String title) throws Exception {
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
        return json.get("productId").asLong();
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