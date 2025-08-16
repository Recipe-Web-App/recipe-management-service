package com.recipe_manager.unit_tests.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.recipe_manager.model.dto.response.ShoppingListResponse;
import com.recipe_manager.model.dto.shopping.ShoppingListItemDto;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShoppingListResponseTest {

  @Test
  @DisplayName("Should build shopping list response with all fields")
  void shouldBuildWithAllFields() {
    ShoppingListItemDto item1 = ShoppingListItemDto.builder()
        .ingredientName("Salt")
        .totalQuantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .estimatedPrice(new BigDecimal("2.99"))
        .build();

    ShoppingListItemDto item2 = ShoppingListItemDto.builder()
        .ingredientName("Pepper")
        .totalQuantity(new BigDecimal("0.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(true)
        .estimatedPrice(new BigDecimal("1.99"))
        .build();

    List<ShoppingListItemDto> items = Arrays.asList(item1, item2);

    ShoppingListResponse response = ShoppingListResponse.builder()
        .recipeId(123L)
        .items(items)
        .totalCount(2)
        .totalEstimatedCost(new BigDecimal("4.98"))
        .build();

    assertThat(response.getRecipeId()).isEqualTo(123L);
    assertThat(response.getItems()).hasSize(2);
    assertThat(response.getTotalCount()).isEqualTo(2);
    assertThat(response.getTotalEstimatedCost()).isEqualByComparingTo(new BigDecimal("4.98"));
  }

  @Test
  @DisplayName("Should handle empty items list")
  void shouldHandleEmptyItemsList() {
    ShoppingListResponse response = ShoppingListResponse.builder()
        .recipeId(456L)
        .items(Collections.emptyList())
        .totalCount(0)
        .totalEstimatedCost(BigDecimal.ZERO)
        .build();

    assertThat(response.getRecipeId()).isEqualTo(456L);
    assertThat(response.getItems()).isEmpty();
    assertThat(response.getTotalCount()).isEqualTo(0);
    assertThat(response.getTotalEstimatedCost()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should support toString method")
  void shouldSupportToString() {
    ShoppingListResponse response = ShoppingListResponse.builder()
        .recipeId(789L)
        .items(Collections.emptyList())
        .totalCount(0)
        .totalEstimatedCost(BigDecimal.ZERO)
        .build();

    String toString = response.toString();
    assertThat(toString).contains("ShoppingListResponse");
    assertThat(toString).contains("789");
  }
}
