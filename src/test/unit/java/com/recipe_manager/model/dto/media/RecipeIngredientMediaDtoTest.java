package com.recipe_manager.model.dto.media;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeIngredientMediaDto class.
 */
@Tag("standard-processing")
class RecipeIngredientMediaDtoTest {

  private static final Long MEDIA_ID = 1L;
  private static final String URL = "https://example.com/image.jpg";
  private static final String ALT_TEXT = "Example image";
  private static final String CONTENT_TYPE = "image/jpeg";
  private static final Long FILE_SIZE = 1024L;
  private static final LocalDateTime CREATED_AT = LocalDateTime.of(2023, 1, 1, 12, 0);
  private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2023, 1, 2, 12, 0);
  private static final Long RECIPE_ID = 100L;
  private static final Long INGREDIENT_ID = 200L;

  @Test
  @DisplayName("Should create RecipeIngredientMediaDto with constructor")
  void shouldCreateRecipeIngredientMediaDtoWithConstructor() {
    // When
    RecipeIngredientMediaDto mediaDto = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);

    // Then
    assertThat(mediaDto.getMediaId()).isEqualTo(MEDIA_ID);
    assertThat(mediaDto.getUrl()).isEqualTo(URL);
    assertThat(mediaDto.getAltText()).isEqualTo(ALT_TEXT);
    assertThat(mediaDto.getContentType()).isEqualTo(CONTENT_TYPE);
    assertThat(mediaDto.getFileSize()).isEqualTo(FILE_SIZE);
    assertThat(mediaDto.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(mediaDto.getUpdatedAt()).isEqualTo(UPDATED_AT);
    assertThat(mediaDto.getRecipeId()).isEqualTo(RECIPE_ID);
    assertThat(mediaDto.getIngredientId()).isEqualTo(INGREDIENT_ID);
  }

  @Test
  @DisplayName("Should create RecipeIngredientMediaDto with builder")
  void shouldCreateRecipeIngredientMediaDtoWithBuilder() {
    // When
    RecipeIngredientMediaDto mediaDto = RecipeIngredientMediaDto.builder()
        .mediaId(MEDIA_ID)
        .url(URL)
        .altText(ALT_TEXT)
        .contentType(CONTENT_TYPE)
        .fileSize(FILE_SIZE)
        .createdAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .recipeId(RECIPE_ID)
        .ingredientId(INGREDIENT_ID)
        .build();

    // Then
    assertThat(mediaDto.getMediaId()).isEqualTo(MEDIA_ID);
    assertThat(mediaDto.getUrl()).isEqualTo(URL);
    assertThat(mediaDto.getAltText()).isEqualTo(ALT_TEXT);
    assertThat(mediaDto.getContentType()).isEqualTo(CONTENT_TYPE);
    assertThat(mediaDto.getFileSize()).isEqualTo(FILE_SIZE);
    assertThat(mediaDto.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(mediaDto.getUpdatedAt()).isEqualTo(UPDATED_AT);
    assertThat(mediaDto.getRecipeId()).isEqualTo(RECIPE_ID);
    assertThat(mediaDto.getIngredientId()).isEqualTo(INGREDIENT_ID);
  }

  @Test
  @DisplayName("Should set and get RecipeIngredientMediaDto properties")
  void shouldSetAndGetRecipeIngredientMediaDtoProperties() {
    // Given
    RecipeIngredientMediaDto mediaDto = new RecipeIngredientMediaDto();

    // When
    mediaDto.setMediaId(MEDIA_ID);
    mediaDto.setUrl(URL);
    mediaDto.setAltText(ALT_TEXT);
    mediaDto.setContentType(CONTENT_TYPE);
    mediaDto.setFileSize(FILE_SIZE);
    mediaDto.setCreatedAt(CREATED_AT);
    mediaDto.setUpdatedAt(UPDATED_AT);
    mediaDto.setRecipeId(RECIPE_ID);
    mediaDto.setIngredientId(INGREDIENT_ID);

    // Then
    assertThat(mediaDto.getMediaId()).isEqualTo(MEDIA_ID);
    assertThat(mediaDto.getUrl()).isEqualTo(URL);
    assertThat(mediaDto.getAltText()).isEqualTo(ALT_TEXT);
    assertThat(mediaDto.getContentType()).isEqualTo(CONTENT_TYPE);
    assertThat(mediaDto.getFileSize()).isEqualTo(FILE_SIZE);
    assertThat(mediaDto.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(mediaDto.getUpdatedAt()).isEqualTo(UPDATED_AT);
    assertThat(mediaDto.getRecipeId()).isEqualTo(RECIPE_ID);
    assertThat(mediaDto.getIngredientId()).isEqualTo(INGREDIENT_ID);
  }

  @Test
  @DisplayName("Should return true when comparing equal RecipeIngredientMediaDto objects")
  void shouldReturnTrueWhenComparingEqualRecipeIngredientMediaDtoObjects() {
    // Given
    RecipeIngredientMediaDto mediaDto1 = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);
    RecipeIngredientMediaDto mediaDto2 = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);

    // When & Then
    assertThat(mediaDto1).isEqualTo(mediaDto2);
    assertThat(mediaDto2).isEqualTo(mediaDto1);
  }

  @Test
  @DisplayName("Should return false when comparing different RecipeIngredientMediaDto objects")
  void shouldReturnFalseWhenComparingDifferentRecipeIngredientMediaDtoObjects() {
    // Given
    RecipeIngredientMediaDto mediaDto1 = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);
    RecipeIngredientMediaDto mediaDto2 = new RecipeIngredientMediaDto(
        2L, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);

    // When & Then
    assertThat(mediaDto1).isNotEqualTo(mediaDto2);
    assertThat(mediaDto2).isNotEqualTo(mediaDto1);
  }

  @Test
  @DisplayName("Should return false when comparing with different recipe ID")
  void shouldReturnFalseWhenComparingWithDifferentRecipeId() {
    // Given
    RecipeIngredientMediaDto mediaDto1 = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);
    RecipeIngredientMediaDto mediaDto2 = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, 999L, INGREDIENT_ID);

    // When & Then
    assertThat(mediaDto1).isNotEqualTo(mediaDto2);
    assertThat(mediaDto2).isNotEqualTo(mediaDto1);
  }

  @Test
  @DisplayName("Should return false when comparing with different ingredient ID")
  void shouldReturnFalseWhenComparingWithDifferentIngredientId() {
    // Given
    RecipeIngredientMediaDto mediaDto1 = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);
    RecipeIngredientMediaDto mediaDto2 = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, 999L);

    // When & Then
    assertThat(mediaDto1).isNotEqualTo(mediaDto2);
    assertThat(mediaDto2).isNotEqualTo(mediaDto1);
  }

  @Test
  @DisplayName("Should return false when comparing with null")
  void shouldReturnFalseWhenComparingWithNull() {
    // Given
    RecipeIngredientMediaDto mediaDto = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);

    // When & Then
    assertThat(mediaDto).isNotEqualTo(null);
  }

  @Test
  @DisplayName("Should return false when comparing with different type")
  void shouldReturnFalseWhenComparingWithDifferentType() {
    // Given
    RecipeIngredientMediaDto mediaDto = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);
    Object other = new Object();

    // When & Then
    assertThat(mediaDto).isNotEqualTo(other);
  }

  @Test
  @DisplayName("Should return true when comparing with itself")
  void shouldReturnTrueWhenComparingWithItself() {
    // Given
    RecipeIngredientMediaDto mediaDto = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);

    // When & Then
    assertThat(mediaDto).isEqualTo(mediaDto);
  }

  @Test
  @DisplayName("Should have same hashCode for equal objects")
  void shouldHaveSameHashCodeForEqualObjects() {
    // Given
    RecipeIngredientMediaDto mediaDto1 = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);
    RecipeIngredientMediaDto mediaDto2 = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);

    // When & Then
    assertThat(mediaDto1.hashCode()).isEqualTo(mediaDto2.hashCode());
  }

  @Test
  @DisplayName("Should have different hashCode for different objects")
  void shouldHaveDifferentHashCodeForDifferentObjects() {
    // Given
    RecipeIngredientMediaDto mediaDto1 = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);
    RecipeIngredientMediaDto mediaDto2 = new RecipeIngredientMediaDto(
        2L, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);

    // When & Then
    assertThat(mediaDto1.hashCode()).isNotEqualTo(mediaDto2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values in equals")
  void shouldHandleNullValuesInEquals() {
    // Given
    RecipeIngredientMediaDto mediaDto1 = new RecipeIngredientMediaDto(
        null, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);
    RecipeIngredientMediaDto mediaDto2 = new RecipeIngredientMediaDto(
        null, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);
    RecipeIngredientMediaDto mediaDto3 = new RecipeIngredientMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);

    // When & Then
    assertThat(mediaDto1).isEqualTo(mediaDto2);
    assertThat(mediaDto1).isNotEqualTo(mediaDto3);
  }

  @Test
  @DisplayName("Should handle null values in hashCode")
  void shouldHandleNullValuesInHashCode() {
    // Given
    RecipeIngredientMediaDto mediaDto = new RecipeIngredientMediaDto(
        null, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, INGREDIENT_ID);

    // When & Then
    assertThat(mediaDto.hashCode()).isNotNull();
  }
}
