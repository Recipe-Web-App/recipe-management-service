package com.recipe_manager.unit_tests.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.recipe_manager.model.dto.recipe.RecipeRevisionDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeRevision;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;
import com.recipe_manager.model.mapper.RecipeRevisionMapper;

@org.junit.jupiter.api.Tag("unit")
class RecipeRevisionMapperTest {

  private final RecipeRevisionMapper recipeRevisionMapper = Mappers.getMapper(RecipeRevisionMapper.class);

  private RecipeRevision recipeRevision;
  private Recipe recipe;
  private UUID userId;
  private Long recipeId;
  private LocalDateTime createdAt;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    recipeId = 123L;
    createdAt = LocalDateTime.now();

    recipe = Recipe.builder()
        .recipeId(recipeId)
        .build();

    recipeRevision = RecipeRevision.builder()
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
  }

  @Test
  @DisplayName("Should map RecipeRevision entity to RecipeRevisionDto")
  void shouldMapRecipeRevisionEntityToDto() {
    // Act
    RecipeRevisionDto result = recipeRevisionMapper.toDto(recipeRevision);

    // Assert
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
    // Act
    RecipeRevisionDto result = recipeRevisionMapper.toDto(null);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeRevision with null recipe")
  void shouldHandleRecipeRevisionWithNullRecipe() {
    // Arrange
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

    // Act
    RecipeRevisionDto result = recipeRevisionMapper.toDto(revisionWithNullRecipe);

    // Assert
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
    // Arrange
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

    // Act
    RecipeRevisionDto result = recipeRevisionMapper.toDto(revisionWithNullComment);

    // Assert
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
    // Arrange
    UUID userId2 = UUID.randomUUID();
    Long recipeId2 = 456L;
    LocalDateTime createdAt2 = LocalDateTime.now().minusDays(1);

    Recipe recipe2 = Recipe.builder()
        .recipeId(recipeId2)
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

    // Act
    List<RecipeRevisionDto> result = recipeRevisionMapper.toDtoList(revisions);

    // Assert
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
    // Act
    List<RecipeRevisionDto> result = recipeRevisionMapper.toDtoList(null);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle empty list")
  void shouldHandleEmptyList() {
    // Act
    List<RecipeRevisionDto> result = recipeRevisionMapper.toDtoList(Arrays.asList());

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should handle list with null elements")
  void shouldHandleListWithNullElements() {
    // Arrange
    List<RecipeRevision> revisionsWithNull = Arrays.asList(recipeRevision, null);

    // Act
    List<RecipeRevisionDto> result = recipeRevisionMapper.toDtoList(revisionsWithNull);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isNotNull();
    assertThat(result.get(0).getRevisionId()).isEqualTo(1L);
    assertThat(result.get(1)).isNull();
  }

  @Test
  @DisplayName("Should handle all revision categories and types")
  void shouldHandleAllRevisionCategoriesAndTypes() {
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

    RecipeRevisionDto result = recipeRevisionMapper.toDto(stepDeleteRevision);

    assertThat(result).isNotNull();
    assertThat(result.getRevisionCategory()).isEqualTo(RevisionCategory.STEP);
    assertThat(result.getRevisionType()).isEqualTo(RevisionType.DELETE);
    assertThat(result.getChangeComment()).isEqualTo("Removed unnecessary step");
  }

  @Test
  @DisplayName("Should handle RecipeRevision with null fields")
  void shouldHandleRecipeRevisionWithNullFields() {
    // Arrange
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

    // Act
    RecipeRevisionDto result = recipeRevisionMapper.toDto(revisionWithNulls);

    // Assert
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
}
