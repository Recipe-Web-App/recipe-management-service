package com.recipe_manager.model.dto.external.recipescraper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.dto.ingredient.QuantityDto;
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
        .quantity(QuantityDto.builder()
            .amount(1.0)
            .measurement(IngredientUnit.TSP)
            .build())
        .estimatedPrice(new BigDecimal("1.99"))
        .build();

    assertThat(dto.getIngredientName()).isEqualTo("Salt");
    assertThat(dto.getQuantity().getAmount()).isEqualByComparingTo(1.0);
    assertThat(dto.getQuantity().getMeasurement()).isEqualTo(IngredientUnit.TSP);
    assertThat(dto.getEstimatedPrice()).isEqualByComparingTo(new BigDecimal("1.99"));
  }

  @Test
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    IngredientShoppingInfoDto original = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(QuantityDto.builder()
            .amount(1.0)
            .measurement(IngredientUnit.TSP)
            .build())
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
    assertThat(dto.getEstimatedPrice()).isNull();
  }

  @Test
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    IngredientShoppingInfoDto dto1 = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(QuantityDto.builder()
            .amount(1.0)
            .measurement(IngredientUnit.TSP)
            .build())
        .estimatedPrice(new BigDecimal("1.99"))
        .build();

    IngredientShoppingInfoDto dto2 = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(QuantityDto.builder()
            .amount(1.0)
            .measurement(IngredientUnit.TSP)
            .build())
        .estimatedPrice(new BigDecimal("1.99"))
        .build();

    IngredientShoppingInfoDto differentDto = IngredientShoppingInfoDto.builder()
        .ingredientName("Pepper")
        .quantity(QuantityDto.builder()
            .amount(0.5)
            .measurement(IngredientUnit.TSP)
            .build())
        .estimatedPrice(new BigDecimal("2.99"))
        .build();

    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2)
        .isNotEqualTo(differentDto)
        .doesNotHaveSameHashCodeAs(differentDto);
  }

  @Test
  @DisplayName("Should support toString method")
  void shouldSupportToString() {
    IngredientShoppingInfoDto dto = IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(QuantityDto.builder()
            .amount(1.0)
            .measurement(IngredientUnit.TSP)
            .build())
        .estimatedPrice(new BigDecimal("2.99"))
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("IngredientShoppingInfoDto");
    assertThat(toString).contains("Salt");
  }
}
