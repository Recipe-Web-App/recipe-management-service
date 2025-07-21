package com.recipe_manager.model.dto.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.recipe_manager.model.dto.media.RecipeStepMediaDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeStepDto.
 */
@Tag("unit")
class RecipeStepDtoTest {

  private RecipeStepDto recipeStepDto;

  @BeforeEach
  void setUp() {
    recipeStepDto = RecipeStepDto.builder()
        .stepId(1L)
        .recipeId(1L)
        .stepNumber(1)
        .instruction("Test instruction")
        .optional(false)
        .timerSeconds(300)
        .build();
  }

  @Test
  @DisplayName("Should create recipe step DTO with constructor")
  @Tag("standard-processing")
  void shouldCreateRecipeStepDtoWithConstructor() {
    // Given
    List<RecipeStepMediaDto> media = Arrays.asList(new RecipeStepMediaDto());
    LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 12, 0);

    // When
    RecipeStepDto dto = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, createdAt, updatedAt, media);

    // Then
    assertThat(dto.getStepId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(1L);
    assertThat(dto.getStepNumber()).isEqualTo(1);
    assertThat(dto.getInstruction()).isEqualTo("Test instruction");
    assertThat(dto.getOptional()).isFalse();
    assertThat(dto.getTimerSeconds()).isEqualTo(300);
    assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    assertThat(dto.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(dto.getMedia()).isEqualTo(media);
  }

  @Test
  @DisplayName("Should create recipe step DTO with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeStepDtoWithBuilder() {
    // Then
    assertThat(recipeStepDto.getStepId()).isEqualTo(1L);
    assertThat(recipeStepDto.getRecipeId()).isEqualTo(1L);
    assertThat(recipeStepDto.getStepNumber()).isEqualTo(1);
    assertThat(recipeStepDto.getInstruction()).isEqualTo("Test instruction");
    assertThat(recipeStepDto.getOptional()).isFalse();
    assertThat(recipeStepDto.getTimerSeconds()).isEqualTo(300);
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    Long newStepId = 2L;
    Long newRecipeId = 2L;
    Integer newStepNumber = 2;
    String newInstruction = "Updated instruction";
    Boolean newOptional = true;
    Integer newTimerSeconds = 600;
    LocalDateTime createdAt = LocalDateTime.now();

    // When
    recipeStepDto.setStepId(newStepId);
    recipeStepDto.setRecipeId(newRecipeId);
    recipeStepDto.setStepNumber(newStepNumber);
    recipeStepDto.setInstruction(newInstruction);
    recipeStepDto.setOptional(newOptional);
    recipeStepDto.setTimerSeconds(newTimerSeconds);
    recipeStepDto.setCreatedAt(createdAt);

    // Then
    assertThat(recipeStepDto.getStepId()).isEqualTo(newStepId);
    assertThat(recipeStepDto.getRecipeId()).isEqualTo(newRecipeId);
    assertThat(recipeStepDto.getStepNumber()).isEqualTo(newStepNumber);
    assertThat(recipeStepDto.getInstruction()).isEqualTo(newInstruction);
    assertThat(recipeStepDto.getOptional()).isEqualTo(newOptional);
    assertThat(recipeStepDto.getTimerSeconds()).isEqualTo(newTimerSeconds);
    assertThat(recipeStepDto.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should return unmodifiable list for media")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForMedia() {
    // Given
    List<RecipeStepMediaDto> media = new ArrayList<>();
    recipeStepDto.setMedia(media);

    // When & Then
    assertThat(recipeStepDto.getMedia()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should defensively copy media list")
  @Tag("standard-processing")
  void shouldDefensivelyCopyMediaList() {
    // Given
    List<RecipeStepMediaDto> originalMedia = new ArrayList<>();
    originalMedia.add(new RecipeStepMediaDto());
    recipeStepDto.setMedia(originalMedia);

    // When
    originalMedia.add(new RecipeStepMediaDto());

    // Then
    assertThat(recipeStepDto.getMedia()).hasSize(1);
  }

  @Test
  @DisplayName("Should handle null media in constructor")
  @Tag("error-processing")
  void shouldHandleNullMediaInConstructor() {
    // When
    RecipeStepDto dto = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, LocalDateTime.now(), LocalDateTime.now(), null);

    // Then
    assertThat(dto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("Should handle null media in setter")
  @Tag("error-processing")
  void shouldHandleNullMediaInSetter() {
    // Given
    recipeStepDto.setMedia(Arrays.asList(new RecipeStepMediaDto()));

    // When
    recipeStepDto.setMedia(null);

    // Then
    assertThat(recipeStepDto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("Should return true when comparing equal RecipeStepDto objects")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingEqualRecipeStepDtoObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeStepDto dto1 = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());
    RecipeStepDto dto2 = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1).isEqualTo(dto2);
    assertThat(dto2).isEqualTo(dto1);
  }

  @Test
  @DisplayName("Should return false when comparing different RecipeStepDto objects")
  @Tag("standard-processing")
  void shouldReturnFalseWhenComparingDifferentRecipeStepDtoObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeStepDto dto1 = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());
    RecipeStepDto dto2 = new RecipeStepDto(2L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());

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
    RecipeStepDto dto = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto).isNotEqualTo(null);
  }

  @Test
  @DisplayName("Should return false when comparing with different type")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithDifferentType() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeStepDto dto = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());
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
    RecipeStepDto dto = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto).isEqualTo(dto);
  }

  @Test
  @DisplayName("Should have same hashCode for equal objects")
  @Tag("standard-processing")
  void shouldHaveSameHashCodeForEqualObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeStepDto dto1 = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());
    RecipeStepDto dto2 = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
  }

  @Test
  @DisplayName("Should have different hashCode for different objects")
  @Tag("standard-processing")
  void shouldHaveDifferentHashCodeForDifferentObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeStepDto dto1 = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());
    RecipeStepDto dto2 = new RecipeStepDto(2L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1.hashCode()).isNotEqualTo(dto2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values in equals")
  @Tag("error-processing")
  void shouldHandleNullValuesInEquals() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeStepDto dto1 = new RecipeStepDto(null, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());
    RecipeStepDto dto2 = new RecipeStepDto(null, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());
    RecipeStepDto dto3 = new RecipeStepDto(1L, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());

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
    RecipeStepDto dto = new RecipeStepDto(null, 1L, 1, "Test instruction",
        false, 300, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto.hashCode()).isNotNull();
  }

  @Test
  @DisplayName("Builder should defensively copy media list")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyMediaList() {
    List<RecipeStepMediaDto> media = new ArrayList<>();
    media.add(new RecipeStepMediaDto());
    RecipeStepDto dto = RecipeStepDto.builder()
        .media(media)
        .build();
    media.add(new RecipeStepMediaDto());
    assertThat(dto.getMedia()).hasSize(1);
  }

  @Test
  @DisplayName("Builder should handle null media as empty")
  @Tag("error-processing")
  void builderShouldHandleNullMediaAsEmpty() {
    RecipeStepDto dto = RecipeStepDto.builder()
        .media(null)
        .build();
    assertThat(dto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("toString should include key fields and not be null")
  @Tag("standard-processing")
  void toStringShouldIncludeKeyFields() {
    RecipeStepDto dto = RecipeStepDto.builder()
        .stepId(1L)
        .recipeId(1L)
        .stepNumber(1)
        .instruction("Test instruction")
        .optional(false)
        .timerSeconds(300)
        .build();
    String str = dto.toString();
    assertThat(str).isNotNull();
    assertThat(str).contains("RecipeStepDto");
    assertThat(str).contains("Test instruction");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    RecipeStepDto dto = new RecipeStepDto(null, null, null, null, null, null, null, null, null);
    assertThat(dto.getStepId()).isNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getStepNumber()).isNull();
    assertThat(dto.getInstruction()).isNull();
    assertThat(dto.getOptional()).isNull();
    assertThat(dto.getTimerSeconds()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
    assertThat(dto.getMedia()).isEmpty();
  }
}
