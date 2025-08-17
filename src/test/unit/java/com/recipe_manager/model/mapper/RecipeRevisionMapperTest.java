package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
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
  @Test
  @DisplayName("Should map RecipeRevision entity to RecipeRevisionDto")
  void shouldMapRecipeRevisionEntityToDto() {
    UUID userId = UUID.randomUUID();
    Long recipeId = 123L;
    LocalDateTime createdAt = LocalDateTime.now();

    Recipe recipe = Recipe.builder()
        .recipeId(recipeId)
        .build();

    RecipeRevision recipeRevision = RecipeRevision.builder()
        .revisionId(1L)
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.ADD)
        .previousData("{\"name\":\"old ingredient\"}")
        .newData("{\"name\":\"new ingredient\"}")
        .changeComment("Added new ingredient")
        .createdAt(createdAt)
        .build();

    RecipeRevisionDto result = mapper.toDto(recipeRevision);

    assertThat(result).isNotNull();
    assertThat(result.getRevisionId()).isEqualTo(1L);
    assertThat(result.getRecipeId()).isEqualTo(recipeId);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(result.getRevisionType()).isEqualTo(RevisionType.ADD);
    assertThat(result.getPreviousData()).isEqualTo("{\"name\":\"old ingredient\"}");
    assertThat(result.getNewData()).isEqualTo("{\"name\":\"new ingredient\"}");
    assertThat(result.getChangeComment()).isEqualTo("Added new ingredient");
    assertThat(result.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should handle null RecipeRevision entity")
  void shouldHandleNullRecipeRevisionEntity() {
    RecipeRevisionDto result = mapper.toDto(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeRevision with null recipe")
  void shouldHandleRecipeRevisionWithNullRecipe() {
    UUID userId = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.now();

    RecipeRevision revisionWithNullRecipe = RecipeRevision.builder()
        .revisionId(2L)
        .recipe(null)
        .userId(userId)
        .revisionCategory(RevisionCategory.STEP)
        .revisionType(RevisionType.UPDATE)
        .previousData("{\"step\":\"old step\"}")
        .newData("{\"step\":\"updated step\"}")
        .changeComment("Updated step")
        .createdAt(createdAt)
        .build();

    RecipeRevisionDto result = mapper.toDto(revisionWithNullRecipe);

    assertThat(result).isNotNull();
    assertThat(result.getRevisionId()).isEqualTo(2L);
    assertThat(result.getRecipeId()).isNull();
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getRevisionCategory()).isEqualTo(RevisionCategory.STEP);
    assertThat(result.getRevisionType()).isEqualTo(RevisionType.UPDATE);
    assertThat(result.getPreviousData()).isEqualTo("{\"step\":\"old step\"}");
    assertThat(result.getNewData()).isEqualTo("{\"step\":\"updated step\"}");
    assertThat(result.getChangeComment()).isEqualTo("Updated step");
    assertThat(result.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should handle RecipeRevision with null comment")
  void shouldHandleRecipeRevisionWithNullComment() {
    UUID userId = UUID.randomUUID();
    Long recipeId = 123L;
    LocalDateTime createdAt = LocalDateTime.now();
    Recipe recipe = Recipe.builder()
        .recipeId(recipeId)
        .build();

    RecipeRevision revisionWithNullComment = RecipeRevision.builder()
        .revisionId(3L)
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.DELETE)
        .previousData("{\"name\":\"deleted ingredient\"}")
        .newData("{}")
        .changeComment(null)
        .createdAt(createdAt)
        .build();

    RecipeRevisionDto result = mapper.toDto(revisionWithNullComment);

    assertThat(result).isNotNull();
    assertThat(result.getRevisionId()).isEqualTo(3L);
    assertThat(result.getRecipeId()).isEqualTo(recipeId);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(result.getRevisionType()).isEqualTo(RevisionType.DELETE);
    assertThat(result.getPreviousData()).isEqualTo("{\"name\":\"deleted ingredient\"}");
    assertThat(result.getNewData()).isEqualTo("{}");
    assertThat(result.getChangeComment()).isNull();
    assertThat(result.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should map list of RecipeRevision entities to list of DTOs")
  void shouldMapListOfRecipeRevisionEntitiesToDtoList() {
    UUID userId = UUID.randomUUID();
    Long recipeId = 123L;
    LocalDateTime createdAt = LocalDateTime.now();

    UUID userId2 = UUID.randomUUID();
    Long recipeId2 = 456L;
    LocalDateTime createdAt2 = LocalDateTime.now().minusDays(1);

    Recipe recipe = Recipe.builder()
        .recipeId(recipeId)
        .build();
    Recipe recipe2 = Recipe.builder()
        .recipeId(recipeId2)
        .build();

    RecipeRevision recipeRevision = RecipeRevision.builder()
        .revisionId(1L)
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.ADD)
        .previousData("{\"name\":\"old ingredient\"}")
        .newData("{\"name\":\"new ingredient\"}")
        .changeComment("Added new ingredient")
        .createdAt(createdAt)
        .build();
    RecipeRevision recipeRevision2 = RecipeRevision.builder()
        .revisionId(2L)
        .recipe(recipe2)
        .userId(userId2)
        .revisionCategory(RevisionCategory.STEP)
        .revisionType(RevisionType.UPDATE)
        .previousData("{\"instruction\":\"old step\"}")
        .newData("{\"instruction\":\"updated step\"}")
        .changeComment("Updated cooking step")
        .createdAt(createdAt2)
        .build();

    List<RecipeRevision> revisions = Arrays.asList(recipeRevision, recipeRevision2);

    List<RecipeRevisionDto> result = mapper.toDtoList(revisions);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);

    RecipeRevisionDto dto1 = result.get(0);
    assertThat(dto1.getRevisionId()).isEqualTo(1L);
    assertThat(dto1.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto1.getUserId()).isEqualTo(userId);
    assertThat(dto1.getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(dto1.getRevisionType()).isEqualTo(RevisionType.ADD);
    assertThat(dto1.getCreatedAt()).isEqualTo(createdAt);

    RecipeRevisionDto dto2 = result.get(1);
    assertThat(dto2.getRevisionId()).isEqualTo(2L);
    assertThat(dto2.getRecipeId()).isEqualTo(recipeId2);
    assertThat(dto2.getUserId()).isEqualTo(userId2);
    assertThat(dto2.getRevisionCategory()).isEqualTo(RevisionCategory.STEP);
    assertThat(dto2.getRevisionType()).isEqualTo(RevisionType.UPDATE);
    assertThat(dto2.getCreatedAt()).isEqualTo(createdAt2);
  }

  @Test
  @DisplayName("Should handle null list")
  void shouldHandleNullList() {
    List<RecipeRevisionDto> result = mapper.toDtoList(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle empty list")
  void shouldHandleEmptyList() {
    List<RecipeRevisionDto> result = mapper.toDtoList(Arrays.asList());
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should handle list with null elements")
  void shouldHandleListWithNullElements() {
    UUID userId = UUID.randomUUID();
    Long recipeId = 123L;
    LocalDateTime createdAt = LocalDateTime.now();

    Recipe recipe = Recipe.builder()
        .recipeId(recipeId)
        .build();

    RecipeRevision recipeRevision = RecipeRevision.builder()
        .revisionId(1L)
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.ADD)
        .previousData("{\"name\":\"old ingredient\"}")
        .newData("{\"name\":\"new ingredient\"}")
        .changeComment("Added new ingredient")
        .createdAt(createdAt)
        .build();

    List<RecipeRevision> revisionsWithNull = Arrays.asList(recipeRevision, null);

    List<RecipeRevisionDto> result = mapper.toDtoList(revisionsWithNull);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isNotNull();
    assertThat(result.get(0).getRevisionId()).isEqualTo(1L);
    assertThat(result.get(1)).isNull();
  }

  @Test
  @DisplayName("Should handle all revision categories and types")
  void shouldHandleAllRevisionCategoriesAndTypes() {
    UUID userId = UUID.randomUUID();
    Long recipeId = 123L;
    LocalDateTime createdAt = LocalDateTime.now();
    Recipe recipe = Recipe.builder()
        .recipeId(recipeId)
        .build();

    // Test STEP category with DELETE type
    RecipeRevision stepDeleteRevision = RecipeRevision.builder()
        .revisionId(4L)
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.STEP)
        .revisionType(RevisionType.DELETE)
        .previousData("{\"step\":\"removed step\"}")
        .newData("{}")
        .changeComment("Removed unnecessary step")
        .createdAt(createdAt)
        .build();

    RecipeRevisionDto result = mapper.toDto(stepDeleteRevision);

    assertThat(result).isNotNull();
    assertThat(result.getRevisionCategory()).isEqualTo(RevisionCategory.STEP);
    assertThat(result.getRevisionType()).isEqualTo(RevisionType.DELETE);
    assertThat(result.getChangeComment()).isEqualTo("Removed unnecessary step");
  }

  @Test
  @DisplayName("Should handle RecipeRevision with null fields")
  void shouldHandleRecipeRevisionWithNullFields() {
    Long recipeId = 123L;
    Recipe recipe = Recipe.builder()
        .recipeId(recipeId)
        .build();

    RecipeRevision revisionWithNulls = RecipeRevision.builder()
        .revisionId(5L)
        .recipe(recipe)
        .userId(null)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.ADD)
        .previousData(null)
        .newData(null)
        .changeComment(null)
        .createdAt(null)
        .build();

    RecipeRevisionDto result = mapper.toDto(revisionWithNulls);

    assertThat(result).isNotNull();
    assertThat(result.getRevisionId()).isEqualTo(5L);
    assertThat(result.getRecipeId()).isEqualTo(recipeId);
    assertThat(result.getUserId()).isNull();
    assertThat(result.getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(result.getRevisionType()).isEqualTo(RevisionType.ADD);
    assertThat(result.getPreviousData()).isNull();
    assertThat(result.getNewData()).isNull();
    assertThat(result.getChangeComment()).isNull();
    assertThat(result.getCreatedAt()).isNull();
  }

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
