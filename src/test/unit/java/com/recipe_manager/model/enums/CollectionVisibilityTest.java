package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for CollectionVisibility enum. */
@Tag("unit")
class CollectionVisibilityTest {

  @Test
  @DisplayName("Should have correct enum values")
  @Tag("standard-processing")
  void shouldHaveCorrectEnumValues() {
    // Given & When
    CollectionVisibility[] values = CollectionVisibility.values();

    // Then
    assertThat(values).hasSize(3);
    assertThat(values)
        .containsExactlyInAnyOrder(
            CollectionVisibility.PUBLIC,
            CollectionVisibility.PRIVATE,
            CollectionVisibility.FRIENDS_ONLY);
  }

  @Test
  @DisplayName("Should have correct ordinal values")
  @Tag("standard-processing")
  void shouldHaveCorrectOrdinalValues() {
    // Then
    assertThat(CollectionVisibility.PUBLIC.ordinal()).isEqualTo(0);
    assertThat(CollectionVisibility.PRIVATE.ordinal()).isEqualTo(1);
    assertThat(CollectionVisibility.FRIENDS_ONLY.ordinal()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should convert from string correctly")
  @Tag("standard-processing")
  void shouldConvertFromStringCorrectly() {
    // Given
    String publicString = "PUBLIC";
    String privateString = "PRIVATE";
    String friendsOnlyString = "FRIENDS_ONLY";

    // When & Then
    assertThat(CollectionVisibility.valueOf(publicString)).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(CollectionVisibility.valueOf(privateString))
        .isEqualTo(CollectionVisibility.PRIVATE);
    assertThat(CollectionVisibility.valueOf(friendsOnlyString))
        .isEqualTo(CollectionVisibility.FRIENDS_ONLY);
  }

  @Test
  @DisplayName("Should have correct string representation")
  @Tag("standard-processing")
  void shouldHaveCorrectStringRepresentation() {
    // Then
    assertThat(CollectionVisibility.PUBLIC.name()).isEqualTo("PUBLIC");
    assertThat(CollectionVisibility.PRIVATE.name()).isEqualTo("PRIVATE");
    assertThat(CollectionVisibility.FRIENDS_ONLY.name()).isEqualTo("FRIENDS_ONLY");
  }

  @Test
  @DisplayName("Should compare visibility levels correctly")
  @Tag("standard-processing")
  void shouldCompareVisibilityLevelsCorrectly() {
    // Then
    assertThat(CollectionVisibility.PUBLIC).isLessThan(CollectionVisibility.PRIVATE);
    assertThat(CollectionVisibility.PRIVATE).isLessThan(CollectionVisibility.FRIENDS_ONLY);

    assertThat(CollectionVisibility.FRIENDS_ONLY).isGreaterThan(CollectionVisibility.PRIVATE);
    assertThat(CollectionVisibility.PRIVATE).isGreaterThan(CollectionVisibility.PUBLIC);
  }
}
