package com.recipe_manager.unit_tests.model.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.dto.external.recipescraper.IngredientShoppingInfoDto;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IngredientShoppingInfoDtoTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    IngredientShoppingInfoDto dto = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .estimatedPrice(new BigDecimal("1.99"))
        .build();

    assertThat(dto.getIngredientName()).isEqualTo("Salt");
    assertThat(dto.getQuantity()).isEqualByComparingTo(new BigDecimal("1.0"));
    assertThat(dto.getUnit()).isEqualTo(IngredientUnit.TSP);
    assertThat(dto.getEstimatedPrice()).isEqualByComparingTo(new BigDecimal("1.99"));
  }

  @Test
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    IngredientShoppingInfoDto original = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .estimatedPrice(new BigDecimal("1.99"))
        .build();

    String json = objectMapper.writeValueAsString(original);
    IngredientShoppingInfoDto deserialized = objectMapper.readValue(json, IngredientShoppingInfoDto.class);

    assertThat(deserialized)
        .usingRecursiveComparison()
        .isEqualTo(original);
  }

  @Test
  @DisplayName("Should handle null values properly")
  void shouldHandleNullValues() {
    IngredientShoppingInfoDto dto = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .build();

    assertThat(dto.getIngredientName()).isEqualTo("Salt");
    assertThat(dto.getQuantity()).isNull();
    assertThat(dto.getUnit()).isNull();
    assertThat(dto.getEstimatedPrice()).isNull();
  }

  @Test
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    IngredientShoppingInfoDto dto1 = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .estimatedPrice(new BigDecimal("1.99"))
        .build();

    IngredientShoppingInfoDto dto2 = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .estimatedPrice(new BigDecimal("1.99"))
        .build();

    IngredientShoppingInfoDto differentDto = IngredientShoppingInfoDto.builder()
        .ingredientName("Pepper")
        .quantity(new BigDecimal("0.5"))
        .unit(IngredientUnit.TSP)
        .estimatedPrice(new BigDecimal("2.99"))
        .build();

    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2)
        .isNotEqualTo(differentDto)
        .doesNotHaveSameHashCodeAs(differentDto);
  }
}
