package com.recipe_manager.model.entity.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeTag entity.
 */
@Tag("unit")
class RecipeTagTest {

  private RecipeTag recipeTag;

  @BeforeEach
  void setUp() {
    recipeTag = RecipeTag.builder()
        .name("Test Tag")
        .build();
  }

  @Test
  @DisplayName("Should create recipe tag with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeTagWithBuilder() {
    // Then
    assertThat(recipeTag.getName()).isEqualTo("Test Tag");
    assertThat(recipeTag.getRecipes()).isEmpty();
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    Long tagId = 1L;
    String newName = "Updated Tag";
    LocalDateTime createdAt = LocalDateTime.now();

    // When
    recipeTag.setTagId(tagId);
    recipeTag.setName(newName);
    recipeTag.setCreatedAt(createdAt);

    // Then
    assertThat(recipeTag.getTagId()).isEqualTo(tagId);
    assertThat(recipeTag.getName()).isEqualTo(newName);
    assertThat(recipeTag.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should have correct toString representation")
  @Tag("standard-processing")
  void shouldHaveCorrectToStringRepresentation() {
    // When
    String toString = recipeTag.toString();

    // Then
    assertThat(toString).contains("RecipeTag");
    assertThat(toString).contains("Test Tag");
  }

  @Test
  @DisplayName("Should handle tag with recipes")
  @Tag("standard-processing")
  void shouldHandleTagWithRecipes() {
    // Given
    Recipe recipe1 = Recipe.builder()
        .title("Recipe 1")
        .build();
    Recipe recipe2 = Recipe.builder()
        .title("Recipe 2")
        .build();

    // When
    recipeTag.setRecipes(java.util.Arrays.asList(recipe1, recipe2));

    // Then
    assertThat(recipeTag.getRecipes()).hasSize(2);
    assertThat(recipeTag.getRecipes()).contains(recipe1, recipe2);
  }

  @Test
  @DisplayName("Should handle different tag names")
  @Tag("standard-processing")
  void shouldHandleDifferentTagNames() {
    // Given
    RecipeTag italianTag = RecipeTag.builder()
        .name("Italian")
        .build();

    RecipeTag vegetarianTag = RecipeTag.builder()
        .name("Vegetarian")
        .build();

    RecipeTag quickTag = RecipeTag.builder()
        .name("Quick & Easy")
        .build();

    // Then
    assertThat(italianTag.getName()).isEqualTo("Italian");
    assertThat(vegetarianTag.getName()).isEqualTo("Vegetarian");
    assertThat(quickTag.getName()).isEqualTo("Quick & Easy");
  }

  @Test
  @DisplayName("Should handle tag with special characters")
  @Tag("standard-processing")
  void shouldHandleTagWithSpecialCharacters() {
    // Given
    RecipeTag specialTag = RecipeTag.builder()
        .name("Tag with @#$% symbols & numbers 123")
        .build();

    // Then
    assertThat(specialTag.getName()).isEqualTo("Tag with @#$% symbols & numbers 123");
  }

  @Test
  @DisplayName("Should handle empty tag name")
  @Tag("standard-processing")
  void shouldHandleEmptyTagName() {
    // Given
    RecipeTag emptyTag = RecipeTag.builder()
        .name("")
        .build();

    // Then
    assertThat(emptyTag.getName()).isEmpty();
  }

  @Test
  @DisplayName("Should handle tag with maximum length name")
  @Tag("standard-processing")
  void shouldHandleTagWithMaximumLengthName() {
    // Given
    String maxLengthName = "A".repeat(50); // Maximum length per schema
    RecipeTag maxTag = RecipeTag.builder()
        .name(maxLengthName)
        .build();

    // Then
    assertThat(maxTag.getName()).isEqualTo(maxLengthName);
    assertThat(maxTag.getName().length()).isEqualTo(50);
  }

  @Test
  @DisplayName("Should create RecipeTag with all-args constructor and defensively copy recipes")
  @Tag("standard-processing")
  void shouldCreateWithAllArgsConstructorAndDefensiveCopy() {
    java.util.List<Recipe> recipes = new java.util.ArrayList<>();
    recipes.add(Recipe.builder().title("R1").build());
    RecipeTag tag = new RecipeTag(1L, "Name", java.time.LocalDateTime.now(), recipes);
    recipes.add(Recipe.builder().title("R2").build());
    assertThat(tag.getRecipes()).hasSize(1);
  }

  @Test
  @DisplayName("All-args constructor should handle null recipes as empty")
  @Tag("error-processing")
  void allArgsConstructorShouldHandleNullRecipesAsEmpty() {
    RecipeTag tag = new RecipeTag(1L, "Name", java.time.LocalDateTime.now(), null);
    assertThat(tag.getRecipes()).isEmpty();
  }

  @Test
  @DisplayName("Builder should defensively copy recipes list")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyRecipesList() {
    java.util.List<Recipe> recipes = new java.util.ArrayList<>();
    recipes.add(Recipe.builder().title("R1").build());
    RecipeTag tag = RecipeTag.builder().recipes(recipes).build();
    recipes.add(Recipe.builder().title("R2").build());
    assertThat(tag.getRecipes()).hasSize(1);
  }

  @Test
  @DisplayName("Getters and setters should defensively copy recipes list")
  @Tag("standard-processing")
  void gettersAndSettersShouldDefensivelyCopyRecipesList() {
    java.util.List<Recipe> recipes = new java.util.ArrayList<>();
    recipes.add(Recipe.builder().title("R1").build());
    recipeTag.setRecipes(recipes);
    recipes.add(Recipe.builder().title("R2").build());
    assertThat(recipeTag.getRecipes()).hasSize(1);
  }

  @Test
  @DisplayName("All-args constructor should defensively copy recipes list")
  @Tag("standard-processing")
  void allArgsConstructorShouldDefensivelyCopyRecipesList() {
    java.util.List<Recipe> recipes = new java.util.ArrayList<>();
    recipes.add(Recipe.builder().title("R1").build());
    RecipeTag tag = new RecipeTag(1L, "Name", java.time.LocalDateTime.now(), recipes);
    recipes.add(Recipe.builder().title("R2").build());
    assertThat(tag.getRecipes()).hasSize(1);
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    RecipeTag tag = new RecipeTag(null, null, null, null);
    assertThat(tag.getTagId()).isNull();
    assertThat(tag.getName()).isNull();
    assertThat(tag.getCreatedAt()).isNull();
    assertThat(tag.getRecipes()).isEmpty();
  }

  @Test
  @DisplayName("Equals and hashCode: self, null, different type, different values, all-null fields")
  @Tag("standard-processing")
  void equalsAndHashCodeEdgeCases() {
    RecipeTag tag1 = RecipeTag.builder().name("A").recipes(new java.util.ArrayList<>()).build();
    RecipeTag tag2 = RecipeTag.builder().name("A").recipes(new java.util.ArrayList<>()).build();
    RecipeTag tag3 = RecipeTag.builder().name("Different").build();
    assertThat(tag1).isEqualTo(tag1);
    assertThat(tag1).isEqualTo(tag2);
    assertThat(tag1.hashCode()).isEqualTo(tag2.hashCode());
    assertThat(tag1).isNotEqualTo(tag3);
    assertThat(tag1.hashCode()).isNotEqualTo(tag3.hashCode());
    assertThat(tag1).isNotEqualTo(null);
    assertThat(tag1).isNotEqualTo(new Object());
    RecipeTag tagNulls1 = new RecipeTag(null, null, null, null);
    RecipeTag tagNulls2 = new RecipeTag(null, null, null, null);
    assertThat(tagNulls1).isEqualTo(tagNulls2);
    assertThat(tagNulls1.hashCode()).isEqualTo(tagNulls2.hashCode());
  }
}
