package com.recipe_manager.unit_tests.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.recipe_manager.model.dto.shopping.ShoppingListItemDto;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShoppingListItemDtoTest {

  @Test
  @DisplayName("Should build shopping list item with all fields")
  void shouldBuildWithAllFields() {
    ShoppingListItemDto item = ShoppingListItemDto.builder()
        .ingredientName("Salt")
        .totalQuantity(new BigDecimal("1.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .estimatedPrice(new BigDecimal("2.99"))
        .build();

    assertThat(item.getIngredientName()).isEqualTo("Salt");
    assertThat(item.getTotalQuantity()).isEqualByComparingTo(new BigDecimal("1.5"));
    assertThat(item.getUnit()).isEqualTo(IngredientUnit.TSP);
    assertThat(item.getIsOptional()).isFalse();
    assertThat(item.getEstimatedPrice()).isEqualByComparingTo(new BigDecimal("2.99"));
  }

  @Test
  @DisplayName("Should handle null values")
  void shouldHandleNullValues() {
    ShoppingListItemDto item = ShoppingListItemDto.builder()
        .ingredientName("Pepper")
        .totalQuantity(new BigDecimal("0.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(null)
        .estimatedPrice(null)
        .build();

    assertThat(item.getIngredientName()).isEqualTo("Pepper");
    assertThat(item.getTotalQuantity()).isEqualByComparingTo(new BigDecimal("0.5"));
    assertThat(item.getUnit()).isEqualTo(IngredientUnit.TSP);
    assertThat(item.getIsOptional()).isNull();
    assertThat(item.getEstimatedPrice()).isNull();
  }

  @Test
  @DisplayName("Should support equality and hash code")
  void shouldSupportEqualityAndHashCode() {
    ShoppingListItemDto item1 = ShoppingListItemDto.builder()
        .ingredientName("Salt")
        .totalQuantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .estimatedPrice(new BigDecimal("2.99"))
        .build();

    ShoppingListItemDto item2 = ShoppingListItemDto.builder()
        .ingredientName("Salt")
        .totalQuantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .estimatedPrice(new BigDecimal("2.99"))
        .build();

    assertThat(item1).isEqualTo(item2);
    assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
  }

  @Test
  @DisplayName("Should support toString method")
  void shouldSupportToString() {
    ShoppingListItemDto item = ShoppingListItemDto.builder()
        .ingredientName("Oregano")
        .totalQuantity(new BigDecimal("0.25"))
        .unit(IngredientUnit.TSP)
        .isOptional(true)
        .estimatedPrice(new BigDecimal("1.49"))
        .build();

    String toString = item.toString();
    assertThat(toString).contains("ShoppingListItemDto");
    assertThat(toString).contains("Oregano");
  }
}
