package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.recipe_manager.model.enums.DifficultyLevel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CreateRecipeRequest class.
 */
@Tag("unit")
class CreateRecipeRequestTest {

  private static final String TITLE = "Test Recipe";
  private static final String DESCRIPTION = "A delicious test recipe";
  private static final String ORIGIN_URL = "https://example.com/recipe";
  private static final BigDecimal SERVINGS = new BigDecimal("4.0");
  private static final Integer PREPARATION_TIME = 30;
  private static final Integer COOKING_TIME = 45;
  private static final DifficultyLevel DIFFICULTY = DifficultyLevel.MEDIUM;

  @Test
  @DisplayName("Should create CreateRecipeRequest with constructor")
  @Tag("standard-processing")
  void shouldCreateCreateRecipeRequestWithConstructor() {
    // Given
    List<CreateRecipeIngredientRequest> ingredients = Arrays.asList(new CreateRecipeIngredientRequest());
    List<CreateRecipeStepRequest> steps = Arrays.asList(new CreateRecipeStepRequest());

    // When
    CreateRecipeRequest request = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, ingredients, steps);

    // Then
    assertThat(request.getTitle()).isEqualTo(TITLE);
    assertThat(request.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(request.getOriginUrl()).isEqualTo(ORIGIN_URL);
    assertThat(request.getServings()).isEqualTo(SERVINGS);
    assertThat(request.getPreparationTime()).isEqualTo(PREPARATION_TIME);
    assertThat(request.getCookingTime()).isEqualTo(COOKING_TIME);
    assertThat(request.getDifficulty()).isEqualTo(DIFFICULTY);
    assertThat(request.getIngredients()).isEqualTo(ingredients);
    assertThat(request.getSteps()).isEqualTo(steps);
  }

  @Test
  @DisplayName("Should create CreateRecipeRequest with builder")
  @Tag("standard-processing")
  void shouldCreateCreateRecipeRequestWithBuilder() {
    // When
    CreateRecipeRequest request = CreateRecipeRequest.builder()
        .title(TITLE)
        .description(DESCRIPTION)
        .originUrl(ORIGIN_URL)
        .servings(SERVINGS)
        .preparationTime(PREPARATION_TIME)
        .cookingTime(COOKING_TIME)
        .difficulty(DIFFICULTY)
        .build();

    // Then
    assertThat(request.getTitle()).isEqualTo(TITLE);
    assertThat(request.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(request.getOriginUrl()).isEqualTo(ORIGIN_URL);
    assertThat(request.getServings()).isEqualTo(SERVINGS);
    assertThat(request.getPreparationTime()).isEqualTo(PREPARATION_TIME);
    assertThat(request.getCookingTime()).isEqualTo(COOKING_TIME);
    assertThat(request.getDifficulty()).isEqualTo(DIFFICULTY);
  }

  @Test
  @DisplayName("Should set and get CreateRecipeRequest properties")
  @Tag("standard-processing")
  void shouldSetAndGetCreateRecipeRequestProperties() {
    // Given
    CreateRecipeRequest request = new CreateRecipeRequest();

    // When
    request.setTitle(TITLE);
    request.setDescription(DESCRIPTION);
    request.setOriginUrl(ORIGIN_URL);
    request.setServings(SERVINGS);
    request.setPreparationTime(PREPARATION_TIME);
    request.setCookingTime(COOKING_TIME);
    request.setDifficulty(DIFFICULTY);

    // Then
    assertThat(request.getTitle()).isEqualTo(TITLE);
    assertThat(request.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(request.getOriginUrl()).isEqualTo(ORIGIN_URL);
    assertThat(request.getServings()).isEqualTo(SERVINGS);
    assertThat(request.getPreparationTime()).isEqualTo(PREPARATION_TIME);
    assertThat(request.getCookingTime()).isEqualTo(COOKING_TIME);
    assertThat(request.getDifficulty()).isEqualTo(DIFFICULTY);
  }

  @Test
  @DisplayName("Should return unmodifiable list for ingredients")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForIngredients() {
    // Given
    List<CreateRecipeIngredientRequest> ingredients = new ArrayList<>();
    CreateRecipeRequest request = new CreateRecipeRequest();
    request.setIngredients(ingredients);

    // When & Then
    assertThat(request.getIngredients()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should return unmodifiable list for steps")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForSteps() {
    // Given
    List<CreateRecipeStepRequest> steps = new ArrayList<>();
    CreateRecipeRequest request = new CreateRecipeRequest();
    request.setSteps(steps);

    // When & Then
    assertThat(request.getSteps()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should defensively copy ingredients list")
  @Tag("standard-processing")
  void shouldDefensivelyCopyIngredientsList() {
    // Given
    List<CreateRecipeIngredientRequest> originalIngredients = new ArrayList<>();
    originalIngredients.add(new CreateRecipeIngredientRequest());
    CreateRecipeRequest request = new CreateRecipeRequest();
    request.setIngredients(originalIngredients);

    // When
    originalIngredients.add(new CreateRecipeIngredientRequest());

    // Then
    assertThat(request.getIngredients()).hasSize(1);
  }

  @Test
  @DisplayName("Should defensively copy steps list")
  @Tag("standard-processing")
  void shouldDefensivelyCopyStepsList() {
    // Given
    List<CreateRecipeStepRequest> originalSteps = new ArrayList<>();
    originalSteps.add(new CreateRecipeStepRequest());
    CreateRecipeRequest request = new CreateRecipeRequest();
    request.setSteps(originalSteps);

    // When
    originalSteps.add(new CreateRecipeStepRequest());

    // Then
    assertThat(request.getSteps()).hasSize(1);
  }

  @Test
  @DisplayName("Should handle null ingredients in constructor")
  @Tag("error-processing")
  void shouldHandleNullIngredientsInConstructor() {
    // When
    CreateRecipeRequest request = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, null, null);

    // Then
    assertThat(request.getIngredients()).isEmpty();
    assertThat(request.getSteps()).isEmpty();
  }

  @Test
  @DisplayName("Should handle null ingredients in setter")
  @Tag("error-processing")
  void shouldHandleNullIngredientsInSetter() {
    // Given
    CreateRecipeRequest request = new CreateRecipeRequest();

    // When
    request.setIngredients(null);

    // Then
    assertThat(request.getIngredients()).isEmpty();
  }

  @Test
  @DisplayName("Should handle null steps in setter")
  @Tag("error-processing")
  void shouldHandleNullStepsInSetter() {
    // Given
    CreateRecipeRequest request = new CreateRecipeRequest();

    // When
    request.setSteps(null);

    // Then
    assertThat(request.getSteps()).isEmpty();
  }

  @Test
  @DisplayName("Should return true when comparing equal CreateRecipeRequest objects")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingEqualCreateRecipeRequestObjects() {
    // Given
    CreateRecipeRequest request1 = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());
    CreateRecipeRequest request2 = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(request1).isEqualTo(request2);
    assertThat(request2).isEqualTo(request1);
  }

  @Test
  @DisplayName("Should return false when comparing different CreateRecipeRequest objects")
  @Tag("standard-processing")
  void shouldReturnFalseWhenComparingDifferentCreateRecipeRequestObjects() {
    // Given
    CreateRecipeRequest request1 = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());
    CreateRecipeRequest request2 = new CreateRecipeRequest("Different Title", DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(request1).isNotEqualTo(request2);
    assertThat(request2).isNotEqualTo(request1);
  }

  @Test
  @DisplayName("Should return false when comparing with null")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithNull() {
    // Given
    CreateRecipeRequest request = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(request).isNotEqualTo(null);
  }

  @Test
  @DisplayName("Should return false when comparing with different type")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithDifferentType() {
    // Given
    CreateRecipeRequest request = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());
    Object other = new Object();

    // When & Then
    assertThat(request).isNotEqualTo(other);
  }

  @Test
  @DisplayName("Should return true when comparing with itself")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingWithItself() {
    // Given
    CreateRecipeRequest request = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(request).isEqualTo(request);
  }

  @Test
  @DisplayName("Should have same hashCode for equal objects")
  @Tag("standard-processing")
  void shouldHaveSameHashCodeForEqualObjects() {
    // Given
    CreateRecipeRequest request1 = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());
    CreateRecipeRequest request2 = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
  }

  @Test
  @DisplayName("Should have different hashCode for different objects")
  @Tag("standard-processing")
  void shouldHaveDifferentHashCodeForDifferentObjects() {
    // Given
    CreateRecipeRequest request1 = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());
    CreateRecipeRequest request2 = new CreateRecipeRequest("Different Title", DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values in equals")
  @Tag("error-processing")
  void shouldHandleNullValuesInEquals() {
    // Given
    CreateRecipeRequest request1 = new CreateRecipeRequest(null, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());
    CreateRecipeRequest request2 = new CreateRecipeRequest(null, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());
    CreateRecipeRequest request3 = new CreateRecipeRequest(TITLE, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(request1).isEqualTo(request2);
    assertThat(request1).isNotEqualTo(request3);
  }

  @Test
  @DisplayName("Should handle null values in hashCode")
  @Tag("error-processing")
  void shouldHandleNullValuesInHashCode() {
    // Given
    CreateRecipeRequest request = new CreateRecipeRequest(null, DESCRIPTION, ORIGIN_URL,
        SERVINGS, PREPARATION_TIME, COOKING_TIME, DIFFICULTY, new ArrayList<>(), new ArrayList<>());

    // When & Then
    assertThat(request.hashCode()).isNotNull();
  }

  @Test
  @DisplayName("Builder should defensively copy mutable lists")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyMutableLists() {
    List<CreateRecipeIngredientRequest> ingredients = new ArrayList<>();
    ingredients.add(new CreateRecipeIngredientRequest());
    List<CreateRecipeStepRequest> steps = new ArrayList<>();
    steps.add(new CreateRecipeStepRequest());

    CreateRecipeRequest dto = CreateRecipeRequest.builder()
        .ingredients(ingredients)
        .steps(steps)
        .build();

    ingredients.add(new CreateRecipeIngredientRequest());
    steps.add(new CreateRecipeStepRequest());

    assertThat(dto.getIngredients()).hasSize(1);
    assertThat(dto.getSteps()).hasSize(1);
  }

  @Test
  @DisplayName("Builder should handle null lists as empty")
  @Tag("error-processing")
  void builderShouldHandleNullListsAsEmpty() {
    CreateRecipeRequest dto = CreateRecipeRequest.builder()
        .ingredients(null)
        .steps(null)
        .build();
    assertThat(dto.getIngredients()).isEmpty();
    assertThat(dto.getSteps()).isEmpty();
  }

  @Test
  @DisplayName("toString should include key fields and not be null")
  @Tag("standard-processing")
  void toStringShouldIncludeKeyFields() {
    CreateRecipeRequest dto = CreateRecipeRequest.builder()
        .title(TITLE)
        .description(DESCRIPTION)
        .originUrl(ORIGIN_URL)
        .servings(SERVINGS)
        .preparationTime(PREPARATION_TIME)
        .cookingTime(COOKING_TIME)
        .difficulty(DIFFICULTY)
        .build();
    String str = dto.toString();
    assertThat(str).isNotNull();
    assertThat(str).contains("CreateRecipeRequest");
    assertThat(str).contains(TITLE);
    assertThat(str).contains(DESCRIPTION);
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    CreateRecipeRequest dto = new CreateRecipeRequest(null, null, null, null, null, null, null, null, null);
    assertThat(dto.getTitle()).isNull();
    assertThat(dto.getDescription()).isNull();
    assertThat(dto.getOriginUrl()).isNull();
    assertThat(dto.getServings()).isNull();
    assertThat(dto.getPreparationTime()).isNull();
    assertThat(dto.getCookingTime()).isNull();
    assertThat(dto.getDifficulty()).isNull();
    assertThat(dto.getIngredients()).isEmpty();
    assertThat(dto.getSteps()).isEmpty();
  }
}
