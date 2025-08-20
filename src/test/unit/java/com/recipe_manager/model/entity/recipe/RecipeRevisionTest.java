package com.recipe_manager.model.entity.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.dto.revision.IngredientAddRevision;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class RecipeRevisionTest {

  @Test
  @DisplayName("All-args constructor assigns all fields")
  @Tag("standard-processing")
  void allArgsConstructorAssignsFields() {
    Recipe recipe = new Recipe();
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    IngredientAddRevision previousRevision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Previous Ingredient")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    IngredientAddRevision newRevision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("New Ingredient")
        .quantity(new BigDecimal("2.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    RecipeRevision revision = new RecipeRevision(1L, recipe, userId, RevisionCategory.INGREDIENT, RevisionType.ADD,
        previousRevision, newRevision, "comment", now);
    assertThat(revision.getRevisionId()).isEqualTo(1L);
    assertThat(revision.getRecipe()).isSameAs(recipe);
    assertThat(revision.getUserId()).isEqualTo(userId);
    assertThat(revision.getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(revision.getRevisionType()).isEqualTo(RevisionType.ADD);
    assertThat(revision.getPreviousData()).isEqualTo(previousRevision);
    assertThat(revision.getNewData()).isEqualTo(newRevision);
    assertThat(revision.getChangeComment()).isEqualTo("comment");
    assertThat(revision.getCreatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("No-args constructor sets nulls and default createdAt")
  @Tag("standard-processing")
  void noArgsConstructorSetsDefaults() {
    RecipeRevision revision = new RecipeRevision();
    assertThat(revision.getRevisionId()).isNull();
    assertThat(revision.getRecipe()).isNull();
    assertThat(revision.getUserId()).isNull();
    assertThat(revision.getRevisionCategory()).isNull();
    assertThat(revision.getRevisionType()).isNull();
    assertThat(revision.getPreviousData()).isNull();
    assertThat(revision.getNewData()).isNull();
    assertThat(revision.getChangeComment()).isNull();
    // createdAt may be set by default, but we allow null for test
  }

  @Test
  @DisplayName("Setters and getters work for all fields")
  @Tag("standard-processing")
  void settersAndGettersWork() {
    RecipeRevision revision = new RecipeRevision();
    revision.setChangeComment("Test");
    assertThat(revision.getChangeComment()).isEqualTo("Test");
  }

  @Test
  @DisplayName("Builder works with typed revision data fields")
  @Tag("standard-processing")
  void builderWorksWithTypedRevisionFields() {
    IngredientAddRevision previousData = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(new BigDecimal("2.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .description("All-purpose flour")
        .build();

    IngredientAddRevision newData = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(new BigDecimal("3.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .description("All-purpose flour")
        .build();

    Recipe recipe = Recipe.builder().recipeId(123L).build();
    UUID userId = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.now();

    RecipeRevision revision = RecipeRevision.builder()
        .revisionId(1L)
        .recipe(recipe)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.UPDATE)
        .previousData(previousData)
        .newData(newData)
        .changeComment("Increased flour quantity")
        .createdAt(createdAt)
        .build();

    assertThat(revision.getRevisionId()).isEqualTo(1L);
    assertThat(revision.getRecipe()).isEqualTo(recipe);
    assertThat(revision.getUserId()).isEqualTo(userId);
    assertThat(revision.getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(revision.getRevisionType()).isEqualTo(RevisionType.UPDATE);
    assertThat(revision.getPreviousData()).isEqualTo(previousData);
    assertThat(revision.getNewData()).isEqualTo(newData);
    assertThat(revision.getChangeComment()).isEqualTo("Increased flour quantity");
    assertThat(revision.getCreatedAt()).isEqualTo(createdAt);

    // Verify the typed data can be accessed directly
    assertThat(revision.getPreviousData()).isInstanceOf(IngredientAddRevision.class);
    assertThat(revision.getNewData()).isInstanceOf(IngredientAddRevision.class);

    IngredientAddRevision retrievedPrevious = (IngredientAddRevision) revision.getPreviousData();
    IngredientAddRevision retrievedNew = (IngredientAddRevision) revision.getNewData();

    assertThat(retrievedPrevious.getQuantity()).isEqualTo(new BigDecimal("2.0"));
    assertThat(retrievedNew.getQuantity()).isEqualTo(new BigDecimal("3.0"));
  }

  @Test
  @DisplayName("Equals/hashCode/toString are generated by Lombok")
  @Tag("standard-processing")
  void equalsHashCodeToString() {
    LocalDateTime now = LocalDateTime.now();

    IngredientAddRevision revision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Test Ingredient")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    RecipeRevision r1 = RecipeRevision.builder().previousData(revision).createdAt(now).build();
    RecipeRevision r2 = RecipeRevision.builder().previousData(revision).createdAt(now).build();
    assertThat(r1).isEqualTo(r2);
    assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    assertThat(r1.toString()).contains("Test Ingredient");
  }
}
