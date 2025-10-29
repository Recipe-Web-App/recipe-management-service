package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for CollaborationMode enum. */
@Tag("unit")
class CollaborationModeTest {

  @Test
  @DisplayName("Should have correct enum values")
  @Tag("standard-processing")
  void shouldHaveCorrectEnumValues() {
    // Given & When
    CollaborationMode[] values = CollaborationMode.values();

    // Then
    assertThat(values).hasSize(3);
    assertThat(values)
        .containsExactlyInAnyOrder(
            CollaborationMode.OWNER_ONLY,
            CollaborationMode.ALL_USERS,
            CollaborationMode.SPECIFIC_USERS);
  }

  @Test
  @DisplayName("Should have correct ordinal values")
  @Tag("standard-processing")
  void shouldHaveCorrectOrdinalValues() {
    // Then
    assertThat(CollaborationMode.OWNER_ONLY.ordinal()).isEqualTo(0);
    assertThat(CollaborationMode.ALL_USERS.ordinal()).isEqualTo(1);
    assertThat(CollaborationMode.SPECIFIC_USERS.ordinal()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should convert from string correctly")
  @Tag("standard-processing")
  void shouldConvertFromStringCorrectly() {
    // Given
    String ownerOnlyString = "OWNER_ONLY";
    String allUsersString = "ALL_USERS";
    String specificUsersString = "SPECIFIC_USERS";

    // When & Then
    assertThat(CollaborationMode.valueOf(ownerOnlyString))
        .isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(CollaborationMode.valueOf(allUsersString)).isEqualTo(CollaborationMode.ALL_USERS);
    assertThat(CollaborationMode.valueOf(specificUsersString))
        .isEqualTo(CollaborationMode.SPECIFIC_USERS);
  }

  @Test
  @DisplayName("Should have correct string representation")
  @Tag("standard-processing")
  void shouldHaveCorrectStringRepresentation() {
    // Then
    assertThat(CollaborationMode.OWNER_ONLY.name()).isEqualTo("OWNER_ONLY");
    assertThat(CollaborationMode.ALL_USERS.name()).isEqualTo("ALL_USERS");
    assertThat(CollaborationMode.SPECIFIC_USERS.name()).isEqualTo("SPECIFIC_USERS");
  }

  @Test
  @DisplayName("Should compare collaboration modes correctly")
  @Tag("standard-processing")
  void shouldCompareCollaborationModesCorrectly() {
    // Then
    assertThat(CollaborationMode.OWNER_ONLY).isLessThan(CollaborationMode.ALL_USERS);
    assertThat(CollaborationMode.ALL_USERS).isLessThan(CollaborationMode.SPECIFIC_USERS);

    assertThat(CollaborationMode.SPECIFIC_USERS).isGreaterThan(CollaborationMode.ALL_USERS);
    assertThat(CollaborationMode.ALL_USERS).isGreaterThan(CollaborationMode.OWNER_ONLY);
  }
}
