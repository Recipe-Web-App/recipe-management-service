package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.recipe.RecipeRevisionDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeRevision;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/**
 * Unit tests for RecipeRevisionMapper.
 */
@Tag("unit")
class RecipeRevisionMapperTest {

  private final RecipeRevisionMapper mapper = Mappers.getMapper(RecipeRevisionMapper.class);

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map RecipeRevision entity to RecipeRevisionDto")
  void shouldMapEntityToDto() {
    UUID userId = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.now();

    Recipe recipe = Recipe.builder()
        .recipeId(500L)
        .build();

    RecipeRevision entity = RecipeRevision.builder()
        .revisionId(10L)
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.UPDATE)
        .previousData("{\"name\":\"Sugar\",\"quantity\":\"1 cup\"}")
        .newData("{\"name\":\"Sugar\",\"quantity\":\"2 cups\"}")
        .changeComment("Increased sugar quantity")
        .createdAt(createdAt)
        .build();

    RecipeRevisionDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getRevisionId()).isEqualTo(10L);
    assertThat(result.getRecipeId()).isEqualTo(500L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(result.getRevisionType()).isEqualTo(RevisionType.UPDATE);
    assertThat(result.getPreviousData()).isEqualTo("{\"name\":\"Sugar\",\"quantity\":\"1 cup\"}");
    assertThat(result.getNewData()).isEqualTo("{\"name\":\"Sugar\",\"quantity\":\"2 cups\"}");
    assertThat(result.getChangeComment()).isEqualTo("Increased sugar quantity");
    assertThat(result.getCreatedAt()).isEqualTo(createdAt);
    // Ignored fields should be null or default values
    assertThat(result.getUpdatedAt()).isNull();
    assertThat(result.getMedia()).isNotNull().isEmpty(); // MapStruct returns empty list for @Default fields
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map a list of RecipeRevision entities to RecipeRevisionDto list")
  void shouldMapEntityListToDto() {
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    Recipe recipe1 = Recipe.builder()
        .recipeId(600L)
        .build();

    Recipe recipe2 = Recipe.builder()
        .recipeId(700L)
        .build();

    RecipeRevision entity1 = RecipeRevision.builder()
        .revisionId(20L)
        .recipe(recipe1)
        .userId(userId1)
        .revisionCategory(RevisionCategory.STEP)
        .revisionType(RevisionType.ADD)
        .previousData("{}")
        .newData("{\"instruction\":\"Mix well\"}")
        .changeComment("Added mixing step")
        .createdAt(now)
        .build();

    RecipeRevision entity2 = RecipeRevision.builder()
        .revisionId(30L)
        .recipe(recipe2)
        .userId(userId2)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.DELETE)
        .previousData("{\"title\":\"Old Recipe\"}")
        .newData("{}")
        .changeComment("Deleted old recipe")
        .createdAt(now.plusMinutes(30))
        .build();

    List<RecipeRevisionDto> results = mapper.toDtoList(List.of(entity1, entity2));

    assertThat(results).hasSize(2);
    assertThat(results.get(0).getRevisionId()).isEqualTo(20L);
    assertThat(results.get(0).getRecipeId()).isEqualTo(600L);
    assertThat(results.get(0).getRevisionCategory()).isEqualTo(RevisionCategory.STEP);
    assertThat(results.get(0).getRevisionType()).isEqualTo(RevisionType.ADD);
    assertThat(results.get(0).getChangeComment()).isEqualTo("Added mixing step");
    assertThat(results.get(1).getRevisionId()).isEqualTo(30L);
    assertThat(results.get(1).getRecipeId()).isEqualTo(700L);
    assertThat(results.get(1).getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(results.get(1).getRevisionType()).isEqualTo(RevisionType.DELETE);
    assertThat(results.get(1).getChangeComment()).isEqualTo("Deleted old recipe");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null recipe in RecipeRevision entity")
  void shouldHandleNullRecipe() {
    UUID userId = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.now();

    RecipeRevision entity = RecipeRevision.builder()
        .revisionId(40L)
        .recipe(null)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.UPDATE)
        .previousData("{}")
        .newData("{}")
        .changeComment("Test revision")
        .createdAt(createdAt)
        .build();

    RecipeRevisionDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getRevisionId()).isEqualTo(40L);
    assertThat(result.getRecipeId()).isNull();
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(result.getRevisionType()).isEqualTo(RevisionType.UPDATE);
    assertThat(result.getChangeComment()).isEqualTo("Test revision");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null change comment")
  void shouldHandleNullChangeComment() {
    UUID userId = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.now();

    Recipe recipe = Recipe.builder()
        .recipeId(800L)
        .build();

    RecipeRevision entity = RecipeRevision.builder()
        .revisionId(50L)
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.STEP)
        .revisionType(RevisionType.UPDATE)
        .previousData("{\"title\":\"Old Title\"}")
        .newData("{\"title\":\"New Title\"}")
        .changeComment(null)
        .createdAt(createdAt)
        .build();

    RecipeRevisionDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getRevisionId()).isEqualTo(50L);
    assertThat(result.getRecipeId()).isEqualTo(800L);
    assertThat(result.getChangeComment()).isNull();
    assertThat(result.getRevisionCategory()).isEqualTo(RevisionCategory.STEP);
    assertThat(result.getRevisionType()).isEqualTo(RevisionType.UPDATE);
  }
}
