package com.recipe_manager.model.entity.ingredient;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Ingredient entity.
 */
@Tag("unit")
class IngredientTest {

  private Ingredient ingredient;

  @BeforeEach
  void setUp() {
    ingredient = Ingredient.builder()
        .name("Test Ingredient")
        .description("A test ingredient for testing")
        .category("Vegetables")
        .build();
  }

  @Test
  @DisplayName("Should create ingredient with builder")
  @Tag("standard-processing")
  void shouldCreateIngredientWithBuilder() {
    // Then
    assertThat(ingredient.getName()).isEqualTo("Test Ingredient");
    assertThat(ingredient.getDescription()).isEqualTo("A test ingredient for testing");
    assertThat(ingredient.getCategory()).isEqualTo("Vegetables");
    assertThat(ingredient.getRecipeIngredients()).isEmpty();
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    Long ingredientId = 1L;
    String newName = "Updated Ingredient";
    String newDescription = "Updated description";
    String newCategory = "Fruits";
    LocalDateTime createdAt = LocalDateTime.now();

    // When
    ingredient.setIngredientId(ingredientId);
    ingredient.setName(newName);
    ingredient.setDescription(newDescription);
    ingredient.setCategory(newCategory);
    ingredient.setCreatedAt(createdAt);

    // Then
    assertThat(ingredient.getIngredientId()).isEqualTo(ingredientId);
    assertThat(ingredient.getName()).isEqualTo(newName);
    assertThat(ingredient.getDescription()).isEqualTo(newDescription);
    assertThat(ingredient.getCategory()).isEqualTo(newCategory);
    assertThat(ingredient.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should have correct toString representation")
  @Tag("standard-processing")
  void shouldHaveCorrectToStringRepresentation() {
    // When
    String toString = ingredient.toString();

    // Then
    assertThat(toString).contains("Ingredient");
    assertThat(toString).contains("Test Ingredient");
    assertThat(toString).contains("A test ingredient for testing");
    assertThat(toString).contains("Vegetables");
  }

  @Test
  @DisplayName("Should handle ingredient with null description")
  @Tag("standard-processing")
  void shouldHandleIngredientWithNullDescription() {
    // Given
    Ingredient nullDescriptionIngredient = Ingredient.builder()
        .name("Simple Ingredient")
        .build();

    // Then
    assertThat(nullDescriptionIngredient.getDescription()).isNull();
  }

  @Test
  @DisplayName("Should handle ingredient with null category")
  @Tag("standard-processing")
  void shouldHandleIngredientWithNullCategory() {
    // Given
    Ingredient nullCategoryIngredient = Ingredient.builder()
        .name("Uncategorized Ingredient")
        .build();

    // Then
    assertThat(nullCategoryIngredient.getCategory()).isNull();
  }

  @Test
  @DisplayName("Should handle different ingredient categories")
  @Tag("standard-processing")
  void shouldHandleDifferentIngredientCategories() {
    // Given
    Ingredient vegetableIngredient = Ingredient.builder()
        .name("Carrot")
        .category("Vegetables")
        .build();

    Ingredient fruitIngredient = Ingredient.builder()
        .name("Apple")
        .category("Fruits")
        .build();

    Ingredient meatIngredient = Ingredient.builder()
        .name("Chicken")
        .category("Meat")
        .build();

    Ingredient dairyIngredient = Ingredient.builder()
        .name("Milk")
        .category("Dairy")
        .build();

    // Then
    assertThat(vegetableIngredient.getCategory()).isEqualTo("Vegetables");
    assertThat(fruitIngredient.getCategory()).isEqualTo("Fruits");
    assertThat(meatIngredient.getCategory()).isEqualTo("Meat");
    assertThat(dairyIngredient.getCategory()).isEqualTo("Dairy");
  }

  @Test
  @DisplayName("Should handle ingredient with long description")
  @Tag("standard-processing")
  void shouldHandleIngredientWithLongDescription() {
    // Given
    String longDescription = "This is a very long description for an ingredient that contains " +
        "multiple sentences and detailed information about the ingredient's properties, " +
        "nutritional value, and usage recommendations. It might also contain special " +
        "characters like numbers (1, 2, 3) and symbols (@#$%) as well.";

    Ingredient longDescriptionIngredient = Ingredient.builder()
        .name("Complex Ingredient")
        .description(longDescription)
        .category("Complex")
        .build();

    // Then
    assertThat(longDescriptionIngredient.getDescription()).isEqualTo(longDescription);
    assertThat(longDescriptionIngredient.getDescription().length()).isGreaterThan(100);
  }

  @Test
  @DisplayName("Should handle ingredient with special characters in name")
  @Tag("standard-processing")
  void shouldHandleIngredientWithSpecialCharactersInName() {
    // Given
    Ingredient specialIngredient = Ingredient.builder()
        .name("Ingredient with @#$% symbols & numbers 123")
        .category("Special")
        .build();

    // Then
    assertThat(specialIngredient.getName()).isEqualTo("Ingredient with @#$% symbols & numbers 123");
  }

  @Test
  @DisplayName("Should handle ingredient with maximum length name")
  @Tag("standard-processing")
  void shouldHandleIngredientWithMaximumLengthName() {
    // Given
    String maxLengthName = "A".repeat(100); // Maximum length per schema
    Ingredient maxNameIngredient = Ingredient.builder()
        .name(maxLengthName)
        .build();

    // Then
    assertThat(maxNameIngredient.getName()).isEqualTo(maxLengthName);
    assertThat(maxNameIngredient.getName().length()).isEqualTo(100);
  }

  @Test
  @DisplayName("Builder should defensively copy recipeIngredients list")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyRecipeIngredientsList() {
    java.util.List<com.recipe_manager.model.entity.recipe.RecipeIngredient> list = new java.util.ArrayList<>();
    list.add(com.recipe_manager.model.entity.recipe.RecipeIngredient.builder().isOptional(false).build());
    Ingredient ing = Ingredient.builder().recipeIngredients(list).build();
    list.add(com.recipe_manager.model.entity.recipe.RecipeIngredient.builder().isOptional(true).build());
    assertThat(ing.getRecipeIngredients()).hasSize(1);
  }

  @Test
  @DisplayName("Getters and setters should defensively copy recipeIngredients list")
  @Tag("standard-processing")
  void gettersAndSettersShouldDefensivelyCopyRecipeIngredientsList() {
    java.util.List<com.recipe_manager.model.entity.recipe.RecipeIngredient> list = new java.util.ArrayList<>();
    list.add(com.recipe_manager.model.entity.recipe.RecipeIngredient.builder().isOptional(false).build());
    ingredient.setRecipeIngredients(list);
    list.add(com.recipe_manager.model.entity.recipe.RecipeIngredient.builder().isOptional(true).build());
    assertThat(ingredient.getRecipeIngredients()).hasSize(1);
  }

  @Test
  @DisplayName("All-args constructor should defensively copy recipeIngredients list")
  @Tag("standard-processing")
  void allArgsConstructorShouldDefensivelyCopyRecipeIngredientsList() {
    java.util.List<com.recipe_manager.model.entity.recipe.RecipeIngredient> list = new java.util.ArrayList<>();
    list.add(com.recipe_manager.model.entity.recipe.RecipeIngredient.builder().isOptional(false).build());
    Ingredient ing = new Ingredient(1L, "N", "D", "C", java.time.LocalDateTime.now(), list);
    list.add(com.recipe_manager.model.entity.recipe.RecipeIngredient.builder().isOptional(true).build());
    assertThat(ing.getRecipeIngredients()).hasSize(1);
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    Ingredient ing = new Ingredient(null, null, null, null, null, null);
    assertThat(ing.getIngredientId()).isNull();
    assertThat(ing.getName()).isNull();
    assertThat(ing.getDescription()).isNull();
    assertThat(ing.getCategory()).isNull();
    assertThat(ing.getCreatedAt()).isNull();
    assertThat(ing.getRecipeIngredients()).isEmpty();
  }

  @Test
  @DisplayName("Equals and hashCode: self, null, different type, different values, all-null fields")
  @Tag("standard-processing")
  void equalsAndHashCodeEdgeCases() {
    Ingredient ing1 = Ingredient.builder().name("A").build();
    Ingredient ing2 = Ingredient.builder().name("A").build();
    Ingredient ing3 = Ingredient.builder().name("B").build();
    assertThat(ing1).isEqualTo(ing1);
    assertThat(ing1).isEqualTo(ing2);
    assertThat(ing1.hashCode()).isEqualTo(ing2.hashCode());
    assertThat(ing1).isNotEqualTo(ing3);
    assertThat(ing1.hashCode()).isNotEqualTo(ing3.hashCode());
    assertThat(ing1).isNotEqualTo(null);
    assertThat(ing1).isNotEqualTo(new Object());
    Ingredient ingNulls1 = new Ingredient(null, null, null, null, null, null);
    Ingredient ingNulls2 = new Ingredient(null, null, null, null, null, null);
    assertThat(ingNulls1).isEqualTo(ingNulls2);
    assertThat(ingNulls1.hashCode()).isEqualTo(ingNulls2.hashCode());
  }
}
