package com.yarixer.marketplace.catalog.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yarixer.marketplace.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

@Sql(scripts = "classpath:sql/cleanup.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/public-catalog-seed.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class PublicCatalogControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void tagsShouldBeReturnedSortedByName() throws Exception {
        mockMvc.perform(get("/api/public/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Easter"))
                .andExpect(jsonPath("$[1].name").value("Seasonal"))
                .andExpect(jsonPath("$[2].name").value("Voxel"));
    }

    @Test
    void productsShouldReturnOnlyApprovedAndActiveItems() throws Exception {
        mockMvc.perform(get("/api/public/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].title").value("Winter Crystals Bundle"))
                .andExpect(jsonPath("$.items[1].title").value("Easter Eggs 2026"));
    }

    @Test
    void searchShouldMatchPrefixAtBeginningOfTitle() throws Exception {
        mockMvc.perform(get("/api/public/products").param("q", "easter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].slug").value("easter-eggs-2026"));
    }

    @Test
    void searchShouldMatchPrefixAtBeginningOfWord() throws Exception {
        mockMvc.perform(get("/api/public/products").param("q", "crystals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].slug").value("winter-crystals-bundle"));
    }

    @Test
    void tagFilterShouldReturnOnlyVisibleMatchingProducts() throws Exception {
        mockMvc.perform(get("/api/public/products").param("tags", "easter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].slug").value("easter-eggs-2026"));
    }

    @Test
    void invalidPageSizeShouldReturnValidationError() throws Exception {
        mockMvc.perform(get("/api/public/products").param("size", "999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("size"));
    }

    @Test
    void productDetailsShouldReturnPublishedLiveProduct() throws Exception {
        mockMvc.perform(get("/api/public/products/easter-eggs-2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1000))
                .andExpect(jsonPath("$.slug").value("easter-eggs-2026"))
                .andExpect(jsonPath("$.title").value("Easter Eggs 2026"))
                .andExpect(jsonPath("$.shortDescription").value("Seasonal voxel egg pack."))
                .andExpect(jsonPath("$.description").value("Approved easter pack for testing."))
                .andExpect(jsonPath("$.priceMinor").value(499))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.seller.publicName").value("VoxelMaster"))
                .andExpect(jsonPath("$.seller.slug").value("voxelmaster"))
                .andExpect(jsonPath("$.tags.length()").value(3))
                .andExpect(jsonPath("$.voteSummary.positiveCount").value(2))
                .andExpect(jsonPath("$.voteSummary.negativeCount").value(1))
                .andExpect(jsonPath("$.voteSummary.score").value(1))
                .andExpect(jsonPath("$.archiveAttached").value(true));
    }

    @Test
    void productDetailsShouldReturn404ForPendingProduct() throws Exception {
        mockMvc.perform(get("/api/public/products/hidden-draft-pack"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void productDetailsShouldReturn404ForArchivedProduct() throws Exception {
        mockMvc.perform(get("/api/public/products/archived-pack"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}