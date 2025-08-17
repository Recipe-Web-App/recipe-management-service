package com.recipe_manager.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class RecipeScraperExceptionTest {

  @Test
  @DisplayName("Should create exception with message")
  void shouldCreateExceptionWithMessage() {
    RecipeScraperException exception = new RecipeScraperException("Test error");

    assertThat(exception.getMessage()).isEqualTo("Test error");
  }

  @Test
  @DisplayName("Should create exception with message and cause")
  void shouldCreateExceptionWithMessageAndCause() {
    RuntimeException cause = new RuntimeException("Original error");
    RecipeScraperException exception = new RecipeScraperException("Wrapped error", cause);

    assertAll(
        () -> assertThat(exception.getMessage())
            .isEqualTo("Wrapped error"),
        () -> assertThat(exception.getCause())
            .isEqualTo(cause));
  }

  @Test
  @DisplayName("Should create exception with recipe ID and message")
  void shouldCreateExceptionWithRecipeId() {
    RecipeScraperException exception = new RecipeScraperException(123L, "Recipe parsing failed");

    assertAll(
        () -> assertThat(exception.getMessage())
            .contains("Recipe scraper error for recipe 123"),
        () -> assertThat(exception.getRecipeId())
            .isEqualTo(123L));
  }

  @Test
  @DisplayName("Should create exception with recipe ID, message, and cause")
  void shouldCreateExceptionWithRecipeIdMessageAndCause() {
    RuntimeException cause = new RuntimeException("Network timeout");
    RecipeScraperException exception = new RecipeScraperException(456L, "Failed to fetch recipe", cause);

    assertAll(
        () -> assertThat(exception.getMessage())
            .contains("Recipe scraper error for recipe 456")
            .contains("Failed to fetch recipe"),
        () -> assertThat(exception.getRecipeId())
            .isEqualTo(456L),
        () -> assertThat(exception.getCause())
            .isEqualTo(cause));
  }

  @Test
  @DisplayName("Should create exception with recipe ID, status code, and message")
  void shouldCreateExceptionWithRecipeIdStatusCodeAndMessage() {
    RecipeScraperException exception = new RecipeScraperException(789L, 404, "Recipe not found");

    assertAll(
        () -> assertThat(exception.getMessage())
            .contains("Recipe scraper error for recipe 789")
            .contains("Recipe not found"),
        () -> assertThat(exception.getRecipeId())
            .isEqualTo(789L),
        () -> assertThat(exception.getStatusCode())
            .isEqualTo(404));
  }

  @Test
  @DisplayName("Should handle null recipe ID")
  void shouldHandleNullRecipeId() {
    RecipeScraperException exception = new RecipeScraperException(null, "Error with unknown recipe");

    assertAll(
        () -> assertThat(exception.getMessage())
            .contains("Recipe scraper error for recipe null"),
        () -> assertThat(exception.getRecipeId())
            .isNull());
  }

  @Test
  @DisplayName("Should handle null recipe ID with cause")
  void shouldHandleNullRecipeIdWithCause() {
    RuntimeException cause = new RuntimeException("Service unavailable");
    RecipeScraperException exception = new RecipeScraperException(null, "Service error", cause);

    assertAll(
        () -> assertThat(exception.getMessage())
            .contains("Recipe scraper error for recipe null"),
        () -> assertThat(exception.getRecipeId())
            .isNull(),
        () -> assertThat(exception.getCause())
            .isEqualTo(cause));
  }

  @Test
  @DisplayName("Should handle null recipe ID with status code")
  void shouldHandleNullRecipeIdWithStatusCode() {
    RecipeScraperException exception = new RecipeScraperException(null, 500, "Internal server error");

    assertAll(
        () -> assertThat(exception.getMessage())
            .contains("Recipe scraper error for recipe null"),
        () -> assertThat(exception.getRecipeId())
            .isNull(),
        () -> assertThat(exception.getStatusCode())
            .isEqualTo(500));
  }
}
