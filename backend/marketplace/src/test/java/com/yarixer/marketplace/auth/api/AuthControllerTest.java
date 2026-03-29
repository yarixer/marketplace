package com.yarixer.marketplace.auth.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class AuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerThenReadCurrentUserShouldWork() throws Exception {
        String requestBody = """
            {
              "email": "buyer1@test.local",
              "displayName": "Buyer One",
              "password": "StrongPass123"
            }
            """;

        MvcResult registerResult = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("buyer1@test.local"))
                .andExpect(jsonPath("$.user.seller").value(false))
                .andExpect(jsonPath("$.user.roles[0]").value("BUYER"))
                .andReturn();

        JsonNode registerJson = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        String accessToken = registerJson.get("accessToken").asText();

        mockMvc.perform(
                        get("/api/account/me")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("buyer1@test.local"))
                .andExpect(jsonPath("$.displayName").value("Buyer One"))
                .andExpect(jsonPath("$.seller").value(false))
                .andExpect(jsonPath("$.roles[0]").value("BUYER"));
    }

    @Test
    void refreshShouldRotateRefreshToken() throws Exception {
        String registerBody = """
            {
              "email": "buyer2@test.local",
              "displayName": "Buyer Two",
              "password": "StrongPass123"
            }
            """;

        MvcResult registerResult = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(APPLICATION_JSON)
                                .content(registerBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode registerJson = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        String oldRefreshToken = registerJson.get("refreshToken").asText();

        String refreshBody = """
            {
              "refreshToken": "%s"
            }
            """.formatted(oldRefreshToken);

        MvcResult refreshResult = mockMvc.perform(
                        post("/api/auth/refresh")
                                .contentType(APPLICATION_JSON)
                                .content(refreshBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode refreshJson = objectMapper.readTree(refreshResult.getResponse().getContentAsString());
        String newRefreshToken = refreshJson.get("refreshToken").asText();

        assertThat(newRefreshToken).isNotEqualTo(oldRefreshToken);

        mockMvc.perform(
                        post("/api/auth/refresh")
                                .contentType(APPLICATION_JSON)
                                .content(refreshBody)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void becomeSellerShouldAddRoleAndCreateProfile() throws Exception {
        String registerBody = """
            {
              "email": "buyer3@test.local",
              "displayName": "Buyer Three",
              "password": "StrongPass123"
            }
            """;

        MvcResult registerResult = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(APPLICATION_JSON)
                                .content(registerBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode registerJson = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        String accessToken = registerJson.get("accessToken").asText();

        String becomeSellerBody = """
            {
              "publicName": "Voxel Forge"
            }
            """;

        mockMvc.perform(
                        post("/api/account/become-seller")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(APPLICATION_JSON)
                                .content(becomeSellerBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").value(true))
                .andExpect(jsonPath("$.sellerProfile.publicName").value("Voxel Forge"))
                .andExpect(jsonPath("$.sellerProfile.slug").value("voxel-forge"))
                .andExpect(jsonPath("$.roles[0]").value("BUYER"))
                .andExpect(jsonPath("$.roles[1]").value("SELLER"));

        mockMvc.perform(
                        get("/api/account/me")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").value(true))
                .andExpect(jsonPath("$.sellerProfile.slug").value("voxel-forge"));
    }
}