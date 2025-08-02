package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.recipe_manager.model.enums.DifficultyLevel;
import com.recipe_manager.model.enums.IngredientMatchMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class SearchRecipesRequestTest {

  @Test
  @DisplayName("Builder sets all fields correctly")
  @Tag("standard-processing")
  void builderSetsAllFields() {
    // Given
    List<String> ingredients = Arrays.asList("chicken", "pasta", "garlic");
    List<String> tags = Arrays.asList("italian", "quick");

    // When
    SearchRecipesRequest request = SearchRecipesRequest.builder()
        .recipeNameQuery("Chicken Pasta")
        .ingredients(ingredients)
        .ingredientMatchMode(IngredientMatchMode.OR)
        .difficulty(DifficultyLevel.MEDIUM)
        .maxCookingTime(30)
        .maxPreparationTime(45)
        .minServings(BigDecimal.valueOf(2))
        .maxServings(BigDecimal.valueOf(6))
        .tags(tags)
        .build();

    // Then
    assertThat(request.getRecipeNameQuery()).isEqualTo("Chicken Pasta");
    assertThat(request.getIngredients()).isSameAs(ingredients);
    assertThat(request.getIngredientMatchMode()).isEqualTo(IngredientMatchMode.OR);
    assertThat(request.getDifficulty()).isEqualTo(DifficultyLevel.MEDIUM);
    assertThat(request.getMaxCookingTime()).isEqualTo(30);
    assertThat(request.getMaxPreparationTime()).isEqualTo(45);
    assertThat(request.getMinServings()).isEqualTo(BigDecimal.valueOf(2));
    assertThat(request.getMaxServings()).isEqualTo(BigDecimal.valueOf(6));
    assertThat(request.getTags()).isSameAs(tags);
  }

  @Test
  @DisplayName("Builder uses default ingredient match mode when not specified")
  @Tag("standard-processing")
  void builderUsesDefaultIngredientMatchMode() {
    // When
    SearchRecipesRequest request = SearchRecipesRequest.builder()
        .recipeNameQuery("Test Recipe")
        .build();

    // Then
    assertThat(request.getIngredientMatchMode()).isEqualTo(IngredientMatchMode.AND);
  }

  @Test
  @DisplayName("Setters and getters work for all fields")
  @Tag("standard-processing")
  void settersAndGettersWork() {
    // Given
    SearchRecipesRequest request = new SearchRecipesRequest();
    List<String> ingredients = Arrays.asList("tomato", "basil");
    List<String> tags = Arrays.asList("vegetarian", "italian");

    // When
    request.setRecipeNameQuery("Margherita Pizza");
    request.setIngredients(ingredients);
    request.setIngredientMatchMode(IngredientMatchMode.AND);
    request.setDifficulty(DifficultyLevel.HARD);
    request.setMaxCookingTime(25);
    request.setMaxPreparationTime(15);
    request.setMinServings(BigDecimal.valueOf(1));
    request.setMaxServings(BigDecimal.valueOf(4));
    request.setTags(tags);

    // Then
    assertThat(request.getRecipeNameQuery()).isEqualTo("Margherita Pizza");
    assertThat(request.getIngredients()).isSameAs(ingredients);
    assertThat(request.getIngredientMatchMode()).isEqualTo(IngredientMatchMode.AND);
    assertThat(request.getDifficulty()).isEqualTo(DifficultyLevel.HARD);
    assertThat(request.getMaxCookingTime()).isEqualTo(25);
    assertThat(request.getMaxPreparationTime()).isEqualTo(15);
    assertThat(request.getMinServings()).isEqualTo(BigDecimal.valueOf(1));
    assertThat(request.getMaxServings()).isEqualTo(BigDecimal.valueOf(4));
    assertThat(request.getTags()).isSameAs(tags);
  }

  @Test
  @DisplayName("NoArgsConstructor creates instance with null fields except default ingredient match mode")
  @Tag("standard-processing")
  void noArgsConstructorCreatesInstanceWithNullFields() {
    // When
    SearchRecipesRequest request = new SearchRecipesRequest();

    // Then
    assertThat(request.getRecipeNameQuery()).isNull();
    assertThat(request.getIngredients()).isNull();
    assertThat(request.getIngredientMatchMode()).isEqualTo(IngredientMatchMode.AND);
    assertThat(request.getDifficulty()).isNull();
    assertThat(request.getMaxCookingTime()).isNull();
    assertThat(request.getMaxPreparationTime()).isNull();
    assertThat(request.getMinServings()).isNull();
    assertThat(request.getMaxServings()).isNull();
    assertThat(request.getTags()).isNull();
  }

  @Test
  @DisplayName("AllArgsConstructor sets all fields correctly")
  @Tag("standard-processing")
  void allArgsConstructorSetsAllFields() {
    // Given
    List<String> ingredients = Arrays.asList("beef", "onion");
    List<String> tags = Arrays.asList("comfort-food", "winter");

    // When
    SearchRecipesRequest request = new SearchRecipesRequest(
        "Beef Stew",
        ingredients,
        IngredientMatchMode.OR,
        DifficultyLevel.EASY,
        60,
        20,
        BigDecimal.valueOf(3),
        BigDecimal.valueOf(8),
        tags);

    // Then
    assertThat(request.getRecipeNameQuery()).isEqualTo("Beef Stew");
    assertThat(request.getIngredients()).isSameAs(ingredients);
    assertThat(request.getIngredientMatchMode()).isEqualTo(IngredientMatchMode.OR);
    assertThat(request.getDifficulty()).isEqualTo(DifficultyLevel.EASY);
    assertThat(request.getMaxCookingTime()).isEqualTo(60);
    assertThat(request.getMaxPreparationTime()).isEqualTo(20);
    assertThat(request.getMinServings()).isEqualTo(BigDecimal.valueOf(3));
    assertThat(request.getMaxServings()).isEqualTo(BigDecimal.valueOf(8));
    assertThat(request.getTags()).isSameAs(tags);
  }

  @Test
  @DisplayName("Equals and hashCode work correctly for same content")
  @Tag("standard-processing")
  void equalsAndHashCodeWorkForSameContent() {
    // Given
    List<String> ingredients = Arrays.asList("flour", "eggs");

    SearchRecipesRequest request1 = SearchRecipesRequest.builder()
        .recipeNameQuery("Pasta")
        .ingredients(ingredients)
        .ingredientMatchMode(IngredientMatchMode.AND)
        .difficulty(DifficultyLevel.MEDIUM)
        .maxCookingTime(15)
        .build();

    SearchRecipesRequest request2 = SearchRecipesRequest.builder()
        .recipeNameQuery("Pasta")
        .ingredients(ingredients)
        .ingredientMatchMode(IngredientMatchMode.AND)
        .difficulty(DifficultyLevel.MEDIUM)
        .maxCookingTime(15)
        .build();

    // Then
    assertThat(request1).isEqualTo(request2);
    assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
  }

  @Test
  @DisplayName("Equals returns false for different content")
  @Tag("standard-processing")
  void equalsReturnsFalseForDifferentContent() {
    // Given
    SearchRecipesRequest request1 = SearchRecipesRequest.builder()
        .recipeNameQuery("Pasta")
        .build();

    SearchRecipesRequest request2 = SearchRecipesRequest.builder()
        .recipeNameQuery("Pizza")
        .build();

    // Then
    assertThat(request1).isNotEqualTo(request2);
  }

  @Test
  @DisplayName("ToString includes all field information")
  @Tag("standard-processing")
  void toStringIncludesAllFieldInformation() {
    // Given
    SearchRecipesRequest request = SearchRecipesRequest.builder()
        .recipeNameQuery("Test Recipe")
        .ingredients(Arrays.asList("ingredient1"))
        .ingredientMatchMode(IngredientMatchMode.OR)
        .difficulty(DifficultyLevel.HARD)
        .maxCookingTime(30)
        .maxPreparationTime(15)
        .minServings(BigDecimal.valueOf(2))
        .maxServings(BigDecimal.valueOf(4))
        .build();

    // When
    String toString = request.toString();

    // Then
    assertThat(toString).contains("SearchRecipesRequest");
    assertThat(toString).contains("recipeNameQuery=Test Recipe");
    assertThat(toString).contains("ingredients=[ingredient1]");
    assertThat(toString).contains("ingredientMatchMode=OR");
    assertThat(toString).contains("difficulty=HARD");
    assertThat(toString).contains("maxCookingTime=30");
    assertThat(toString).contains("maxPreparationTime=15");
    assertThat(toString).contains("minServings=2");
    assertThat(toString).contains("maxServings=4");
  }

  @Test
  @DisplayName("Builder allows partial field setting")
  @Tag("standard-processing")
  void builderAllowsPartialFieldSetting() {
    // When
    SearchRecipesRequest request = SearchRecipesRequest.builder()
        .recipeNameQuery("Quick Meal")
        .maxCookingTime(20)
        .build();

    // Then
    assertThat(request.getRecipeNameQuery()).isEqualTo("Quick Meal");
    assertThat(request.getMaxCookingTime()).isEqualTo(20);
    assertThat(request.getIngredients()).isNull();
    assertThat(request.getDifficulty()).isNull();
    assertThat(request.getIngredientMatchMode()).isEqualTo(IngredientMatchMode.AND); // default
  }

  @Test
  @DisplayName("Builder can override default ingredient match mode")
  @Tag("standard-processing")
  void builderCanOverrideDefaultIngredientMatchMode() {
    // When
    SearchRecipesRequest request = SearchRecipesRequest.builder()
        .ingredientMatchMode(IngredientMatchMode.OR)
        .build();

    // Then
    assertThat(request.getIngredientMatchMode()).isEqualTo(IngredientMatchMode.OR);
  }
}
