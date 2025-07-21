package com.recipe_manager.model.entity.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.enums.DifficultyLevel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Recipe entity.
 */
@Tag("unit")
class RecipeTest {

  private Recipe recipe;
  private UUID userId;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    now = LocalDateTime.now();
    recipe = Recipe.builder()
        .userId(userId)
        .title("Test Recipe")
        .description("A test recipe")
        .servings(BigDecimal.valueOf(4))
        .preparationTime(30)
        .cookingTime(45)
        .difficulty(DifficultyLevel.MEDIUM)
        .build();
  }

  @Test
  @DisplayName("Should create recipe with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeWithBuilder() {
    // Then
    assertThat(recipe.getUserId()).isEqualTo(userId);
    assertThat(recipe.getTitle()).isEqualTo("Test Recipe");
    assertThat(recipe.getDescription()).isEqualTo("A test recipe");
    assertThat(recipe.getServings()).isEqualTo(BigDecimal.valueOf(4));
    assertThat(recipe.getPreparationTime()).isEqualTo(30);
    assertThat(recipe.getCookingTime()).isEqualTo(45);
    assertThat(recipe.getDifficulty()).isEqualTo(DifficultyLevel.MEDIUM);
  }

  @Test
  @DisplayName("Should have empty collections by default")
  @Tag("standard-processing")
  void shouldHaveEmptyCollectionsByDefault() {
    // Then
    assertThat(recipe.getRecipeIngredients()).isEmpty();
    assertThat(recipe.getRecipeSteps()).isEmpty();
    assertThat(recipe.getRecipeRevisions()).isEmpty();
    assertThat(recipe.getRecipeFavorites()).isEmpty();
    assertThat(recipe.getRecipeTags()).isEmpty();
  }

  @Test
  @DisplayName("Should add recipe ingredient")
  @Tag("standard-processing")
  void shouldAddRecipeIngredient() {
    // Given
    Ingredient ingredient = Ingredient.builder()
        .name("Test Ingredient")
        .build();
    RecipeIngredient recipeIngredient = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(ingredient)
        .build();

    // When
    recipe.addRecipeIngredient(recipeIngredient);

    // Then
    assertThat(recipe.getRecipeIngredients()).hasSize(1);
    assertThat(recipeIngredient.getRecipe()).isEqualTo(recipe);
  }

  @Test
  @DisplayName("Should remove recipe ingredient")
  @Tag("standard-processing")
  void shouldRemoveRecipeIngredient() {
    // Given
    Ingredient ingredient = Ingredient.builder()
        .name("Test Ingredient")
        .build();
    RecipeIngredient recipeIngredient = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(ingredient)
        .build();
    recipe.addRecipeIngredient(recipeIngredient);

    // When
    recipe.removeRecipeIngredient(recipeIngredient);

    // Then
    assertThat(recipe.getRecipeIngredients()).isEmpty();
    assertThat(recipeIngredient.getRecipe()).isNull();
  }

  @Test
  @DisplayName("Should add recipe step")
  @Tag("standard-processing")
  void shouldAddRecipeStep() {
    // Given
    RecipeStep recipeStep = RecipeStep.builder()
        .recipe(recipe)
        .stepNumber(1)
        .instruction("Test instruction")
        .build();

    // When
    recipe.addRecipeStep(recipeStep);

    // Then
    assertThat(recipe.getRecipeSteps()).hasSize(1);
    assertThat(recipeStep.getRecipe()).isEqualTo(recipe);
  }

  @Test
  @DisplayName("Should remove recipe step")
  @Tag("standard-processing")
  void shouldRemoveRecipeStep() {
    // Given
    RecipeStep recipeStep = RecipeStep.builder()
        .recipe(recipe)
        .stepNumber(1)
        .instruction("Test instruction")
        .build();
    recipe.addRecipeStep(recipeStep);

    // When
    recipe.removeRecipeStep(recipeStep);

    // Then
    assertThat(recipe.getRecipeSteps()).isEmpty();
    assertThat(recipeStep.getRecipe()).isNull();
  }

  @Test
  @DisplayName("Should add recipe tag")
  @Tag("standard-processing")
  void shouldAddRecipeTag() {
    // Given
    RecipeTag recipeTag = RecipeTag.builder()
        .name("Test Tag")
        .build();

    // When
    recipe.addRecipeTag(recipeTag);

    // Then
    assertThat(recipe.getRecipeTags()).hasSize(1);
    assertThat(recipe.getRecipeTags()).contains(recipeTag);
  }

  @Test
  @DisplayName("Should remove recipe tag")
  @Tag("standard-processing")
  void shouldRemoveRecipeTag() {
    // Given
    RecipeTag recipeTag = RecipeTag.builder()
        .name("Test Tag")
        .build();
    recipe.addRecipeTag(recipeTag);

    // When
    recipe.removeRecipeTag(recipeTag);

    // Then
    assertThat(recipe.getRecipeTags()).isEmpty();
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    Long recipeId = 1L;
    String originUrl = "https://example.com/recipe";
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now();

    // When
    recipe.setRecipeId(recipeId);
    recipe.setOriginUrl(originUrl);
    recipe.setCreatedAt(createdAt);
    recipe.setUpdatedAt(updatedAt);

    // Then
    assertThat(recipe.getRecipeId()).isEqualTo(recipeId);
    assertThat(recipe.getOriginUrl()).isEqualTo(originUrl);
    assertThat(recipe.getCreatedAt()).isEqualTo(createdAt);
    assertThat(recipe.getUpdatedAt()).isEqualTo(updatedAt);
  }

  @Test
  @DisplayName("Should have correct toString representation")
  @Tag("standard-processing")
  void shouldHaveCorrectToStringRepresentation() {
    // When
    String toString = recipe.toString();

    // Then
    assertThat(toString).contains("Test Recipe");
    assertThat(toString).contains("A test recipe");
    assertThat(toString).contains("MEDIUM");
  }

  @Test
  @DisplayName("Builder should defensively copy all mutable lists")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyAllMutableLists() {
    java.util.List<RecipeIngredient> ingredients = new java.util.ArrayList<>();
    java.util.List<RecipeStep> steps = new java.util.ArrayList<>();
    java.util.List<RecipeRevision> revisions = new java.util.ArrayList<>();
    java.util.List<RecipeFavorite> favorites = new java.util.ArrayList<>();
    java.util.List<RecipeTag> tags = new java.util.ArrayList<>();
    ingredients.add(RecipeIngredient.builder().isOptional(false).build());
    steps.add(RecipeStep.builder().stepNumber(1).build());
    revisions.add(RecipeRevision.builder().userId(userId).build());
    favorites.add(RecipeFavorite.builder().userId(userId).build());
    tags.add(RecipeTag.builder().name("T").build());
    Recipe r = Recipe.builder().recipeIngredients(ingredients).recipeSteps(steps).recipeRevisions(revisions)
        .recipeFavorites(favorites).recipeTags(tags).build();
    ingredients.add(RecipeIngredient.builder().isOptional(true).build());
    steps.add(RecipeStep.builder().stepNumber(2).build());
    revisions.add(RecipeRevision.builder().userId(UUID.randomUUID()).build());
    favorites.add(RecipeFavorite.builder().userId(UUID.randomUUID()).build());
    tags.add(RecipeTag.builder().name("T2").build());
    assertThat(r.getRecipeIngredients()).hasSize(1);
    assertThat(r.getRecipeSteps()).hasSize(1);
    assertThat(r.getRecipeRevisions()).hasSize(1);
    assertThat(r.getRecipeFavorites()).hasSize(1);
    assertThat(r.getRecipeTags()).hasSize(1);
  }

  @Test
  @DisplayName("Getters and setters should defensively copy all mutable lists")
  @Tag("standard-processing")
  void gettersAndSettersShouldDefensivelyCopyAllMutableLists() {
    Recipe r = Recipe.builder().userId(userId).title("T").build();
    java.util.List<RecipeIngredient> ingredients = new java.util.ArrayList<>();
    java.util.List<RecipeStep> steps = new java.util.ArrayList<>();
    java.util.List<RecipeRevision> revisions = new java.util.ArrayList<>();
    java.util.List<RecipeFavorite> favorites = new java.util.ArrayList<>();
    java.util.List<RecipeTag> tags = new java.util.ArrayList<>();
    ingredients.add(RecipeIngredient.builder().isOptional(false).build());
    steps.add(RecipeStep.builder().stepNumber(1).build());
    revisions.add(RecipeRevision.builder().userId(userId).build());
    favorites.add(RecipeFavorite.builder().userId(userId).build());
    tags.add(RecipeTag.builder().name("T").build());
    r.setRecipeIngredients(ingredients);
    r.setRecipeSteps(steps);
    r.setRecipeRevisions(revisions);
    r.setRecipeFavorites(favorites);
    r.setRecipeTags(tags);
    ingredients.add(RecipeIngredient.builder().isOptional(true).build());
    steps.add(RecipeStep.builder().stepNumber(2).build());
    revisions.add(RecipeRevision.builder().userId(UUID.randomUUID()).build());
    favorites.add(RecipeFavorite.builder().userId(UUID.randomUUID()).build());
    tags.add(RecipeTag.builder().name("T2").build());
    assertThat(r.getRecipeIngredients()).hasSize(1);
    assertThat(r.getRecipeSteps()).hasSize(1);
    assertThat(r.getRecipeRevisions()).hasSize(1);
    assertThat(r.getRecipeFavorites()).hasSize(1);
    assertThat(r.getRecipeTags()).hasSize(1);
  }

  @Test
  @DisplayName("All-args constructor should defensively copy all mutable lists")
  @Tag("standard-processing")
  void allArgsConstructorShouldDefensivelyCopyAllMutableLists() {
    java.util.List<RecipeIngredient> ingredients = new java.util.ArrayList<>();
    java.util.List<RecipeStep> steps = new java.util.ArrayList<>();
    java.util.List<RecipeRevision> revisions = new java.util.ArrayList<>();
    java.util.List<RecipeFavorite> favorites = new java.util.ArrayList<>();
    java.util.List<RecipeTag> tags = new java.util.ArrayList<>();
    ingredients.add(RecipeIngredient.builder().isOptional(false).build());
    steps.add(RecipeStep.builder().stepNumber(1).build());
    revisions.add(RecipeRevision.builder().userId(userId).build());
    favorites.add(RecipeFavorite.builder().userId(userId).build());
    tags.add(RecipeTag.builder().name("T").build());
    Recipe r = new Recipe(1L, userId, "T", "D", "U", java.math.BigDecimal.ONE, 1, 1,
        com.recipe_manager.model.enums.DifficultyLevel.EASY, java.time.LocalDateTime.now(),
        java.time.LocalDateTime.now(), ingredients, steps, revisions, favorites, tags);
    ingredients.add(RecipeIngredient.builder().isOptional(true).build());
    steps.add(RecipeStep.builder().stepNumber(2).build());
    revisions.add(RecipeRevision.builder().userId(UUID.randomUUID()).build());
    favorites.add(RecipeFavorite.builder().userId(UUID.randomUUID()).build());
    tags.add(RecipeTag.builder().name("T2").build());
    assertThat(r.getRecipeIngredients()).hasSize(1);
    assertThat(r.getRecipeSteps()).hasSize(1);
    assertThat(r.getRecipeRevisions()).hasSize(1);
    assertThat(r.getRecipeFavorites()).hasSize(1);
    assertThat(r.getRecipeTags()).hasSize(1);
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    Recipe r = new Recipe(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null);
    assertThat(r.getRecipeId()).isNull();
    assertThat(r.getUserId()).isNull();
    assertThat(r.getTitle()).isNull();
    assertThat(r.getDescription()).isNull();
    assertThat(r.getOriginUrl()).isNull();
    assertThat(r.getServings()).isNull();
    assertThat(r.getPreparationTime()).isNull();
    assertThat(r.getCookingTime()).isNull();
    assertThat(r.getDifficulty()).isNull();
    assertThat(r.getCreatedAt()).isNull();
    assertThat(r.getUpdatedAt()).isNull();
    assertThat(r.getRecipeIngredients()).isEmpty();
    assertThat(r.getRecipeSteps()).isEmpty();
    assertThat(r.getRecipeRevisions()).isEmpty();
    assertThat(r.getRecipeFavorites()).isEmpty();
    assertThat(r.getRecipeTags()).isEmpty();
  }

  @Test
  @DisplayName("Equals and hashCode: self, null, different type, different values, all-null fields")
  @Tag("standard-processing")
  void equalsAndHashCodeEdgeCases() {
    Recipe r1 = Recipe.builder().userId(userId).title("T").build();
    Recipe r2 = Recipe.builder().userId(userId).title("T").build();
    Recipe r3 = Recipe.builder().userId(UUID.randomUUID()).title("T2").build();
    assertThat(r1).isEqualTo(r1);
    assertThat(r1).isEqualTo(r2);
    assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    assertThat(r1).isNotEqualTo(r3);
    assertThat(r1.hashCode()).isNotEqualTo(r3.hashCode());
    assertThat(r1).isNotEqualTo(null);
    assertThat(r1).isNotEqualTo(new Object());
    Recipe rNulls1 = new Recipe(null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null);
    Recipe rNulls2 = new Recipe(null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null);
    assertThat(rNulls1).isEqualTo(rNulls2);
    assertThat(rNulls1.hashCode()).isEqualTo(rNulls2.hashCode());
  }
}
