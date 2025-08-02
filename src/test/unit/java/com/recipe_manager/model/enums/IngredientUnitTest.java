package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for IngredientUnit enum.
 */
@Tag("unit")
class IngredientUnitTest {

  @Test
  @DisplayName("Should have correct enum values")
  @Tag("standard-processing")
  void shouldHaveCorrectEnumValues() {
    // Given & When
    IngredientUnit[] values = IngredientUnit.values();

    // Then
    assertThat(values).hasSize(17);
    assertThat(values).containsExactlyInAnyOrder(
        IngredientUnit.G, // Gram
        IngredientUnit.KG, // Kilogram
        IngredientUnit.OZ, // Ounce
        IngredientUnit.LB, // Pound
        IngredientUnit.ML, // Milliliter
        IngredientUnit.L, // Liter
        IngredientUnit.CUP, // Cup
        IngredientUnit.TBSP, // Tablespoon
        IngredientUnit.TSP, // Teaspoon
        IngredientUnit.PIECE, // Piece
        IngredientUnit.CLOVE, // Clove
        IngredientUnit.SLICE, // Slice
        IngredientUnit.PINCH, // Pinch
        IngredientUnit.CAN, // Can
        IngredientUnit.BOTTLE, // Bottle
        IngredientUnit.PACKET, // Packet
        IngredientUnit.UNIT // Unit
    );
  }

  @Test
  @DisplayName("Should have correct weight units")
  @Tag("standard-processing")
  void shouldHaveCorrectWeightUnits() {
    // Then
    assertThat(IngredientUnit.G).isNotNull();
    assertThat(IngredientUnit.KG).isNotNull();
    assertThat(IngredientUnit.OZ).isNotNull();
    assertThat(IngredientUnit.LB).isNotNull();
  }

  @Test
  @DisplayName("Should have correct volume units")
  @Tag("standard-processing")
  void shouldHaveCorrectVolumeUnits() {
    // Then
    assertThat(IngredientUnit.ML).isNotNull();
    assertThat(IngredientUnit.L).isNotNull();
    assertThat(IngredientUnit.CUP).isNotNull();
    assertThat(IngredientUnit.TBSP).isNotNull();
    assertThat(IngredientUnit.TSP).isNotNull();
  }

  @Test
  @DisplayName("Should have correct count units")
  @Tag("standard-processing")
  void shouldHaveCorrectCountUnits() {
    // Then
    assertThat(IngredientUnit.PIECE).isNotNull();
    assertThat(IngredientUnit.CLOVE).isNotNull();
    assertThat(IngredientUnit.SLICE).isNotNull();
    assertThat(IngredientUnit.UNIT).isNotNull();
  }

  @Test
  @DisplayName("Should have correct package units")
  @Tag("standard-processing")
  void shouldHaveCorrectPackageUnits() {
    // Then
    assertThat(IngredientUnit.CAN).isNotNull();
    assertThat(IngredientUnit.BOTTLE).isNotNull();
    assertThat(IngredientUnit.PACKET).isNotNull();
  }

  @Test
  @DisplayName("Should have correct small quantity units")
  @Tag("standard-processing")
  void shouldHaveCorrectSmallQuantityUnits() {
    // Then
    assertThat(IngredientUnit.PINCH).isNotNull();
  }

  @Test
  @DisplayName("Should convert from string correctly")
  @Tag("standard-processing")
  void shouldConvertFromStringCorrectly() {
    // Given
    String gramString = "G";
    String kilogramString = "KG";
    String cupString = "CUP";

    // When & Then
    assertThat(IngredientUnit.valueOf(gramString)).isEqualTo(IngredientUnit.G);
    assertThat(IngredientUnit.valueOf(kilogramString)).isEqualTo(IngredientUnit.KG);
    assertThat(IngredientUnit.valueOf(cupString)).isEqualTo(IngredientUnit.CUP);
  }

  @Test
  @DisplayName("Should have correct string representation")
  @Tag("standard-processing")
  void shouldHaveCorrectStringRepresentation() {
    // Then
    assertThat(IngredientUnit.G.name()).isEqualTo("G");
    assertThat(IngredientUnit.KG.name()).isEqualTo("KG");
    assertThat(IngredientUnit.CUP.name()).isEqualTo("CUP");
    assertThat(IngredientUnit.TBSP.name()).isEqualTo("TBSP");
    assertThat(IngredientUnit.TSP.name()).isEqualTo("TSP");
  }
}
