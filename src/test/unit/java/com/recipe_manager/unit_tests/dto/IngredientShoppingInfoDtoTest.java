package com.recipe_manager.unit_tests.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.recipe_manager.model.dto.external.recipescraper.IngredientShoppingInfoDto;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IngredientShoppingInfoDtoTest {

  @Test
  @DisplayName("Should build IngredientShoppingInfoDto with all fields")
  void shouldBuildWithAllFields() {
    IngredientShoppingInfoDto dto = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.5"))
        .unit(IngredientUnit.TSP)
        .estimatedPrice(new BigDecimal("2.99"))
        .build();

    assertThat(dto.getIngredientName()).isEqualTo("Salt");
    assertThat(dto.getQuantity()).isEqualByComparingTo(new BigDecimal("1.5"));
    assertThat(dto.getUnit()).isEqualTo(IngredientUnit.TSP);
    assertThat(dto.getEstimatedPrice()).isEqualByComparingTo(new BigDecimal("2.99"));
  }

  @Test
  @DisplayName("Should support toString method")
  void shouldSupportToString() {
    IngredientShoppingInfoDto dto = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .estimatedPrice(new BigDecimal("2.99"))
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("IngredientShoppingInfoDto");
    assertThat(toString).contains("Salt");
  }
}
