package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeTagDto;
import com.recipe_manager.model.entity.recipe.RecipeTag;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/**
 * Unit tests for RecipeTagMapper.
 */
@Tag("unit")
class RecipeTagMapperTest {

  private final RecipeTagMapper mapper = Mappers.getMapper(RecipeTagMapper.class);

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map RecipeTag entity to RecipeTagDto")
  void shouldMapEntityToDto() {
    LocalDateTime createdAt = LocalDateTime.now();

    RecipeTag entity = RecipeTag.builder()
        .tagId(100L)
        .name("Breakfast")
        .createdAt(createdAt)
        .build();

    RecipeTagDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(100L);
    assertThat(result.getName()).isEqualTo("Breakfast");
    assertThat(result.getCreatedAt()).isEqualTo(createdAt);
    // Ignored fields should be null or default values
    assertThat(result.getRecipeId()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
    assertThat(result.getMedia()).isNotNull().isEmpty(); // MapStruct returns empty list for @Default fields
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map a list of RecipeTag entities to RecipeTagDto list")
  void shouldMapEntityListToDto() {
    LocalDateTime now = LocalDateTime.now();

    RecipeTag entity1 = RecipeTag.builder()
        .tagId(200L)
        .name("Lunch")
        .createdAt(now)
        .build();

    RecipeTag entity2 = RecipeTag.builder()
        .tagId(300L)
        .name("Dinner")
        .createdAt(now.plusHours(1))
        .build();

    List<RecipeTagDto> results = mapper.toDtoList(List.of(entity1, entity2));

    assertThat(results).hasSize(2);
    assertThat(results.get(0).getTagId()).isEqualTo(200L);
    assertThat(results.get(0).getName()).isEqualTo("Lunch");
    assertThat(results.get(0).getCreatedAt()).isEqualTo(now);
    assertThat(results.get(1).getTagId()).isEqualTo(300L);
    assertThat(results.get(1).getName()).isEqualTo("Dinner");
    assertThat(results.get(1).getCreatedAt()).isEqualTo(now.plusHours(1));
    // All should have ignored fields as null
    assertThat(results.get(0).getRecipeId()).isNull();
    assertThat(results.get(0).getUpdatedAt()).isNull();
    assertThat(results.get(1).getRecipeId()).isNull();
    assertThat(results.get(1).getUpdatedAt()).isNull();
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null createdAt")
  void shouldHandleNullCreatedAt() {
    RecipeTag entity = RecipeTag.builder()
        .tagId(400L)
        .name("Snack")
        .createdAt(null)
        .build();

    RecipeTagDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(400L);
    assertThat(result.getName()).isEqualTo("Snack");
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle empty tag name")
  void shouldHandleEmptyTagName() {
    LocalDateTime createdAt = LocalDateTime.now();

    RecipeTag entity = RecipeTag.builder()
        .tagId(500L)
        .name("")
        .createdAt(createdAt)
        .build();

    RecipeTagDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(500L);
    assertThat(result.getName()).isEqualTo("");
    assertThat(result.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null tag name")
  void shouldHandleNullTagName() {
    LocalDateTime createdAt = LocalDateTime.now();

    RecipeTag entity = RecipeTag.builder()
        .tagId(600L)
        .name(null)
        .createdAt(createdAt)
        .build();

    RecipeTagDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(600L);
    assertThat(result.getName()).isNull();
    assertThat(result.getCreatedAt()).isEqualTo(createdAt);
  }
}
