package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.dto.request.UpdateRecipeRequest;
import com.recipe_manager.model.dto.revision.IngredientAddRevision;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeFavorite;
import com.recipe_manager.model.entity.recipe.RecipeFavoriteId;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.entity.recipe.RecipeRevision;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.entity.recipe.RecipeTag;
import com.recipe_manager.model.enums.DifficultyLevel;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Unit tests for RecipeMapper.
 */
@Tag("unit")
@SpringBootTest(classes = {
    RecipeMapperImpl.class,
    RecipeIngredientMapperImpl.class,
    RecipeStepMapperImpl.class,
    RecipeFavoriteMapperImpl.class,
    RecipeRevisionMapperImpl.class,
    RecipeTagMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
class RecipeMapperTest {
  @Test
  @DisplayName("Should map Recipe entity to RecipeDto")
  void shouldMapRecipeEntityToDto() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    Recipe recipe = Recipe.builder()
        .recipeId(1L)
        .userId(userId)
        .title("Original Title")
        .description("Original Description")
        .originUrl("https://original.example.com")
        .servings(BigDecimal.valueOf(4.0))
        .preparationTime(30)
        .cookingTime(45)
        .difficulty(DifficultyLevel.MEDIUM)
        .createdAt(now)
        .updatedAt(now)
        .recipeIngredients(new java.util.ArrayList<>())
        .recipeSteps(new java.util.ArrayList<>())
        .recipeFavorites(new java.util.ArrayList<>())
        .recipeRevisions(new java.util.ArrayList<>())
        .recipeTags(new java.util.ArrayList<>())
        .build();
    RecipeIngredient ingredient1 = RecipeIngredient.builder()
        .recipe(recipe)
        .quantity(BigDecimal.valueOf(2.0))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();
    recipe.getRecipeIngredients().add(ingredient1);
    RecipeStep step1 = RecipeStep.builder()
        .recipe(recipe)
        .stepNumber(1)
        .instruction("Mix ingredients")
        .build();
    recipe.getRecipeSteps().add(step1);
    RecipeDto result = recipeMapper.toDto(recipe);
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(1L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getTitle()).isEqualTo("Original Title");
    assertThat(result.getDescription()).isEqualTo("Original Description");
    assertThat(result.getOriginUrl()).isEqualTo("https://original.example.com");
    assertThat(result.getServings()).isEqualByComparingTo(BigDecimal.valueOf(4.0));
    assertThat(result.getPreparationTime()).isEqualTo(30);
    assertThat(result.getCookingTime()).isEqualTo(45);
    assertThat(result.getDifficulty()).isEqualTo(DifficultyLevel.MEDIUM);
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getUpdatedAt()).isEqualTo(now);
    assertThat(result.getIngredients()).isNotNull();
    assertThat(result.getSteps()).isNotNull();
    assertThat(result.getFavorites()).isNotNull();
    assertThat(result.getRevisions()).isNotNull();
    assertThat(result.getTags()).isNotNull();
  }

  @Test
  @DisplayName("Should handle null Recipe entity")
  void shouldHandleNullRecipeEntity() {
    RecipeDto result = recipeMapper.toDto(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should map Recipe with null collections")
  void shouldMapRecipeWithNullCollections() {
    UUID userId = UUID.randomUUID();
    Recipe recipeWithNullCollections = Recipe.builder()
        .recipeId(2L)
        .userId(userId)
        .title("Test Recipe")
        .recipeIngredients(null)
        .recipeSteps(null)
        .recipeFavorites(null)
        .recipeRevisions(null)
        .recipeTags(null)
        .build();
    RecipeDto result = recipeMapper.toDto(recipeWithNullCollections);
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(2L);
    assertThat(result.getTitle()).isEqualTo("Test Recipe");
    assertThat(result.getIngredients()).isNull();
    assertThat(result.getSteps()).isNull();
    assertThat(result.getFavorites()).isNull();
    assertThat(result.getRevisions()).isNull();
    assertThat(result.getTags()).isNull();
  }

  @Test
  @DisplayName("Should update recipe from request")
  void shouldUpdateRecipeFromRequest() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    Recipe originalRecipe = Recipe.builder()
        .recipeId(1L)
        .userId(userId)
        .title("Original Title")
        .description("Original Description")
        .originUrl("https://original.example.com")
        .servings(BigDecimal.valueOf(4.0))
        .preparationTime(30)
        .cookingTime(45)
        .difficulty(DifficultyLevel.MEDIUM)
        .createdAt(now)
        .updatedAt(now)
        .recipeIngredients(new java.util.ArrayList<>())
        .recipeSteps(new java.util.ArrayList<>())
        .build();
    UpdateRecipeRequest updateRequest = UpdateRecipeRequest.builder()
        .title("Updated Title")
        .description("Updated Description")
        .originUrl("https://updated.example.com")
        .servings(BigDecimal.valueOf(6.0))
        .preparationTime(20)
        .cookingTime(30)
        .difficulty(DifficultyLevel.EASY)
        .ingredients(java.util.Arrays.asList(
            RecipeIngredientDto.builder()
                .ingredientName("Sugar")
                .quantity(BigDecimal.valueOf(1.0))
                .unit(IngredientUnit.CUP)
                .build()))
        .steps(java.util.Arrays.asList(
            RecipeStepDto.builder()
                .stepNumber(1)
                .instruction("Updated instruction")
                .build()))
        .build();
    recipeMapper.updateRecipeFromRequest(updateRequest, originalRecipe);
    assertThat(originalRecipe.getTitle()).isEqualTo("Updated Title");
    assertThat(originalRecipe.getDescription()).isEqualTo("Updated Description");
    assertThat(originalRecipe.getOriginUrl()).isEqualTo("https://updated.example.com");
    assertThat(originalRecipe.getServings()).isEqualByComparingTo(BigDecimal.valueOf(6.0));
    assertThat(originalRecipe.getPreparationTime()).isEqualTo(20);
    assertThat(originalRecipe.getCookingTime()).isEqualTo(30);
    assertThat(originalRecipe.getDifficulty()).isEqualTo(DifficultyLevel.EASY);
    assertThat(originalRecipe.getRecipeId()).isEqualTo(1L);
    assertThat(originalRecipe.getUserId()).isEqualTo(userId);
    assertThat(originalRecipe.getCreatedAt()).isEqualTo(now);
    assertThat(originalRecipe.getUpdatedAt()).isEqualTo(now);
    assertThat(originalRecipe.getRecipeIngredients()).isNotNull();
    assertThat(originalRecipe.getRecipeSteps()).isNotNull();
  }

  @Test
  @DisplayName("Should handle partial update with null fields")
  void shouldHandlePartialUpdateWithNullFields() {
    UUID userId = UUID.randomUUID();
    Recipe originalRecipe = Recipe.builder()
        .recipeId(1L)
        .userId(userId)
        .title("Original Title")
        .description("Original Description")
        .servings(BigDecimal.valueOf(4.0))
        .preparationTime(30)
        .build();
    UpdateRecipeRequest partialRequest = UpdateRecipeRequest.builder()
        .title("Updated Title")
        .build();
    recipeMapper.updateRecipeFromRequest(partialRequest, originalRecipe);
    assertThat(originalRecipe.getTitle()).isEqualTo("Updated Title");
    assertThat(originalRecipe.getDescription()).isNull();
    assertThat(originalRecipe.getServings()).isNull();
    assertThat(originalRecipe.getPreparationTime()).isNull();
  }

  @Test
  @DisplayName("Should handle null update request")
  void shouldHandleNullUpdateRequest() {
    Recipe originalRecipe = Recipe.builder()
        .title("Original Title")
        .description("Original Description")
        .build();
    recipeMapper.updateRecipeFromRequest(null, originalRecipe);
    assertThat(originalRecipe.getTitle()).isEqualTo("Original Title");
    assertThat(originalRecipe.getDescription()).isEqualTo("Original Description");
  }

  @Test
  @DisplayName("Should handle update request with null collections")
  void shouldHandleUpdateRequestWithNullCollections() {
    Recipe originalRecipe = Recipe.builder()
        .title("Original Title")
        .recipeIngredients(new java.util.ArrayList<>())
        .recipeSteps(new java.util.ArrayList<>())
        .build();
    UpdateRecipeRequest requestWithNullCollections = UpdateRecipeRequest.builder()
        .title("Updated Title")
        .ingredients(null)
        .steps(null)
        .build();
    recipeMapper.updateRecipeFromRequest(requestWithNullCollections, originalRecipe);
    assertThat(originalRecipe.getTitle()).isEqualTo("Updated Title");
    assertThat(originalRecipe.getRecipeIngredients()).isNull();
    assertThat(originalRecipe.getRecipeSteps()).isNull();
  }

  @Test
  @DisplayName("Should handle empty collections in update request")
  void shouldHandleEmptyCollectionsInUpdateRequest() {
    Recipe originalRecipe = Recipe.builder()
        .title("Original Title")
        .recipeIngredients(new java.util.ArrayList<>())
        .recipeSteps(new java.util.ArrayList<>())
        .build();
    UpdateRecipeRequest requestWithEmptyCollections = UpdateRecipeRequest.builder()
        .title("Updated Title")
        .ingredients(new java.util.ArrayList<>())
        .steps(new java.util.ArrayList<>())
        .build();
    recipeMapper.updateRecipeFromRequest(requestWithEmptyCollections, originalRecipe);
    assertThat(originalRecipe.getTitle()).isEqualTo("Updated Title");
    assertThat(originalRecipe.getRecipeIngredients()).isNotNull().isEmpty();
    assertThat(originalRecipe.getRecipeSteps()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("Should preserve Recipe ID fields during update")
  void shouldPreserveRecipeIdFieldsDuringUpdate() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    Recipe originalRecipe = Recipe.builder()
        .recipeId(999L)
        .userId(userId)
        .title("Original Title")
        .createdAt(now.minusDays(1))
        .updatedAt(now.minusHours(1))
        .build();
    Long originalRecipeId = originalRecipe.getRecipeId();
    UUID originalUserId = originalRecipe.getUserId();
    LocalDateTime originalCreatedAt = originalRecipe.getCreatedAt();
    LocalDateTime originalUpdatedAt = originalRecipe.getUpdatedAt();
    UpdateRecipeRequest updateRequest = UpdateRecipeRequest.builder()
        .title("Updated Title")
        .description("Updated Description")
        .originUrl("https://updated.example.com")
        .servings(BigDecimal.valueOf(6.0))
        .preparationTime(20)
        .cookingTime(30)
        .difficulty(DifficultyLevel.EASY)
        .ingredients(java.util.Arrays.asList(
            RecipeIngredientDto.builder()
                .ingredientName("Sugar")
                .quantity(BigDecimal.valueOf(1.0))
                .unit(IngredientUnit.CUP)
                .build()))
        .steps(java.util.Arrays.asList(
            RecipeStepDto.builder()
                .stepNumber(1)
                .instruction("Updated instruction")
                .build()))
        .build();
    recipeMapper.updateRecipeFromRequest(updateRequest, originalRecipe);
    assertThat(originalRecipe.getRecipeId()).isEqualTo(originalRecipeId);
    assertThat(originalRecipe.getUserId()).isEqualTo(originalUserId);
    assertThat(originalRecipe.getCreatedAt()).isEqualTo(originalCreatedAt);
    assertThat(originalRecipe.getUpdatedAt()).isEqualTo(originalUpdatedAt);
    assertThat(originalRecipe.getTitle()).isEqualTo("Updated Title");
    assertThat(originalRecipe.getDescription()).isEqualTo("Updated Description");
  }

  @Autowired
  private RecipeMapper recipeMapper;

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map all fields from UpdateRecipeRequest to Recipe, including nested collections")
  void shouldMapAllFields() {
    RecipeIngredientDto ingredientDto = RecipeIngredientDto.builder()
        .ingredientName("Flour")
        .quantity(BigDecimal.valueOf(1.5))
        .isOptional(false)
        .build();
    RecipeStepDto stepDto = RecipeStepDto.builder()
        .stepNumber(1)
        .instruction("Mix ingredients")
        .optional(false)
        .timerSeconds(60)
        .build();
    UpdateRecipeRequest updateRequest = UpdateRecipeRequest.builder()
        .title("Bread")
        .description("Simple bread recipe")
        .originUrl("http://example.com")
        .servings(BigDecimal.valueOf(2))
        .preparationTime(10)
        .cookingTime(30)
        .difficulty(null)
        .ingredients(List.of(ingredientDto))
        .steps(List.of(stepDto))
        .build();
    Recipe recipe = Recipe.builder().build();

    recipeMapper.updateRecipeFromRequest(updateRequest, recipe);

    assertThat(recipe.getTitle()).isEqualTo("Bread");
    assertThat(recipe.getDescription()).isEqualTo("Simple bread recipe");
    assertThat(recipe.getOriginUrl()).isEqualTo("http://example.com");
    assertThat(recipe.getServings()).isEqualTo(BigDecimal.valueOf(2));
    assertThat(recipe.getPreparationTime()).isEqualTo(10);
    assertThat(recipe.getCookingTime()).isEqualTo(30);
    assertThat(recipe.getDifficulty()).isNull();
    assertThat(recipe.getRecipeIngredients()).hasSize(1);
    RecipeIngredient mappedIngredient = recipe.getRecipeIngredients().get(0);
    assertThat(mappedIngredient.getQuantity()).isEqualTo(BigDecimal.valueOf(1.5));
    assertThat(mappedIngredient.getIsOptional()).isFalse();
    assertThat(recipe.getRecipeSteps()).hasSize(1);
    RecipeStep mappedStep = recipe.getRecipeSteps().get(0);
    assertThat(mappedStep.getStepNumber()).isEqualTo(1);
    assertThat(mappedStep.getInstruction()).isEqualTo("Mix ingredients");
    assertThat(mappedStep.getOptional()).isFalse();
    assertThat(mappedStep.getTimerSeconds()).isEqualTo(60);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map Recipe entity to RecipeDto with all nested collections")
  void shouldMapRecipeToDto() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    // Create recipe ingredients
    Ingredient ingredient = Ingredient.builder()
        .ingredientId(1L)
        .name("Flour")
        .build();
    RecipeIngredient recipeIngredient = RecipeIngredient.builder()
        .ingredient(ingredient)
        .quantity(BigDecimal.valueOf(2.0))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    // Create recipe steps
    RecipeStep recipeStep = RecipeStep.builder()
        .stepNumber(1)
        .instruction("Mix ingredients")
        .optional(false)
        .timerSeconds(120)
        .createdAt(now)
        .build();

    // Create recipe tags
    RecipeTag recipeTag = RecipeTag.builder()
        .tagId(1L)
        .name("Breakfast")
        .build();

    // Create recipe favorites
    RecipeFavoriteId favoriteId = RecipeFavoriteId.builder()
        .userId(userId)
        .recipeId(1L) // Assuming this recipe will have ID 1L
        .build();

    RecipeFavorite recipeFavorite = RecipeFavorite.builder()
        .id(favoriteId)
        .favoritedAt(now)
        .build();

    // Create recipe revisions
    IngredientAddRevision revisionData = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(new BigDecimal("2.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    RecipeRevision recipeRevision = RecipeRevision.builder()
        .revisionId(1L)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.ADD)
        .previousData(revisionData)
        .newData(revisionData)
        .changeComment("Added flour")
        .createdAt(now)
        .build();

    // Build complete recipe
    Recipe recipe = Recipe.builder()
        .recipeId(1L)
        .userId(userId)
        .title("Test Recipe")
        .description("A test recipe")
        .originUrl("http://example.com")
        .servings(BigDecimal.valueOf(4))
        .preparationTime(15)
        .cookingTime(30)
        .difficulty(DifficultyLevel.EASY)
        .createdAt(now)
        .updatedAt(now)
        .recipeIngredients(List.of(recipeIngredient))
        .recipeSteps(List.of(recipeStep))
        .recipeTags(List.of(recipeTag))
        .recipeFavorites(List.of(recipeFavorite))
        .recipeRevisions(List.of(recipeRevision))
        .build();

    // Set up bidirectional relationships
    recipeIngredient.setRecipe(recipe);
    recipeStep.setRecipe(recipe);
    recipeFavorite.setRecipe(recipe);
    recipeRevision.setRecipe(recipe);

    RecipeDto result = recipeMapper.toDto(recipe);

    // Verify basic recipe fields
    assertThat(result.getRecipeId()).isEqualTo(1L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getTitle()).isEqualTo("Test Recipe");
    assertThat(result.getDescription()).isEqualTo("A test recipe");
    assertThat(result.getOriginUrl()).isEqualTo("http://example.com");
    assertThat(result.getServings()).isEqualTo(BigDecimal.valueOf(4));
    assertThat(result.getPreparationTime()).isEqualTo(15);
    assertThat(result.getCookingTime()).isEqualTo(30);
    assertThat(result.getDifficulty()).isEqualTo(DifficultyLevel.EASY);
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getUpdatedAt()).isEqualTo(now);

    // Verify nested collections
    assertThat(result.getIngredients()).hasSize(1);
    assertThat(result.getIngredients().get(0).getIngredientName()).isEqualTo("Flour");
    assertThat(result.getIngredients().get(0).getQuantity()).isEqualTo(BigDecimal.valueOf(2.0));

    assertThat(result.getSteps()).hasSize(1);
    assertThat(result.getSteps().get(0).getInstruction()).isEqualTo("Mix ingredients");
    assertThat(result.getSteps().get(0).getStepNumber()).isEqualTo(1);

    assertThat(result.getTags()).hasSize(1);
    assertThat(result.getTags().get(0).getName()).isEqualTo("Breakfast");
    assertThat(result.getTags().get(0).getTagId()).isEqualTo(1L);

    assertThat(result.getFavorites()).hasSize(1);
    assertThat(result.getFavorites().get(0).getUserId()).isEqualTo(userId);
    assertThat(result.getFavorites().get(0).getRecipeId()).isEqualTo(1L);

    assertThat(result.getRevisions()).hasSize(1);
    assertThat(result.getRevisions().get(0).getRevisionId()).isEqualTo(1L);
    assertThat(result.getRevisions().get(0).getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(result.getRevisions().get(0).getChangeComment()).isEqualTo("Added flour");
  }
}
