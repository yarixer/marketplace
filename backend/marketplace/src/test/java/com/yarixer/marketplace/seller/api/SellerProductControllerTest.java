package com.yarixer.marketplace.seller.api;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Sql(scripts = "classpath:sql/cleanup.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/tag-seed.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class SellerProductControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sellerCanCreateProductAndListIt() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller1@test.local", "Seller One", "Voxel Forge");

        String createBody = """
            {
              "title": "Crystal Pack"
            }
            """;

        mockMvc.perform(
                        post("/api/seller/products")
                                .header("Authorization", "Bearer " + sellerAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(createBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("crystal-pack"))
                .andExpect(jsonPath("$.draftRevision.status").value("DRAFT"))
                .andExpect(jsonPath("$.draftRevision.title").value("Crystal Pack"));

        mockMvc.perform(
                        get("/api/seller/products")
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("crystal-pack"))
                .andExpect(jsonPath("$[0].workflowStatus").value("DRAFT"));
    }

    @Test
    void buyerCannotCreateSellerProduct() throws Exception {
        String buyerAccessToken = registerBuyer("buyer-only@test.local", "Buyer Only");

        String createBody = """
            {
              "title": "Unauthorized Pack"
            }
            """;

        mockMvc.perform(
                        post("/api/seller/products")
                                .header("Authorization", "Bearer " + buyerAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(createBody)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void sellerCanUpdateDraftAndSubmitReview() throws Exception {
        String sellerAccessToken = registerAndBecomeSeller("seller2@test.local", "Seller Two", "Voxel Craft");

        Long productId = createSellerProduct(sellerAccessToken, "Easter Crystal Pack");

        String updateBody = """
            {
              "shortDescription": "Seasonal crystal pack for Easter.",
              "description": "Long draft description for the product.",
              "priceMinor": 799,
              "tagSlugs": ["easter", "voxel"]
            }
            """;

        mockMvc.perform(
                        put("/api/seller/products/{productId}/draft", productId)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                                .contentType(APPLICATION_JSON)
                                .content(updateBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.draftRevision.shortDescription").value("Seasonal crystal pack for Easter."))
                .andExpect(jsonPath("$.draftRevision.priceMinor").value(799))
                .andExpect(jsonPath("$.draftRevision.tags.length()").value(2));

        mockMvc.perform(
                        post("/api/seller/products/{productId}/submit-review", productId)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_REVIEW"));

        mockMvc.perform(
                        get("/api/seller/products/{productId}", productId)
                                .header("Authorization", "Bearer " + sellerAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.draftRevision").doesNotExist())
                .andExpect(jsonPath("$.pendingRevision.status").value("PENDING_REVIEW"))
                .andExpect(jsonPath("$.pendingRevision.title").value("Easter Crystal Pack"));
    }

    @Test
    void sellerCannotAccessForeignProduct() throws Exception {
        String sellerOneAccessToken = registerAndBecomeSeller("seller3@test.local", "Seller Three", "Forge One");
        String sellerTwoAccessToken = registerAndBecomeSeller("seller4@test.local", "Seller Four", "Forge Two");

        Long productId = createSellerProduct(sellerOneAccessToken, "Private Pack");

        mockMvc.perform(
                        get("/api/seller/products/{productId}", productId)
                                .header("Authorization", "Bearer " + sellerTwoAccessToken)
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
        long productId = json.get("productId").asLong();

        assertThat(productId).isPositive();
        return productId;
    }
}