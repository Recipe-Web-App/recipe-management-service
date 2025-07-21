package com.recipe_manager.model.entity.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeRevision entity.
 */
@Tag("unit")
class RecipeRevisionTest {

  private Recipe recipe;
  private UUID userId;
  private RecipeRevision recipeRevision;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    recipe = Recipe.builder()
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .build();

    recipeRevision = RecipeRevision.builder()
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.ADD)
        .previousData("{}")
        .newData("{\"ingredient\":\"salt\"}")
        .changeComment("Added salt to recipe")
        .build();
  }

  @Test
  @DisplayName("Should create recipe revision with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeRevisionWithBuilder() {
    // Then
    assertThat(recipeRevision.getRecipe()).isEqualTo(recipe);
    assertThat(recipeRevision.getUserId()).isEqualTo(userId);
    assertThat(recipeRevision.getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(recipeRevision.getRevisionType()).isEqualTo(RevisionType.ADD);
    assertThat(recipeRevision.getPreviousData()).isEqualTo("{}");
    assertThat(recipeRevision.getNewData()).isEqualTo("{\"ingredient\":\"salt\"}");
    assertThat(recipeRevision.getChangeComment()).isEqualTo("Added salt to recipe");
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    Long revisionId = 1L;
    String newPreviousData = "{\"old\":\"data\"}";
    String newNewData = "{\"new\":\"data\"}";
    String newChangeComment = "Updated comment";
    LocalDateTime createdAt = LocalDateTime.now();

    // When
    recipeRevision.setRevisionId(revisionId);
    recipeRevision.setPreviousData(newPreviousData);
    recipeRevision.setNewData(newNewData);
    recipeRevision.setChangeComment(newChangeComment);
    recipeRevision.setCreatedAt(createdAt);

    // Then
    assertThat(recipeRevision.getRevisionId()).isEqualTo(revisionId);
    assertThat(recipeRevision.getPreviousData()).isEqualTo(newPreviousData);
    assertThat(recipeRevision.getNewData()).isEqualTo(newNewData);
    assertThat(recipeRevision.getChangeComment()).isEqualTo(newChangeComment);
    assertThat(recipeRevision.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should have correct toString representation")
  @Tag("standard-processing")
  void shouldHaveCorrectToStringRepresentation() {
    // When
    String toString = recipeRevision.toString();

    // Then
    assertThat(toString).contains("RecipeRevision");
    assertThat(toString).contains("INGREDIENT");
    assertThat(toString).contains("ADD");
    assertThat(toString).contains("Added salt to recipe");
  }

  @Test
  @DisplayName("Should handle step revision")
  @Tag("standard-processing")
  void shouldHandleStepRevision() {
    // Given
    RecipeRevision stepRevision = RecipeRevision.builder()
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.STEP)
        .revisionType(RevisionType.UPDATE)
        .previousData("{\"step\":\"old instruction\"}")
        .newData("{\"step\":\"new instruction\"}")
        .changeComment("Updated step instruction")
        .build();

    // Then
    assertThat(stepRevision.getRevisionCategory()).isEqualTo(RevisionCategory.STEP);
    assertThat(stepRevision.getRevisionType()).isEqualTo(RevisionType.UPDATE);
    assertThat(stepRevision.getChangeComment()).isEqualTo("Updated step instruction");
  }

  @Test
  @DisplayName("Should handle delete revision")
  @Tag("standard-processing")
  void shouldHandleDeleteRevision() {
    // Given
    RecipeRevision deleteRevision = RecipeRevision.builder()
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.DELETE)
        .previousData("{\"ingredient\":\"salt\"}")
        .newData("{}")
        .changeComment("Removed salt from recipe")
        .build();

    // Then
    assertThat(deleteRevision.getRevisionType()).isEqualTo(RevisionType.DELETE);
    assertThat(deleteRevision.getChangeComment()).isEqualTo("Removed salt from recipe");
  }

  @Test
  @DisplayName("Should handle revision without comment")
  @Tag("standard-processing")
  void shouldHandleRevisionWithoutComment() {
    // Given
    RecipeRevision noCommentRevision = RecipeRevision.builder()
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.ADD)
        .previousData("{}")
        .newData("{\"ingredient\":\"pepper\"}")
        .build();

    // Then
    assertThat(noCommentRevision.getChangeComment()).isNull();
  }

  @Test
  @DisplayName("Should handle revision with complex JSON data")
  @Tag("standard-processing")
  void shouldHandleRevisionWithComplexJsonData() {
    // Given
    String complexPreviousData = "{\"ingredients\":[{\"name\":\"salt\",\"amount\":1}],\"steps\":[{\"instruction\":\"Mix\"}]}";
    String complexNewData = "{\"ingredients\":[{\"name\":\"salt\",\"amount\":2},{\"name\":\"pepper\",\"amount\":1}],\"steps\":[{\"instruction\":\"Mix well\"}]}";

    RecipeRevision complexRevision = RecipeRevision.builder()
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.UPDATE)
        .previousData(complexPreviousData)
        .newData(complexNewData)
        .changeComment("Updated recipe with additional ingredients")
        .build();

    // Then
    assertThat(complexRevision.getPreviousData()).isEqualTo(complexPreviousData);
    assertThat(complexRevision.getNewData()).isEqualTo(complexNewData);
    assertThat(complexRevision.getPreviousData()).contains("salt");
    assertThat(complexRevision.getNewData()).contains("pepper");
  }

  @Test
  @DisplayName("Builder should defensively copy recipe")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyRecipe() {
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R").build();
    RecipeRevision rev = RecipeRevision.builder().recipe(recipe).userId(userId)
        .revisionCategory(com.recipe_manager.model.enums.RevisionCategory.INGREDIENT)
        .revisionType(com.recipe_manager.model.enums.RevisionType.ADD).previousData("{}").newData("{}").build();
    recipe.setTitle("Changed");
    assertThat(rev.getRecipe().getTitle()).isNotEqualTo("Changed");
  }

  @Test
  @DisplayName("Getters and setters should defensively copy recipe")
  @Tag("standard-processing")
  void gettersAndSettersShouldDefensivelyCopyRecipe() {
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R2").build();
    recipeRevision.setRecipe(recipe);
    recipe.setTitle("Changed2");
    assertThat(recipeRevision.getRecipe().getTitle()).isNotEqualTo("Changed2");
  }

  @Test
  @DisplayName("All-args constructor should defensively copy recipe")
  @Tag("standard-processing")
  void allArgsConstructorShouldDefensivelyCopyRecipe() {
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R3").build();
    RecipeRevision rev = new RecipeRevision(1L, recipe, userId,
        com.recipe_manager.model.enums.RevisionCategory.INGREDIENT, com.recipe_manager.model.enums.RevisionType.ADD,
        "{}", "{}", "comment", java.time.LocalDateTime.now());
    recipe.setTitle("Changed3");
    assertThat(rev.getRecipe().getTitle()).isNotEqualTo("Changed3");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    RecipeRevision rev = new RecipeRevision(null, null, null, null, null, null, null, null, null);
    assertThat(rev.getRevisionId()).isNull();
    assertThat(rev.getRecipe()).isNull();
    assertThat(rev.getUserId()).isNull();
    assertThat(rev.getRevisionCategory()).isNull();
    assertThat(rev.getRevisionType()).isNull();
    assertThat(rev.getPreviousData()).isNull();
    assertThat(rev.getNewData()).isNull();
    assertThat(rev.getChangeComment()).isNull();
    assertThat(rev.getCreatedAt()).isNull();
  }

  @Test
  @DisplayName("Equals and hashCode: self, null, different type, different values, all-null fields")
  @Tag("standard-processing")
  void equalsAndHashCodeEdgeCases() {
    RecipeRevision rev1 = RecipeRevision.builder().userId(userId).build();
    RecipeRevision rev2 = RecipeRevision.builder().userId(userId).build();
    RecipeRevision rev3 = RecipeRevision.builder().userId(UUID.randomUUID()).build();
    assertThat(rev1).isEqualTo(rev1);
    assertThat(rev1).isEqualTo(rev2);
    assertThat(rev1.hashCode()).isEqualTo(rev2.hashCode());
    assertThat(rev1).isNotEqualTo(rev3);
    assertThat(rev1.hashCode()).isNotEqualTo(rev3.hashCode());
    assertThat(rev1).isNotEqualTo(null);
    assertThat(rev1).isNotEqualTo(new Object());
    RecipeRevision revNulls1 = new RecipeRevision(null, null, null, null, null, null, null, null, null);
    RecipeRevision revNulls2 = new RecipeRevision(null, null, null, null, null, null, null, null, null);
    assertThat(revNulls1).isEqualTo(revNulls2);
    assertThat(revNulls1.hashCode()).isEqualTo(revNulls2.hashCode());
  }
}
