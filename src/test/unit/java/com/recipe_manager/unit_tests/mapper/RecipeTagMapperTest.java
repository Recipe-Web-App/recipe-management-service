package com.recipe_manager.unit_tests.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.recipe_manager.model.dto.recipe.RecipeTagDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeTag;
import com.recipe_manager.model.mapper.RecipeTagMapper;

@org.junit.jupiter.api.Tag("unit")
class RecipeTagMapperTest {

  private final RecipeTagMapper recipeTagMapper = Mappers.getMapper(RecipeTagMapper.class);

  private RecipeTag recipeTag;

  @BeforeEach
  void setUp() {
    recipeTag = RecipeTag.builder()
        .tagId(1L)
        .name("vegetarian")
        .recipes(new ArrayList<>())
        .build();
  }

  @Test
  @DisplayName("Should map RecipeTag entity to RecipeTagDto")
  void shouldMapRecipeTagEntityToDto() {
    // Act
    RecipeTagDto result = recipeTagMapper.toDto(recipeTag);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("vegetarian");
  }

  @Test
  @DisplayName("Should handle null RecipeTag entity")
  void shouldHandleNullRecipeTagEntity() {
    // Act
    RecipeTagDto result = recipeTagMapper.toDto(null);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeTag with null name")
  void shouldHandleRecipeTagWithNullName() {
    // Arrange
    RecipeTag tagWithNullName = RecipeTag.builder()
        .tagId(2L)
        .name(null)
        .recipes(new ArrayList<>())
        .build();

    // Act
    RecipeTagDto result = recipeTagMapper.toDto(tagWithNullName);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(2L);
    assertThat(result.getName()).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeTag with null recipes list")
  void shouldHandleRecipeTagWithNullRecipesList() {
    // Arrange
    RecipeTag tagWithNullRecipes = RecipeTag.builder()
        .tagId(3L)
        .name("gluten-free")
        .recipes(null)
        .build();

    // Act
    RecipeTagDto result = recipeTagMapper.toDto(tagWithNullRecipes);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(3L);
    assertThat(result.getName()).isEqualTo("gluten-free");
  }

  @Test
  @DisplayName("Should handle RecipeTag with populated recipes list")
  void shouldHandleRecipeTagWithPopulatedRecipesList() {
    // Arrange
    Recipe recipe1 = Recipe.builder().recipeId(1L).title("Recipe 1").build();
    Recipe recipe2 = Recipe.builder().recipeId(2L).title("Recipe 2").build();

    RecipeTag tagWithRecipes = RecipeTag.builder()
        .tagId(4L)
        .name("quick-meals")
        .recipes(Arrays.asList(recipe1, recipe2))
        .build();

    // Act
    RecipeTagDto result = recipeTagMapper.toDto(tagWithRecipes);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(4L);
    assertThat(result.getName()).isEqualTo("quick-meals");
    // The recipes list should not affect the DTO mapping
  }

  @Test
  @DisplayName("Should map list of RecipeTag entities to list of DTOs")
  void shouldMapListOfRecipeTagEntitiesToDtoList() {
    // Arrange
    RecipeTag tag2 = RecipeTag.builder()
        .tagId(2L)
        .name("dairy-free")
        .recipes(new ArrayList<>())
        .build();

    RecipeTag tag3 = RecipeTag.builder()
        .tagId(3L)
        .name("low-carb")
        .recipes(new ArrayList<>())
        .build();

    List<RecipeTag> tags = Arrays.asList(recipeTag, tag2, tag3);

    // Act
    List<RecipeTagDto> result = recipeTagMapper.toDtoList(tags);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3);

    RecipeTagDto dto1 = result.get(0);
    assertThat(dto1.getTagId()).isEqualTo(1L);
    assertThat(dto1.getName()).isEqualTo("vegetarian");

    RecipeTagDto dto2 = result.get(1);
    assertThat(dto2.getTagId()).isEqualTo(2L);
    assertThat(dto2.getName()).isEqualTo("dairy-free");

    RecipeTagDto dto3 = result.get(2);
    assertThat(dto3.getTagId()).isEqualTo(3L);
    assertThat(dto3.getName()).isEqualTo("low-carb");
  }

  @Test
  @DisplayName("Should handle null list")
  void shouldHandleNullList() {
    // Act
    List<RecipeTagDto> result = recipeTagMapper.toDtoList(null);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle empty list")
  void shouldHandleEmptyList() {
    // Act
    List<RecipeTagDto> result = recipeTagMapper.toDtoList(Arrays.asList());

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should handle list with null elements")
  void shouldHandleListWithNullElements() {
    // Arrange
    List<RecipeTag> tagsWithNull = Arrays.asList(recipeTag, null);

    // Act
    List<RecipeTagDto> result = recipeTagMapper.toDtoList(tagsWithNull);

    // Assert
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
    // Arrange
    RecipeTag tagWithEmptyName = RecipeTag.builder()
        .tagId(5L)
        .name("")
        .recipes(new ArrayList<>())
        .build();

    // Act
    RecipeTagDto result = recipeTagMapper.toDto(tagWithEmptyName);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(5L);
    assertThat(result.getName()).isEmpty();
  }

  @Test
  @DisplayName("Should handle RecipeTag with long name")
  void shouldHandleRecipeTagWithLongName() {
    // Arrange
    String longName = "a-very-long-tag-name-that-might-be-at-the-limit";
    RecipeTag tagWithLongName = RecipeTag.builder()
        .tagId(6L)
        .name(longName)
        .recipes(new ArrayList<>())
        .build();

    // Act
    RecipeTagDto result = recipeTagMapper.toDto(tagWithLongName);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(6L);
    assertThat(result.getName()).isEqualTo(longName);
  }

  @Test
  @DisplayName("Should handle RecipeTag with special characters in name")
  void shouldHandleRecipeTagWithSpecialCharactersInName() {
    // Arrange
    String specialName = "spicy-🌶️-food";
    RecipeTag tagWithSpecialName = RecipeTag.builder()
        .tagId(7L)
        .name(specialName)
        .recipes(new ArrayList<>())
        .build();

    // Act
    RecipeTagDto result = recipeTagMapper.toDto(tagWithSpecialName);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(7L);
    assertThat(result.getName()).isEqualTo(specialName);
  }

  @Test
  @DisplayName("Should handle RecipeTag with null tagId")
  void shouldHandleRecipeTagWithNullTagId() {
    // Arrange
    RecipeTag tagWithNullId = RecipeTag.builder()
        .tagId(null)
        .name("no-id-tag")
        .recipes(new ArrayList<>())
        .build();

    // Act
    RecipeTagDto result = recipeTagMapper.toDto(tagWithNullId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isNull();
    assertThat(result.getName()).isEqualTo("no-id-tag");
  }

  @Test
  @DisplayName("Should handle large list of tags")
  void shouldHandleLargeListOfTags() {
    // Arrange
    List<RecipeTag> largeTags = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      largeTags.add(RecipeTag.builder()
          .tagId((long) i)
          .name("tag-" + i)
          .recipes(new ArrayList<>())
          .build());
    }

    // Act
    List<RecipeTagDto> result = recipeTagMapper.toDtoList(largeTags);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(100);
    assertThat(result.get(0).getTagId()).isEqualTo(1L);
    assertThat(result.get(0).getName()).isEqualTo("tag-1");
    assertThat(result.get(99).getTagId()).isEqualTo(100L);
    assertThat(result.get(99).getName()).isEqualTo("tag-100");
  }
}
