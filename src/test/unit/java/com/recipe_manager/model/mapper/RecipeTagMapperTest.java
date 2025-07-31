package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

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
    RecipeTag entity = RecipeTag.builder()
        .tagId(100L)
        .name("Breakfast")
        .build();

    RecipeTagDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(100L);
    assertThat(result.getName()).isEqualTo("Breakfast");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map a list of RecipeTag entities to RecipeTagDto list")
  void shouldMapEntityListToDto() {
    RecipeTag entity1 = RecipeTag.builder()
        .tagId(200L)
        .name("Lunch")
        .build();

    RecipeTag entity2 = RecipeTag.builder()
        .tagId(300L)
        .name("Dinner")
        .build();

    List<RecipeTagDto> results = mapper.toDtoList(List.of(entity1, entity2));

    assertThat(results).hasSize(2);
    assertThat(results.get(0).getTagId()).isEqualTo(200L);
    assertThat(results.get(0).getName()).isEqualTo("Lunch");
    assertThat(results.get(1).getTagId()).isEqualTo(300L);
    assertThat(results.get(1).getName()).isEqualTo("Dinner");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null createdAt")
  void shouldHandleNullCreatedAt() {
    RecipeTag entity = RecipeTag.builder()
        .tagId(400L)
        .name("Snack")
        .build();

    RecipeTagDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(400L);
    assertThat(result.getName()).isEqualTo("Snack");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle empty tag name")
  void shouldHandleEmptyTagName() {
    RecipeTag entity = RecipeTag.builder()
        .tagId(500L)
        .name("")
        .build();

    RecipeTagDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(500L);
    assertThat(result.getName()).isEqualTo("");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null tag name")
  void shouldHandleNullTagName() {
    RecipeTag entity = RecipeTag.builder()
        .tagId(600L)
        .name(null)
        .build();

    RecipeTagDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(600L);
    assertThat(result.getName()).isNull();
  }
}
