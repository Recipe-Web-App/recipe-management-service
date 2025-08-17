package com.recipe_manager.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import com.recipe_manager.client.recipescraper.RecipeScraperClient;
import com.recipe_manager.model.dto.external.recipescraper.IngredientShoppingInfoDto;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.model.dto.ingredient.QuantityDto;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

@ExtendWith(MockitoExtension.class)
class RecipeScraperClientTest {

  @Mock
  private RecipeScraperClient recipeScraperClient;

  private RecipeScraperShoppingDto testShoppingDto;

  @BeforeEach
  void setUp() {
    IngredientShoppingInfoDto ingredient = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(QuantityDto.builder()
            .amount(1.0)
            .measurement(IngredientUnit.TSP)
            .build())
        .estimatedPrice(new BigDecimal("2.99"))
        .build();

    testShoppingDto = RecipeScraperShoppingDto.builder()
        .recipeId(123L)
        .ingredients(Map.of("Salt", ingredient))
        .totalEstimatedCost(new BigDecimal("2.99"))
        .build();
  }

  @Test
  @DisplayName("Should get shopping info successfully")
  void shouldGetShoppingInfoSuccessfully() {
    // Arrange
    when(recipeScraperClient.getShoppingInfo(123L)).thenReturn(testShoppingDto);

    // Act
    RecipeScraperShoppingDto result = recipeScraperClient.getShoppingInfo(123L);

    // Assert
    assertThat(result).isEqualTo(testShoppingDto);
    assertThat(result.getRecipeId()).isEqualTo(123L);
    assertThat(result.getIngredients()).hasSize(1);
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(new BigDecimal("2.99"));
  }

  @Test
  @DisplayName("Should handle feign exception")
  void shouldHandleFeignException() {
    // Arrange
    Request request = Request.create(Request.HttpMethod.GET, "/test", Map.of(), null, new RequestTemplate());
    FeignException.BadRequest feignException = new FeignException.BadRequest(
        "Bad request", request, null, null);

    when(recipeScraperClient.getShoppingInfo(123L)).thenThrow(feignException);

    // Act & Assert
    assertThatThrownBy(() -> recipeScraperClient.getShoppingInfo(123L))
        .isInstanceOf(FeignException.BadRequest.class)
        .hasMessageContaining("Bad request");
  }

  @Test
  @DisplayName("Should handle null response")
  void shouldHandleNullResponse() {
    // Arrange
    when(recipeScraperClient.getShoppingInfo(123L)).thenReturn(null);

    // Act
    RecipeScraperShoppingDto result = recipeScraperClient.getShoppingInfo(123L);

    // Assert
    assertThat(result).isNull();
  }
}
