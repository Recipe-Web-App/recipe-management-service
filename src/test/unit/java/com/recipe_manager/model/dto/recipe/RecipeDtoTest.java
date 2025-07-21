package com.recipe_manager.model.dto.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.enums.DifficultyLevel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeDto class.
 */
@Tag("unit")
class RecipeDtoTest {

  private static final Long RECIPE_ID = 1L;
  private static final UUID USER_ID = UUID.randomUUID();
  private static final String TITLE = "Test Recipe";
  private static final String DESCRIPTION = "A delicious test recipe";
  private static final String ORIGIN_URL = "https://example.com/recipe";
  private static final BigDecimal SERVINGS = new BigDecimal("4.0");
  private static final Integer PREPARATION_TIME = 30;
  private static final Integer COOKING_TIME = 45;
  private static final DifficultyLevel DIFFICULTY = DifficultyLevel.MEDIUM;
  private static final LocalDateTime CREATED_AT = LocalDateTime.of(2023, 1, 1, 12, 0);
  private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2023, 1, 2, 12, 0);

  @Test
  @DisplayName("Should create RecipeDto with constructor")
  @Tag("standard-processing")
  void shouldCreateRecipeDtoWithConstructor() {
    // Given
    List<RecipeIngredientDto> ingredients = Arrays.asList(new RecipeIngredientDto());
    List<RecipeStepDto> steps = Arrays.asList(new RecipeStepDto());
    List<RecipeTagDto> tags = Arrays.asList(new RecipeTagDto());
    List<RecipeRevisionDto> revisions = Arrays.asList(new RecipeRevisionDto());
    List<RecipeFavoriteDto> favorites = Arrays.asList(new RecipeFavoriteDto());

    // When
    RecipeDto recipeDto = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        ingredients, steps, tags, revisions, favorites);

    // Then
    assertThat(recipeDto.getRecipeId()).isEqualTo(RECIPE_ID);
    assertThat(recipeDto.getUserId()).isEqualTo(USER_ID);
    assertThat(recipeDto.getTitle()).isEqualTo(TITLE);
    assertThat(recipeDto.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(recipeDto.getOriginUrl()).isEqualTo(ORIGIN_URL);
    assertThat(recipeDto.getServings()).isEqualTo(SERVINGS);
    assertThat(recipeDto.getPreparationTime()).isEqualTo(PREPARATION_TIME);
    assertThat(recipeDto.getCookingTime()).isEqualTo(COOKING_TIME);
    assertThat(recipeDto.getDifficulty()).isEqualTo(DIFFICULTY);
    assertThat(recipeDto.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(recipeDto.getUpdatedAt()).isEqualTo(UPDATED_AT);
    assertThat(recipeDto.getIngredients()).isEqualTo(ingredients);
    assertThat(recipeDto.getSteps()).isEqualTo(steps);
    assertThat(recipeDto.getTags()).isEqualTo(tags);
    assertThat(recipeDto.getRevisions()).isEqualTo(revisions);
    assertThat(recipeDto.getFavorites()).isEqualTo(favorites);
  }

  @Test
  @DisplayName("Should create RecipeDto with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeDtoWithBuilder() {
    // When
    RecipeDto recipeDto = RecipeDto.builder()
        .recipeId(RECIPE_ID)
        .userId(USER_ID)
        .title(TITLE)
        .description(DESCRIPTION)
        .originUrl(ORIGIN_URL)
        .servings(SERVINGS)
        .preparationTime(PREPARATION_TIME)
        .cookingTime(COOKING_TIME)
        .difficulty(DIFFICULTY)
        .createdAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .build();

    // Then
    assertThat(recipeDto.getRecipeId()).isEqualTo(RECIPE_ID);
    assertThat(recipeDto.getUserId()).isEqualTo(USER_ID);
    assertThat(recipeDto.getTitle()).isEqualTo(TITLE);
    assertThat(recipeDto.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(recipeDto.getOriginUrl()).isEqualTo(ORIGIN_URL);
    assertThat(recipeDto.getServings()).isEqualTo(SERVINGS);
    assertThat(recipeDto.getPreparationTime()).isEqualTo(PREPARATION_TIME);
    assertThat(recipeDto.getCookingTime()).isEqualTo(COOKING_TIME);
    assertThat(recipeDto.getDifficulty()).isEqualTo(DIFFICULTY);
    assertThat(recipeDto.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(recipeDto.getUpdatedAt()).isEqualTo(UPDATED_AT);
  }

  @Test
  @DisplayName("Should set and get RecipeDto properties")
  @Tag("standard-processing")
  void shouldSetAndGetRecipeDtoProperties() {
    // Given
    RecipeDto recipeDto = new RecipeDto();

    // When
    recipeDto.setRecipeId(RECIPE_ID);
    recipeDto.setUserId(USER_ID);
    recipeDto.setTitle(TITLE);
    recipeDto.setDescription(DESCRIPTION);
    recipeDto.setOriginUrl(ORIGIN_URL);
    recipeDto.setServings(SERVINGS);
    recipeDto.setPreparationTime(PREPARATION_TIME);
    recipeDto.setCookingTime(COOKING_TIME);
    recipeDto.setDifficulty(DIFFICULTY);
    recipeDto.setCreatedAt(CREATED_AT);
    recipeDto.setUpdatedAt(UPDATED_AT);

    // Then
    assertThat(recipeDto.getRecipeId()).isEqualTo(RECIPE_ID);
    assertThat(recipeDto.getUserId()).isEqualTo(USER_ID);
    assertThat(recipeDto.getTitle()).isEqualTo(TITLE);
    assertThat(recipeDto.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(recipeDto.getOriginUrl()).isEqualTo(ORIGIN_URL);
    assertThat(recipeDto.getServings()).isEqualTo(SERVINGS);
    assertThat(recipeDto.getPreparationTime()).isEqualTo(PREPARATION_TIME);
    assertThat(recipeDto.getCookingTime()).isEqualTo(COOKING_TIME);
    assertThat(recipeDto.getDifficulty()).isEqualTo(DIFFICULTY);
    assertThat(recipeDto.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(recipeDto.getUpdatedAt()).isEqualTo(UPDATED_AT);
  }

  @Test
  @DisplayName("Should return unmodifiable list for ingredients")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForIngredients() {
    // Given
    List<RecipeIngredientDto> ingredients = new ArrayList<>();
    RecipeDto recipeDto = new RecipeDto();
    recipeDto.setIngredients(ingredients);

    // When & Then
    assertThat(recipeDto.getIngredients()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should return unmodifiable list for steps")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForSteps() {
    // Given
    List<RecipeStepDto> steps = new ArrayList<>();
    RecipeDto recipeDto = new RecipeDto();
    recipeDto.setSteps(steps);

    // When & Then
    assertThat(recipeDto.getSteps()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should return unmodifiable list for tags")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForTags() {
    // Given
    List<RecipeTagDto> tags = new ArrayList<>();
    RecipeDto recipeDto = new RecipeDto();
    recipeDto.setTags(tags);

    // When & Then
    assertThat(recipeDto.getTags()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should return unmodifiable list for revisions")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForRevisions() {
    // Given
    List<RecipeRevisionDto> revisions = new ArrayList<>();
    RecipeDto recipeDto = new RecipeDto();
    recipeDto.setRevisions(revisions);

    // When & Then
    assertThat(recipeDto.getRevisions()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should return unmodifiable list for favorites")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForFavorites() {
    // Given
    List<RecipeFavoriteDto> favorites = new ArrayList<>();
    RecipeDto recipeDto = new RecipeDto();
    recipeDto.setFavorites(favorites);

    // When & Then
    assertThat(recipeDto.getFavorites()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should defensively copy ingredients list")
  @Tag("standard-processing")
  void shouldDefensivelyCopyIngredientsList() {
    // Given
    List<RecipeIngredientDto> originalIngredients = new ArrayList<>();
    originalIngredients.add(new RecipeIngredientDto());
    RecipeDto recipeDto = new RecipeDto();
    recipeDto.setIngredients(originalIngredients);

    // When
    originalIngredients.add(new RecipeIngredientDto());

    // Then
    assertThat(recipeDto.getIngredients()).hasSize(1);
  }

  @Test
  @DisplayName("Should defensively copy steps list")
  @Tag("standard-processing")
  void shouldDefensivelyCopyStepsList() {
    // Given
    List<RecipeStepDto> originalSteps = new ArrayList<>();
    originalSteps.add(new RecipeStepDto());
    RecipeDto recipeDto = new RecipeDto();
    recipeDto.setSteps(originalSteps);

    // When
    originalSteps.add(new RecipeStepDto());

    // Then
    assertThat(recipeDto.getSteps()).hasSize(1);
  }

  @Test
  @DisplayName("Should handle null ingredients in constructor")
  @Tag("error-processing")
  void shouldHandleNullIngredientsInConstructor() {
    // When
    RecipeDto recipeDto = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        null, null, null, null, null);

    // Then
    assertThat(recipeDto.getIngredients()).isEmpty();
    assertThat(recipeDto.getSteps()).isEmpty();
    assertThat(recipeDto.getTags()).isEmpty();
    assertThat(recipeDto.getRevisions()).isEmpty();
    assertThat(recipeDto.getFavorites()).isEmpty();
  }

  @Test
  @DisplayName("Should handle null ingredients in setter")
  @Tag("error-processing")
  void shouldHandleNullIngredientsInSetter() {
    // Given
    RecipeDto recipeDto = new RecipeDto();

    // When
    recipeDto.setIngredients(null);

    // Then
    assertThat(recipeDto.getIngredients()).isEmpty();
  }

  @Test
  @DisplayName("Should return true when comparing equal RecipeDto objects")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingEqualRecipeDtoObjects() {
    // Given
    RecipeDto recipeDto1 = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    RecipeDto recipeDto2 = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(recipeDto1).isEqualTo(recipeDto2);
    assertThat(recipeDto2).isEqualTo(recipeDto1);
  }

  @Test
  @DisplayName("Should return false when comparing different RecipeDto objects")
  @Tag("standard-processing")
  void shouldReturnFalseWhenComparingDifferentRecipeDtoObjects() {
    // Given
    RecipeDto recipeDto1 = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    RecipeDto recipeDto2 = new RecipeDto(2L, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(recipeDto1).isNotEqualTo(recipeDto2);
    assertThat(recipeDto2).isNotEqualTo(recipeDto1);
  }

  @Test
  @DisplayName("Should return false when comparing with null")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithNull() {
    // Given
    RecipeDto recipeDto = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(recipeDto).isNotEqualTo(null);
  }

  @Test
  @DisplayName("Should return false when comparing with different type")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithDifferentType() {
    // Given
    RecipeDto recipeDto = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    Object other = new Object();

    // When & Then
    assertThat(recipeDto).isNotEqualTo(other);
  }

  @Test
  @DisplayName("Should return true when comparing with itself")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingWithItself() {
    // Given
    RecipeDto recipeDto = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(recipeDto).isEqualTo(recipeDto);
  }

  @Test
  @DisplayName("Should have same hashCode for equal objects")
  @Tag("standard-processing")
  void shouldHaveSameHashCodeForEqualObjects() {
    // Given
    RecipeDto recipeDto1 = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    RecipeDto recipeDto2 = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(recipeDto1.hashCode()).isEqualTo(recipeDto2.hashCode());
  }

  @Test
  @DisplayName("Should have different hashCode for different objects")
  @Tag("standard-processing")
  void shouldHaveDifferentHashCodeForDifferentObjects() {
    // Given
    RecipeDto recipeDto1 = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    RecipeDto recipeDto2 = new RecipeDto(2L, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(recipeDto1.hashCode()).isNotEqualTo(recipeDto2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values in equals")
  @Tag("error-processing")
  void shouldHandleNullValuesInEquals() {
    // Given
    RecipeDto recipeDto1 = new RecipeDto(null, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    RecipeDto recipeDto2 = new RecipeDto(null, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    RecipeDto recipeDto3 = new RecipeDto(RECIPE_ID, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(recipeDto1).isEqualTo(recipeDto2);
    assertThat(recipeDto1).isNotEqualTo(recipeDto3);
  }

  @Test
  @DisplayName("Should handle null values in hashCode")
  @Tag("error-processing")
  void shouldHandleNullValuesInHashCode() {
    // Given
    RecipeDto recipeDto = new RecipeDto(null, USER_ID, TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, CREATED_AT, UPDATED_AT,
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(recipeDto.hashCode()).isNotNull();
  }

  @Test
  @DisplayName("Builder should defensively copy mutable lists")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyMutableLists() {
    List<RecipeIngredientDto> ingredients = new ArrayList<>();
    ingredients.add(new RecipeIngredientDto());
    List<RecipeStepDto> steps = new ArrayList<>();
    steps.add(new RecipeStepDto());
    List<RecipeTagDto> tags = new ArrayList<>();
    tags.add(new RecipeTagDto());
    List<RecipeRevisionDto> revisions = new ArrayList<>();
    revisions.add(new RecipeRevisionDto());
    List<RecipeFavoriteDto> favorites = new ArrayList<>();
    favorites.add(new RecipeFavoriteDto());

    RecipeDto dto = RecipeDto.builder()
        .ingredients(ingredients)
        .steps(steps)
        .tags(tags)
        .revisions(revisions)
        .favorites(favorites)
        .build();

    ingredients.add(new RecipeIngredientDto());
    steps.add(new RecipeStepDto());
    tags.add(new RecipeTagDto());
    revisions.add(new RecipeRevisionDto());
    favorites.add(new RecipeFavoriteDto());

    assertThat(dto.getIngredients()).hasSize(1);
    assertThat(dto.getSteps()).hasSize(1);
    assertThat(dto.getTags()).hasSize(1);
    assertThat(dto.getRevisions()).hasSize(1);
    assertThat(dto.getFavorites()).hasSize(1);
  }

  @Test
  @DisplayName("Builder should handle null lists as empty")
  @Tag("error-processing")
  void builderShouldHandleNullListsAsEmpty() {
    RecipeDto dto = RecipeDto.builder()
        .ingredients(null)
        .steps(null)
        .tags(null)
        .revisions(null)
        .favorites(null)
        .build();
    assertThat(dto.getIngredients()).isEmpty();
    assertThat(dto.getSteps()).isEmpty();
    assertThat(dto.getTags()).isEmpty();
    assertThat(dto.getRevisions()).isEmpty();
    assertThat(dto.getFavorites()).isEmpty();
  }

  @Test
  @DisplayName("toString should include key fields and not be null")
  @Tag("standard-processing")
  void toStringShouldIncludeKeyFields() {
    RecipeDto dto = RecipeDto.builder()
        .recipeId(RECIPE_ID)
        .userId(USER_ID)
        .title(TITLE)
        .description(DESCRIPTION)
        .originUrl(ORIGIN_URL)
        .servings(SERVINGS)
        .preparationTime(PREPARATION_TIME)
        .cookingTime(COOKING_TIME)
        .difficulty(DIFFICULTY)
        .createdAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .build();
    String str = dto.toString();
    assertThat(str).isNotNull();
    assertThat(str).contains("RecipeDto");
    assertThat(str).contains(TITLE);
    assertThat(str).contains(DESCRIPTION);
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    RecipeDto dto = new RecipeDto(null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null);
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getTitle()).isNull();
    assertThat(dto.getDescription()).isNull();
    assertThat(dto.getOriginUrl()).isNull();
    assertThat(dto.getServings()).isNull();
    assertThat(dto.getPreparationTime()).isNull();
    assertThat(dto.getCookingTime()).isNull();
    assertThat(dto.getDifficulty()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
    assertThat(dto.getIngredients()).isEmpty();
    assertThat(dto.getSteps()).isEmpty();
    assertThat(dto.getTags()).isEmpty();
    assertThat(dto.getRevisions()).isEmpty();
    assertThat(dto.getFavorites()).isEmpty();
  }
}
