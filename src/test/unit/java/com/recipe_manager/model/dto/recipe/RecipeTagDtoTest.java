package com.recipe_manager.model.dto.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.recipe_manager.model.dto.media.RecipeTagMediaDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeTagDto.
 */
@Tag("unit")
class RecipeTagDtoTest {

  private RecipeTagDto recipeTagDto;

  @BeforeEach
  void setUp() {
    recipeTagDto = RecipeTagDto.builder()
        .tagId(1L)
        .name("Test Tag")
        .build();
  }

  @Test
  @DisplayName("Should create recipe tag DTO with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeTagDtoWithBuilder() {
    // Then
    assertThat(recipeTagDto.getTagId()).isEqualTo(1L);
    assertThat(recipeTagDto.getName()).isEqualTo("Test Tag");
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    Long newTagId = 2L;
    String newName = "Updated Tag";
    LocalDateTime createdAt = LocalDateTime.now();

    // When
    recipeTagDto.setTagId(newTagId);
    recipeTagDto.setName(newName);
    recipeTagDto.setCreatedAt(createdAt);

    // Then
    assertThat(recipeTagDto.getTagId()).isEqualTo(newTagId);
    assertThat(recipeTagDto.getName()).isEqualTo(newName);
    assertThat(recipeTagDto.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should have correct toString representation")
  @Tag("standard-processing")
  void shouldHaveCorrectToStringRepresentation() {
    // When
    String toString = recipeTagDto.toString();

    // Then
    assertThat(toString).contains("RecipeTagDto");
    assertThat(toString).contains("Test Tag");
  }

  @Test
  @DisplayName("Should handle different tag names")
  @Tag("standard-processing")
  void shouldHandleDifferentTagNames() {
    // Given
    RecipeTagDto italianTag = RecipeTagDto.builder()
        .tagId(1L)
        .name("Italian")
        .build();

    RecipeTagDto vegetarianTag = RecipeTagDto.builder()
        .tagId(2L)
        .name("Vegetarian")
        .build();

    RecipeTagDto quickTag = RecipeTagDto.builder()
        .tagId(3L)
        .name("Quick & Easy")
        .build();

    // Then
    assertThat(italianTag.getName()).isEqualTo("Italian");
    assertThat(vegetarianTag.getName()).isEqualTo("Vegetarian");
    assertThat(quickTag.getName()).isEqualTo("Quick & Easy");
  }

  @Test
  @DisplayName("Should handle tag with special characters")
  @Tag("standard-processing")
  void shouldHandleTagWithSpecialCharacters() {
    // Given
    RecipeTagDto specialTag = RecipeTagDto.builder()
        .tagId(1L)
        .name("Tag with @#$% symbols & numbers 123")
        .build();

    // Then
    assertThat(specialTag.getName()).isEqualTo("Tag with @#$% symbols & numbers 123");
  }

  @Test
  @DisplayName("Should handle tag with maximum length name")
  @Tag("standard-processing")
  void shouldHandleTagWithMaximumLengthName() {
    // Given
    String maxLengthName = "A".repeat(50); // Maximum length per schema
    RecipeTagDto maxTag = RecipeTagDto.builder()
        .tagId(1L)
        .name(maxLengthName)
        .build();

    // Then
    assertThat(maxTag.getName()).isEqualTo(maxLengthName);
    assertThat(maxTag.getName().length()).isEqualTo(50);
  }

  @Test
  @DisplayName("Builder should defensively copy media list")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyMediaList() {
    List<RecipeTagMediaDto> media = new ArrayList<>();
    media.add(new RecipeTagMediaDto());
    RecipeTagDto dto = RecipeTagDto.builder()
        .media(media)
        .build();
    media.add(new RecipeTagMediaDto());
    assertThat(dto.getMedia()).hasSize(1);
  }

  @Test
  @DisplayName("Builder should handle null media as empty")
  @Tag("error-processing")
  void builderShouldHandleNullMediaAsEmpty() {
    RecipeTagDto dto = RecipeTagDto.builder()
        .media(null)
        .build();
    assertThat(dto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("toString should include key fields and not be null")
  @Tag("standard-processing")
  void toStringShouldIncludeKeyFields() {
    RecipeTagDto dto = RecipeTagDto.builder()
        .tagId(1L)
        .name("Test Tag")
        .build();
    String str = dto.toString();
    assertThat(str).isNotNull();
    assertThat(str).contains("RecipeTagDto");
    assertThat(str).contains("Test Tag");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    RecipeTagDto dto = new RecipeTagDto(null, null, null, null, null, null);
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getTagId()).isNull();
    assertThat(dto.getName()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
    assertThat(dto.getMedia()).isEmpty();
  }
}
