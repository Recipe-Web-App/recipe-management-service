package com.recipe_manager.model.entity.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeIngredient entity.
 */
@Tag("unit")
class RecipeIngredientTest {

  private Recipe recipe;
  private Ingredient ingredient;
  private RecipeIngredient recipeIngredient;

  @BeforeEach
  void setUp() {
    recipe = Recipe.builder()
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .build();

    ingredient = Ingredient.builder()
        .name("Test Ingredient")
        .description("A test ingredient")
        .category("Vegetables")
        .build();

    recipeIngredient = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(ingredient)
        .quantity(BigDecimal.valueOf(2.5))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();
  }

  @Test
  @DisplayName("Should create recipe ingredient with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeIngredientWithBuilder() {
    // Then
    assertThat(recipeIngredient.getRecipe()).isEqualTo(recipe);
    assertThat(recipeIngredient.getIngredient()).isEqualTo(ingredient);
    assertThat(recipeIngredient.getQuantity()).isEqualTo(BigDecimal.valueOf(2.5));
    assertThat(recipeIngredient.getUnit()).isEqualTo(IngredientUnit.CUP);
    assertThat(recipeIngredient.getIsOptional()).isFalse();
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    RecipeIngredientId id = RecipeIngredientId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .build();
    BigDecimal newQuantity = BigDecimal.valueOf(3.0);
    IngredientUnit newUnit = IngredientUnit.TBSP;

    // When
    recipeIngredient.setId(id);
    recipeIngredient.setQuantity(newQuantity);
    recipeIngredient.setUnit(newUnit);
    recipeIngredient.setIsOptional(true);

    // Then
    assertThat(recipeIngredient.getId()).isEqualTo(id);
    assertThat(recipeIngredient.getQuantity()).isEqualTo(newQuantity);
    assertThat(recipeIngredient.getUnit()).isEqualTo(newUnit);
    assertThat(recipeIngredient.getIsOptional()).isTrue();
  }

  @Test
  @DisplayName("Should have correct toString representation")
  @Tag("standard-processing")
  void shouldHaveCorrectToStringRepresentation() {
    // When
    String toString = recipeIngredient.toString();

    // Then
    assertThat(toString).contains("RecipeIngredient");
    assertThat(toString).contains("2.5");
    assertThat(toString).contains("CUP");
    assertThat(toString).contains("false");
  }

  @Test
  @DisplayName("Should handle null values")
  @Tag("standard-processing")
  void shouldHandleNullValues() {
    // Given
    RecipeIngredient nullIngredient = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(ingredient)
        .build();

    // When & Then
    assertThat(nullIngredient.getQuantity()).isNull();
    assertThat(nullIngredient.getUnit()).isNull();
    assertThat(nullIngredient.getIsOptional()).isFalse(); // Default value
  }

  @Test
  @DisplayName("Should handle optional ingredient")
  @Tag("standard-processing")
  void shouldHandleOptionalIngredient() {
    // Given
    RecipeIngredient optionalIngredient = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(ingredient)
        .isOptional(true)
        .build();

    // Then
    assertThat(optionalIngredient.getIsOptional()).isTrue();
  }

  @Test
  @DisplayName("Should handle different units")
  @Tag("standard-processing")
  void shouldHandleDifferentUnits() {
    // Given
    RecipeIngredient gramIngredient = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(ingredient)
        .quantity(BigDecimal.valueOf(100))
        .unit(IngredientUnit.G)
        .build();

    RecipeIngredient pieceIngredient = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(ingredient)
        .quantity(BigDecimal.valueOf(2))
        .unit(IngredientUnit.PIECE)
        .build();

    // Then
    assertThat(gramIngredient.getUnit()).isEqualTo(IngredientUnit.G);
    assertThat(pieceIngredient.getUnit()).isEqualTo(IngredientUnit.PIECE);
  }

  @Test
  @DisplayName("Builder should defensively copy id, recipe, and ingredient")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyIdRecipeIngredient() {
    RecipeIngredientId id = new RecipeIngredientId(1L, 2L);
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R").build();
    Ingredient ingredient = Ingredient.builder().name("I").build();
    RecipeIngredient ri = RecipeIngredient.builder().id(id).recipe(recipe).ingredient(ingredient).build();
    id.setRecipeId(999L);
    recipe.setTitle("Changed");
    ingredient.setName("Changed");
    assertThat(ri.getId().getRecipeId()).isNotEqualTo(999L);
    assertThat(ri.getRecipe().getTitle()).isNotEqualTo("Changed");
    assertThat(ri.getIngredient().getName()).isNotEqualTo("Changed");
  }

  @Test
  @DisplayName("Getters and setters should defensively copy id, recipe, and ingredient")
  @Tag("standard-processing")
  void gettersAndSettersShouldDefensivelyCopyIdRecipeIngredient() {
    RecipeIngredientId id = new RecipeIngredientId(2L, 3L);
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R2").build();
    Ingredient ingredient = Ingredient.builder().name("I2").build();
    recipeIngredient.setId(id);
    recipeIngredient.setRecipe(recipe);
    recipeIngredient.setIngredient(ingredient);
    id.setRecipeId(888L);
    recipe.setTitle("Changed2");
    ingredient.setName("Changed2");
    assertThat(recipeIngredient.getId().getRecipeId()).isNotEqualTo(888L);
    assertThat(recipeIngredient.getRecipe().getTitle()).isNotEqualTo("Changed2");
    assertThat(recipeIngredient.getIngredient().getName()).isNotEqualTo("Changed2");
  }

  @Test
  @DisplayName("All-args constructor should defensively copy id, recipe, and ingredient")
  @Tag("standard-processing")
  void allArgsConstructorShouldDefensivelyCopyIdRecipeIngredient() {
    RecipeIngredientId id = new RecipeIngredientId(3L, 4L);
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R3").build();
    Ingredient ingredient = Ingredient.builder().name("I3").build();
    RecipeIngredient ri = new RecipeIngredient(id, recipe, ingredient, null, null, false);
    id.setRecipeId(777L);
    recipe.setTitle("Changed3");
    ingredient.setName("Changed3");
    assertThat(ri.getId().getRecipeId()).isNotEqualTo(777L);
    assertThat(ri.getRecipe().getTitle()).isNotEqualTo("Changed3");
    assertThat(ri.getIngredient().getName()).isNotEqualTo("Changed3");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    RecipeIngredient ri = new RecipeIngredient(null, null, null, null, null, null);
    assertThat(ri.getId()).isNull();
    assertThat(ri.getRecipe()).isNull();
    assertThat(ri.getIngredient()).isNull();
    assertThat(ri.getQuantity()).isNull();
    assertThat(ri.getUnit()).isNull();
    assertThat(ri.getIsOptional()).isNull();
  }

  @Test
  @DisplayName("Equals and hashCode: self, null, different type, different values, all-null fields")
  @Tag("standard-processing")
  void equalsAndHashCodeEdgeCases() {
    RecipeIngredient ri1 = RecipeIngredient.builder().isOptional(false).build();
    RecipeIngredient ri2 = RecipeIngredient.builder().isOptional(false).build();
    RecipeIngredient ri3 = RecipeIngredient.builder().isOptional(true).build();
    assertThat(ri1).isEqualTo(ri1);
    assertThat(ri1).isEqualTo(ri2);
    assertThat(ri1.hashCode()).isEqualTo(ri2.hashCode());
    assertThat(ri1).isNotEqualTo(ri3);
    assertThat(ri1.hashCode()).isNotEqualTo(ri3.hashCode());
    assertThat(ri1).isNotEqualTo(null);
    assertThat(ri1).isNotEqualTo(new Object());
    RecipeIngredient riNulls1 = new RecipeIngredient(null, null, null, null, null, null);
    RecipeIngredient riNulls2 = new RecipeIngredient(null, null, null, null, null, null);
    assertThat(riNulls1).isEqualTo(riNulls2);
    assertThat(riNulls1.hashCode()).isEqualTo(riNulls2.hashCode());
  }
}
