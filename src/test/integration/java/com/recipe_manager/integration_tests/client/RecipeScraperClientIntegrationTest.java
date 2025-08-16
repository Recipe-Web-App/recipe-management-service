package com.recipe_manager.integration_tests.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.client.recipescraper.RecipeScraperClient;
import com.recipe_manager.exception.ExternalServiceException;
import com.recipe_manager.model.dto.external.recipescraper.IngredientShoppingInfoDto;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.model.enums.IngredientUnit;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
    "external.services.recipe-scraper.baseUrl=http://localhost:${wiremock.server.port}",
    "external.services.recipe-scraper.apiKey=test-api-key"
})
class RecipeScraperClientIntegrationTest {

  @Autowired
  private RecipeScraperClient recipeScraperClient;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    // Reset WireMock before each test
    WireMock.reset();
  }

  @AfterEach
  void tearDown() {
    WireMock.reset();
  }

  @Test
  @DisplayName("Should successfully retrieve shopping info")
  void shouldRetrieveShoppingInfo() throws Exception {
    // Prepare test data
    RecipeScraperShoppingDto expectedResponse = createTestShoppingDto();
    String responseJson = objectMapper.writeValueAsString(expectedResponse);

    // Setup mock
    stubFor(get(urlEqualTo("/api/v1/recipes/123/shopping"))
        .withHeader("Authorization", equalTo("Bearer test-api-key"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(responseJson)));

    // Execute test
    RecipeScraperShoppingDto result = recipeScraperClient.getShoppingInfo(123L);

    // Verify
    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);

    verify(getRequestedFor(urlEqualTo("/api/v1/recipes/123/shopping"))
        .withHeader("Authorization", equalTo("Bearer test-api-key")));
  }

  @Test
  @DisplayName("Should handle 404 response")
  void shouldHandle404Response() {
    // Setup mock
    stubFor(get(urlEqualTo("/api/v1/recipes/999/shopping"))
        .willReturn(aResponse()
            .withStatus(404)
            .withBody("{\"message\":\"Recipe not found\"}")));

    // Execute and verify
    assertThatThrownBy(() -> recipeScraperClient.getShoppingInfo(999L))
        .isInstanceOf(ExternalServiceException.class)
        .hasMessageContaining("Recipe not found");
  }

  @Test
  @DisplayName("Should handle timeout")
  void shouldHandleTimeout() {
    // Setup mock
    stubFor(get(urlEqualTo("/api/v1/recipes/123/shopping"))
        .willReturn(aResponse()
            .withFixedDelay(5000) // 5 second delay
            .withStatus(200)
            .withBody("{}")));

    // Execute and verify
    assertThatThrownBy(() -> recipeScraperClient.getShoppingInfo(123L))
        .isInstanceOf(ExternalServiceException.class)
        .hasMessageContaining("timeout");
  }

  @Test
  @DisplayName("Should handle server error")
  void shouldHandleServerError() {
    // Setup mock
    stubFor(get(urlEqualTo("/api/v1/recipes/123/shopping"))
        .willReturn(aResponse()
            .withStatus(500)
            .withBody("{\"message\":\"Internal server error\"}")));

    // Execute and verify
    assertThatThrownBy(() -> recipeScraperClient.getShoppingInfo(123L))
        .isInstanceOf(ExternalServiceException.class)
        .hasMessageContaining("Internal server error");
  }

  private RecipeScraperShoppingDto createTestShoppingDto() {
    HashMap<String, IngredientShoppingInfoDto> ingredients = new HashMap<>();
    ingredients.put("salt", IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .estimatedPrice(new BigDecimal("1.99"))
        .build());

    return RecipeScraperShoppingDto.builder()
        .recipeId(123L)
        .ingredients(ingredients)
        .totalEstimatedCost(new BigDecimal("1.99"))
        .build();
  }
}
