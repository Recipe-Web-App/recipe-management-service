package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeTagDto;
import com.recipe_manager.model.entity.recipe.Recipe;
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
  @Test
  @DisplayName("Should map RecipeTag entity to RecipeTagDto")
  void shouldMapRecipeTagEntityToDto() {
    RecipeTag recipeTag = RecipeTag.builder()
        .tagId(1L)
        .name("vegetarian")
        .recipes(new java.util.ArrayList<>())
        .build();
    RecipeTagDto result = mapper.toDto(recipeTag);
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("vegetarian");
  }

  @Test
  @DisplayName("Should handle null RecipeTag entity")
  void shouldHandleNullRecipeTagEntity() {
    RecipeTagDto result = mapper.toDto(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeTag with null name")
  void shouldHandleRecipeTagWithNullName() {
    RecipeTag tagWithNullName = RecipeTag.builder()
        .tagId(2L)
        .name(null)
        .recipes(new java.util.ArrayList<>())
        .build();
    RecipeTagDto result = mapper.toDto(tagWithNullName);
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(2L);
    assertThat(result.getName()).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeTag with null recipes list")
  void shouldHandleRecipeTagWithNullRecipesList() {
    RecipeTag tagWithNullRecipes = RecipeTag.builder()
        .tagId(3L)
        .name("gluten-free")
        .recipes(null)
        .build();
    RecipeTagDto result = mapper.toDto(tagWithNullRecipes);
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(3L);
    assertThat(result.getName()).isEqualTo("gluten-free");
  }

  @Test
  @DisplayName("Should handle RecipeTag with populated recipes list")
  void shouldHandleRecipeTagWithPopulatedRecipesList() {
    Recipe recipe1 = Recipe.builder().recipeId(1L).title("Recipe 1").build();
    Recipe recipe2 = Recipe.builder().recipeId(2L).title("Recipe 2").build();
    RecipeTag tagWithRecipes = RecipeTag.builder()
        .tagId(4L)
        .name("quick-meals")
        .recipes(java.util.Arrays.asList(recipe1, recipe2))
        .build();
    RecipeTagDto result = mapper.toDto(tagWithRecipes);
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(4L);
    assertThat(result.getName()).isEqualTo("quick-meals");
  }

  @Test
  @DisplayName("Should map list of RecipeTag entities to list of DTOs")
  void shouldMapListOfRecipeTagEntitiesToDtoList() {
    RecipeTag recipeTag = RecipeTag.builder()
        .tagId(1L)
        .name("vegetarian")
        .recipes(new java.util.ArrayList<>())
        .build();
    RecipeTag tag2 = RecipeTag.builder()
        .tagId(2L)
        .name("dairy-free")
        .recipes(new java.util.ArrayList<>())
        .build();
    RecipeTag tag3 = RecipeTag.builder()
        .tagId(3L)
        .name("low-carb")
        .recipes(new java.util.ArrayList<>())
        .build();
    java.util.List<RecipeTag> tags = java.util.Arrays.asList(recipeTag, tag2, tag3);
    java.util.List<RecipeTagDto> result = mapper.toDtoList(tags);
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getTagId()).isEqualTo(1L);
    assertThat(result.get(0).getName()).isEqualTo("vegetarian");
    assertThat(result.get(1).getTagId()).isEqualTo(2L);
    assertThat(result.get(1).getName()).isEqualTo("dairy-free");
    assertThat(result.get(2).getTagId()).isEqualTo(3L);
    assertThat(result.get(2).getName()).isEqualTo("low-carb");
  }

  @Test
  @DisplayName("Should handle null list")
  void shouldHandleNullList() {
    java.util.List<RecipeTagDto> result = mapper.toDtoList(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle empty list")
  void shouldHandleEmptyList() {
    java.util.List<RecipeTagDto> result = mapper.toDtoList(java.util.Arrays.asList());
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should handle list with null elements")
  void shouldHandleListWithNullElements() {
    RecipeTag recipeTag = RecipeTag.builder()
        .tagId(1L)
        .name("vegetarian")
        .recipes(new java.util.ArrayList<>())
        .build();
    java.util.List<RecipeTag> tagsWithNull = java.util.Arrays.asList(recipeTag, null);
    java.util.List<RecipeTagDto> result = mapper.toDtoList(tagsWithNull);
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isNotNull();
    assertThat(result.get(0).getTagId()).isEqualTo(1L);
    assertThat(result.get(0).getName()).isEqualTo("vegetarian");
    assertThat(result.get(1)).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeTag with empty string name")
  void shouldHandleRecipeTagWithEmptyStringName() {
    RecipeTag tagWithEmptyName = RecipeTag.builder()
        .tagId(5L)
        .name("")
        .recipes(new java.util.ArrayList<>())
        .build();
    RecipeTagDto result = mapper.toDto(tagWithEmptyName);
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(5L);
    assertThat(result.getName()).isEmpty();
  }

  @Test
  @DisplayName("Should handle RecipeTag with long name")
  void shouldHandleRecipeTagWithLongName() {
    String longName = "a-very-long-tag-name-that-might-be-at-the-limit";
    RecipeTag tagWithLongName = RecipeTag.builder()
        .tagId(6L)
        .name(longName)
        .recipes(new java.util.ArrayList<>())
        .build();
    RecipeTagDto result = mapper.toDto(tagWithLongName);
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(6L);
    assertThat(result.getName()).isEqualTo(longName);
  }

  @Test
  @DisplayName("Should handle RecipeTag with special characters in name")
  void shouldHandleRecipeTagWithSpecialCharactersInName() {
    String specialName = "spicy-🌶️-food";
    RecipeTag tagWithSpecialName = RecipeTag.builder()
        .tagId(7L)
        .name(specialName)
        .recipes(new java.util.ArrayList<>())
        .build();
    RecipeTagDto result = mapper.toDto(tagWithSpecialName);
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(7L);
    assertThat(result.getName()).isEqualTo(specialName);
  }

  @Test
  @DisplayName("Should handle RecipeTag with null tagId")
  void shouldHandleRecipeTagWithNullTagId() {
    RecipeTag tagWithNullId = RecipeTag.builder()
        .tagId(null)
        .name("no-id-tag")
        .recipes(new java.util.ArrayList<>())
        .build();
    RecipeTagDto result = mapper.toDto(tagWithNullId);
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isNull();
    assertThat(result.getName()).isEqualTo("no-id-tag");
  }

  @Test
  @DisplayName("Should handle large list of tags")
  void shouldHandleLargeListOfTags() {
    java.util.List<RecipeTag> largeTags = new java.util.ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      largeTags.add(RecipeTag.builder()
          .tagId((long) i)
          .name("tag-" + i)
          .recipes(new java.util.ArrayList<>())
          .build());
    }
    java.util.List<RecipeTagDto> result = mapper.toDtoList(largeTags);
    assertThat(result).isNotNull();
    assertThat(result).hasSize(100);
    assertThat(result.get(0).getTagId()).isEqualTo(1L);
    assertThat(result.get(0).getName()).isEqualTo("tag-1");
    assertThat(result.get(99).getTagId()).isEqualTo(100L);
    assertThat(result.get(99).getName()).isEqualTo("tag-100");
  }

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
