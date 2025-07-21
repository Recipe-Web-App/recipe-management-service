package com.recipe_manager.model.dto.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.recipe_manager.model.dto.media.RecipeIngredientMediaDto;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeIngredientDto.
 */
@Tag("unit")
class RecipeIngredientDtoTest {

  private RecipeIngredientDto recipeIngredientDto;

  @BeforeEach
  void setUp() {
    recipeIngredientDto = RecipeIngredientDto.builder()
        .ingredientId(1L)
        .ingredientName("Test Ingredient")
        .quantity(BigDecimal.valueOf(2.5))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .notes("Test notes")
        .build();
  }

  @Test
  @DisplayName("Should create recipe ingredient DTO with constructor")
  @Tag("standard-processing")
  void shouldCreateRecipeIngredientDtoWithConstructor() {
    // Given
    List<RecipeIngredientMediaDto> media = Arrays.asList(new RecipeIngredientMediaDto());
    LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 12, 0);

    // When
    RecipeIngredientDto dto = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        createdAt, updatedAt, media);

    // Then
    assertThat(dto.getRecipeId()).isEqualTo(1L);
    assertThat(dto.getIngredientId()).isEqualTo(1L);
    assertThat(dto.getIngredientName()).isEqualTo("Test Ingredient");
    assertThat(dto.getQuantity()).isEqualTo(BigDecimal.valueOf(2.5));
    assertThat(dto.getUnit()).isEqualTo(IngredientUnit.CUP);
    assertThat(dto.getIsOptional()).isFalse();
    assertThat(dto.getNotes()).isEqualTo("Test notes");
    assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    assertThat(dto.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(dto.getMedia()).isEqualTo(media);
  }

  @Test
  @DisplayName("Should create recipe ingredient DTO with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeIngredientDtoWithBuilder() {
    // Then
    assertThat(recipeIngredientDto.getIngredientId()).isEqualTo(1L);
    assertThat(recipeIngredientDto.getIngredientName()).isEqualTo("Test Ingredient");
    assertThat(recipeIngredientDto.getQuantity()).isEqualTo(BigDecimal.valueOf(2.5));
    assertThat(recipeIngredientDto.getUnit()).isEqualTo(IngredientUnit.CUP);
    assertThat(recipeIngredientDto.getIsOptional()).isFalse();
    assertThat(recipeIngredientDto.getNotes()).isEqualTo("Test notes");
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    Long newRecipeId = 2L;
    Long newIngredientId = 2L;
    String newIngredientName = "Updated Ingredient";
    BigDecimal newQuantity = BigDecimal.valueOf(3.0);
    IngredientUnit newUnit = IngredientUnit.TBSP;
    Boolean newIsOptional = true;
    String newNotes = "Updated notes";

    // When
    recipeIngredientDto.setRecipeId(newRecipeId);
    recipeIngredientDto.setIngredientId(newIngredientId);
    recipeIngredientDto.setIngredientName(newIngredientName);
    recipeIngredientDto.setQuantity(newQuantity);
    recipeIngredientDto.setUnit(newUnit);
    recipeIngredientDto.setIsOptional(newIsOptional);
    recipeIngredientDto.setNotes(newNotes);

    // Then
    assertThat(recipeIngredientDto.getRecipeId()).isEqualTo(newRecipeId);
    assertThat(recipeIngredientDto.getIngredientId()).isEqualTo(newIngredientId);
    assertThat(recipeIngredientDto.getIngredientName()).isEqualTo(newIngredientName);
    assertThat(recipeIngredientDto.getQuantity()).isEqualTo(newQuantity);
    assertThat(recipeIngredientDto.getUnit()).isEqualTo(newUnit);
    assertThat(recipeIngredientDto.getIsOptional()).isEqualTo(newIsOptional);
    assertThat(recipeIngredientDto.getNotes()).isEqualTo(newNotes);
  }

  @Test
  @DisplayName("Should return unmodifiable list for media")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForMedia() {
    // Given
    List<RecipeIngredientMediaDto> media = new ArrayList<>();
    recipeIngredientDto.setMedia(media);

    // When & Then
    assertThat(recipeIngredientDto.getMedia()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should defensively copy media list")
  @Tag("standard-processing")
  void shouldDefensivelyCopyMediaList() {
    // Given
    List<RecipeIngredientMediaDto> originalMedia = new ArrayList<>();
    originalMedia.add(new RecipeIngredientMediaDto());
    recipeIngredientDto.setMedia(originalMedia);

    // When
    originalMedia.add(new RecipeIngredientMediaDto());

    // Then
    assertThat(recipeIngredientDto.getMedia()).hasSize(1);
  }

  @Test
  @DisplayName("Should handle null media in constructor")
  @Tag("error-processing")
  void shouldHandleNullMediaInConstructor() {
    // When
    RecipeIngredientDto dto = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        LocalDateTime.now(), LocalDateTime.now(), null);

    // Then
    assertThat(dto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("Should handle null media in setter")
  @Tag("error-processing")
  void shouldHandleNullMediaInSetter() {
    // Given
    recipeIngredientDto.setMedia(Arrays.asList(new RecipeIngredientMediaDto()));

    // When
    recipeIngredientDto.setMedia(null);

    // Then
    assertThat(recipeIngredientDto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("Should return true when comparing equal RecipeIngredientDto objects")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingEqualRecipeIngredientDtoObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeIngredientDto dto1 = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());
    RecipeIngredientDto dto2 = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1).isEqualTo(dto2);
    assertThat(dto2).isEqualTo(dto1);
  }

  @Test
  @DisplayName("Should return false when comparing different RecipeIngredientDto objects")
  @Tag("standard-processing")
  void shouldReturnFalseWhenComparingDifferentRecipeIngredientDtoObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeIngredientDto dto1 = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());
    RecipeIngredientDto dto2 = new RecipeIngredientDto(2L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1).isNotEqualTo(dto2);
    assertThat(dto2).isNotEqualTo(dto1);
  }

  @Test
  @DisplayName("Should return false when comparing with null")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithNull() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeIngredientDto dto = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto).isNotEqualTo(null);
  }

  @Test
  @DisplayName("Should return false when comparing with different type")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithDifferentType() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeIngredientDto dto = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());
    Object other = new Object();

    // When & Then
    assertThat(dto).isNotEqualTo(other);
  }

  @Test
  @DisplayName("Should return true when comparing with itself")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingWithItself() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeIngredientDto dto = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto).isEqualTo(dto);
  }

  @Test
  @DisplayName("Should have same hashCode for equal objects")
  @Tag("standard-processing")
  void shouldHaveSameHashCodeForEqualObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeIngredientDto dto1 = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());
    RecipeIngredientDto dto2 = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
  }

  @Test
  @DisplayName("Should have different hashCode for different objects")
  @Tag("standard-processing")
  void shouldHaveDifferentHashCodeForDifferentObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeIngredientDto dto1 = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());
    RecipeIngredientDto dto2 = new RecipeIngredientDto(2L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1.hashCode()).isNotEqualTo(dto2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values in equals")
  @Tag("error-processing")
  void shouldHandleNullValuesInEquals() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeIngredientDto dto1 = new RecipeIngredientDto(null, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());
    RecipeIngredientDto dto2 = new RecipeIngredientDto(null, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());
    RecipeIngredientDto dto3 = new RecipeIngredientDto(1L, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1).isEqualTo(dto2);
    assertThat(dto1).isNotEqualTo(dto3);
  }

  @Test
  @DisplayName("Should handle null values in hashCode")
  @Tag("error-processing")
  void shouldHandleNullValuesInHashCode() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeIngredientDto dto = new RecipeIngredientDto(null, 1L, "Test Ingredient",
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto.hashCode()).isNotNull();
  }

  @Test
  @DisplayName("Builder should defensively copy media list")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyMediaList() {
    List<RecipeIngredientMediaDto> media = new ArrayList<>();
    media.add(new RecipeIngredientMediaDto());
    RecipeIngredientDto dto = RecipeIngredientDto.builder()
        .media(media)
        .build();
    media.add(new RecipeIngredientMediaDto());
    assertThat(dto.getMedia()).hasSize(1);
  }

  @Test
  @DisplayName("Builder should handle null media as empty")
  @Tag("error-processing")
  void builderShouldHandleNullMediaAsEmpty() {
    RecipeIngredientDto dto = RecipeIngredientDto.builder()
        .media(null)
        .build();
    assertThat(dto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("toString should include key fields and not be null")
  @Tag("standard-processing")
  void toStringShouldIncludeKeyFields() {
    RecipeIngredientDto dto = RecipeIngredientDto.builder()
        .ingredientId(1L)
        .ingredientName("Test Ingredient")
        .quantity(BigDecimal.valueOf(2.5))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .notes("Test notes")
        .build();
    String str = dto.toString();
    assertThat(str).isNotNull();
    assertThat(str).contains("RecipeIngredientDto");
    assertThat(str).contains("Test Ingredient");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    RecipeIngredientDto dto = new RecipeIngredientDto(null, null, null, null, null, null, null, null, null, null);
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getIngredientId()).isNull();
    assertThat(dto.getIngredientName()).isNull();
    assertThat(dto.getQuantity()).isNull();
    assertThat(dto.getUnit()).isNull();
    assertThat(dto.getIsOptional()).isNull();
    assertThat(dto.getNotes()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
    assertThat(dto.getMedia()).isEmpty();
  }
}
