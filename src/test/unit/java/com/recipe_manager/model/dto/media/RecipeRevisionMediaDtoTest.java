package com.recipe_manager.model.dto.media;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeRevisionMediaDto class.
 */
@Tag("unit")
class RecipeRevisionMediaDtoTest {

  private static final Long MEDIA_ID = 1L;
  private static final String URL = "https://example.com/image.jpg";
  private static final String ALT_TEXT = "Example image";
  private static final String CONTENT_TYPE = "image/jpeg";
  private static final Long FILE_SIZE = 1024L;
  private static final LocalDateTime CREATED_AT = LocalDateTime.of(2023, 1, 1, 12, 0);
  private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2023, 1, 2, 12, 0);
  private static final Long RECIPE_ID = 100L;
  private static final Integer REVISION_NUMBER = 1;

  @Test
  @DisplayName("Should create RecipeRevisionMediaDto with constructor")
  @Tag("standard-processing")
  void shouldCreateRecipeRevisionMediaDtoWithConstructor() {
    // When
    RecipeRevisionMediaDto mediaDto = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);

    // Then
    assertThat(mediaDto.getMediaId()).isEqualTo(MEDIA_ID);
    assertThat(mediaDto.getUrl()).isEqualTo(URL);
    assertThat(mediaDto.getAltText()).isEqualTo(ALT_TEXT);
    assertThat(mediaDto.getContentType()).isEqualTo(CONTENT_TYPE);
    assertThat(mediaDto.getFileSize()).isEqualTo(FILE_SIZE);
    assertThat(mediaDto.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(mediaDto.getUpdatedAt()).isEqualTo(UPDATED_AT);
    assertThat(mediaDto.getRecipeId()).isEqualTo(RECIPE_ID);
    assertThat(mediaDto.getRevisionNumber()).isEqualTo(REVISION_NUMBER);
  }

  @Test
  @DisplayName("Should create RecipeRevisionMediaDto with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeRevisionMediaDtoWithBuilder() {
    // When
    RecipeRevisionMediaDto mediaDto = RecipeRevisionMediaDto.builder()
        .mediaId(MEDIA_ID)
        .url(URL)
        .altText(ALT_TEXT)
        .contentType(CONTENT_TYPE)
        .fileSize(FILE_SIZE)
        .createdAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .recipeId(RECIPE_ID)
        .revisionNumber(REVISION_NUMBER)
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
    assertThat(mediaDto.getRevisionNumber()).isEqualTo(REVISION_NUMBER);
  }

  @Test
  @DisplayName("Should set and get RecipeRevisionMediaDto properties")
  @Tag("standard-processing")
  void shouldSetAndGetRecipeRevisionMediaDtoProperties() {
    // Given
    RecipeRevisionMediaDto mediaDto = new RecipeRevisionMediaDto();

    // When
    mediaDto.setMediaId(MEDIA_ID);
    mediaDto.setUrl(URL);
    mediaDto.setAltText(ALT_TEXT);
    mediaDto.setContentType(CONTENT_TYPE);
    mediaDto.setFileSize(FILE_SIZE);
    mediaDto.setCreatedAt(CREATED_AT);
    mediaDto.setUpdatedAt(UPDATED_AT);
    mediaDto.setRecipeId(RECIPE_ID);
    mediaDto.setRevisionNumber(REVISION_NUMBER);

    // Then
    assertThat(mediaDto.getMediaId()).isEqualTo(MEDIA_ID);
    assertThat(mediaDto.getUrl()).isEqualTo(URL);
    assertThat(mediaDto.getAltText()).isEqualTo(ALT_TEXT);
    assertThat(mediaDto.getContentType()).isEqualTo(CONTENT_TYPE);
    assertThat(mediaDto.getFileSize()).isEqualTo(FILE_SIZE);
    assertThat(mediaDto.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(mediaDto.getUpdatedAt()).isEqualTo(UPDATED_AT);
    assertThat(mediaDto.getRecipeId()).isEqualTo(RECIPE_ID);
    assertThat(mediaDto.getRevisionNumber()).isEqualTo(REVISION_NUMBER);
  }

  @Test
  @DisplayName("Should return true when comparing equal RecipeRevisionMediaDto objects")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingEqualRecipeRevisionMediaDtoObjects() {
    // Given
    RecipeRevisionMediaDto mediaDto1 = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);
    RecipeRevisionMediaDto mediaDto2 = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);

    // When & Then
    assertThat(mediaDto1).isEqualTo(mediaDto2);
    assertThat(mediaDto2).isEqualTo(mediaDto1);
  }

  @Test
  @DisplayName("Should return false when comparing different RecipeRevisionMediaDto objects")
  @Tag("standard-processing")
  void shouldReturnFalseWhenComparingDifferentRecipeRevisionMediaDtoObjects() {
    // Given
    RecipeRevisionMediaDto mediaDto1 = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);
    RecipeRevisionMediaDto mediaDto2 = new RecipeRevisionMediaDto(
        2L, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);

    // When & Then
    assertThat(mediaDto1).isNotEqualTo(mediaDto2);
    assertThat(mediaDto2).isNotEqualTo(mediaDto1);
  }

  @Test
  @DisplayName("Should return false when comparing with different recipe ID")
  @Tag("standard-processing")
  void shouldReturnFalseWhenComparingWithDifferentRecipeId() {
    // Given
    RecipeRevisionMediaDto mediaDto1 = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);
    RecipeRevisionMediaDto mediaDto2 = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, 999L, REVISION_NUMBER);

    // When & Then
    assertThat(mediaDto1).isNotEqualTo(mediaDto2);
    assertThat(mediaDto2).isNotEqualTo(mediaDto1);
  }

  @Test
  @DisplayName("Should return false when comparing with different revision number")
  @Tag("standard-processing")
  void shouldReturnFalseWhenComparingWithDifferentRevisionNumber() {
    // Given
    RecipeRevisionMediaDto mediaDto1 = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);
    RecipeRevisionMediaDto mediaDto2 = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, 999);

    // When & Then
    assertThat(mediaDto1).isNotEqualTo(mediaDto2);
    assertThat(mediaDto2).isNotEqualTo(mediaDto1);
  }

  @Test
  @DisplayName("Should return false when comparing with null")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithNull() {
    // Given
    RecipeRevisionMediaDto mediaDto = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);

    // When & Then
    assertThat(mediaDto).isNotEqualTo(null);
  }

  @Test
  @DisplayName("Should return false when comparing with different type")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithDifferentType() {
    // Given
    RecipeRevisionMediaDto mediaDto = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);
    Object other = new Object();

    // When & Then
    assertThat(mediaDto).isNotEqualTo(other);
  }

  @Test
  @DisplayName("Should return true when comparing with itself")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingWithItself() {
    // Given
    RecipeRevisionMediaDto mediaDto = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);

    // When & Then
    assertThat(mediaDto).isEqualTo(mediaDto);
  }

  @Test
  @DisplayName("Should have same hashCode for equal objects")
  @Tag("standard-processing")
  void shouldHaveSameHashCodeForEqualObjects() {
    // Given
    RecipeRevisionMediaDto mediaDto1 = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);
    RecipeRevisionMediaDto mediaDto2 = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);

    // When & Then
    assertThat(mediaDto1.hashCode()).isEqualTo(mediaDto2.hashCode());
  }

  @Test
  @DisplayName("Should have different hashCode for different objects")
  @Tag("standard-processing")
  void shouldHaveDifferentHashCodeForDifferentObjects() {
    // Given
    RecipeRevisionMediaDto mediaDto1 = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);
    RecipeRevisionMediaDto mediaDto2 = new RecipeRevisionMediaDto(
        2L, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);

    // When & Then
    assertThat(mediaDto1.hashCode()).isNotEqualTo(mediaDto2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values in equals")
  @Tag("error-processing")
  void shouldHandleNullValuesInEquals() {
    // Given
    RecipeRevisionMediaDto mediaDto1 = new RecipeRevisionMediaDto(
        null, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);
    RecipeRevisionMediaDto mediaDto2 = new RecipeRevisionMediaDto(
        null, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);
    RecipeRevisionMediaDto mediaDto3 = new RecipeRevisionMediaDto(
        MEDIA_ID, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);

    // When & Then
    assertThat(mediaDto1).isEqualTo(mediaDto2);
    assertThat(mediaDto1).isNotEqualTo(mediaDto3);
  }

  @Test
  @DisplayName("Should handle null values in hashCode")
  @Tag("error-processing")
  void shouldHandleNullValuesInHashCode() {
    // Given
    RecipeRevisionMediaDto mediaDto = new RecipeRevisionMediaDto(
        null, URL, ALT_TEXT, CONTENT_TYPE, FILE_SIZE, CREATED_AT, UPDATED_AT, RECIPE_ID, REVISION_NUMBER);

    // When & Then
    assertThat(mediaDto.hashCode()).isNotNull();
  }
}
