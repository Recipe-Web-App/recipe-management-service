package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.recipe_manager.model.enums.DifficultyLevel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for UpdateRecipeRequest.
 */
@Tag("unit")
class UpdateRecipeRequestTest {

  private UpdateRecipeRequest updateRecipeRequest;

  @BeforeEach
  void setUp() {
    updateRecipeRequest = UpdateRecipeRequest.builder()
        .title("Updated Recipe")
        .description("An updated test recipe")
        .servings(BigDecimal.valueOf(6))
        .preparationTime(45)
        .cookingTime(60)
        .difficulty(DifficultyLevel.HARD)
        .originUrl("https://updated.com/recipe")
        .build();
  }

  @Test
  @DisplayName("Should create update recipe request with builder")
  @Tag("standard-processing")
  void shouldCreateUpdateRecipeRequestWithBuilder() {
    // Then
    assertThat(updateRecipeRequest.getTitle()).isEqualTo("Updated Recipe");
    assertThat(updateRecipeRequest.getDescription()).isEqualTo("An updated test recipe");
    assertThat(updateRecipeRequest.getServings()).isEqualTo(BigDecimal.valueOf(6));
    assertThat(updateRecipeRequest.getPreparationTime()).isEqualTo(45);
    assertThat(updateRecipeRequest.getCookingTime()).isEqualTo(60);
    assertThat(updateRecipeRequest.getDifficulty()).isEqualTo(DifficultyLevel.HARD);
    assertThat(updateRecipeRequest.getOriginUrl()).isEqualTo("https://updated.com/recipe");
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    String newTitle = "New Updated Recipe";
    String newDescription = "New updated description";
    BigDecimal newServings = BigDecimal.valueOf(8);
    Integer newPreparationTime = 60;
    Integer newCookingTime = 90;
    DifficultyLevel newDifficulty = DifficultyLevel.EXPERT;
    String newOriginUrl = "https://new-updated.com/recipe";

    // When
    updateRecipeRequest.setTitle(newTitle);
    updateRecipeRequest.setDescription(newDescription);
    updateRecipeRequest.setServings(newServings);
    updateRecipeRequest.setPreparationTime(newPreparationTime);
    updateRecipeRequest.setCookingTime(newCookingTime);
    updateRecipeRequest.setDifficulty(newDifficulty);
    updateRecipeRequest.setOriginUrl(newOriginUrl);

    // Then
    assertThat(updateRecipeRequest.getTitle()).isEqualTo(newTitle);
    assertThat(updateRecipeRequest.getDescription()).isEqualTo(newDescription);
    assertThat(updateRecipeRequest.getServings()).isEqualTo(newServings);
    assertThat(updateRecipeRequest.getPreparationTime()).isEqualTo(newPreparationTime);
    assertThat(updateRecipeRequest.getCookingTime()).isEqualTo(newCookingTime);
    assertThat(updateRecipeRequest.getDifficulty()).isEqualTo(newDifficulty);
    assertThat(updateRecipeRequest.getOriginUrl()).isEqualTo(newOriginUrl);
  }

  @Test
  @DisplayName("Should have correct toString representation")
  @Tag("standard-processing")
  void shouldHaveCorrectToStringRepresentation() {
    // When
    String toString = updateRecipeRequest.toString();

    // Then
    assertThat(toString).contains("UpdateRecipeRequest");
    assertThat(toString).contains("Updated Recipe");
    assertThat(toString).contains("An updated test recipe");
    assertThat(toString).contains("HARD");
  }

  @Test
  @DisplayName("Should handle partial update with only title")
  @Tag("standard-processing")
  void shouldHandlePartialUpdateWithOnlyTitle() {
    // Given
    UpdateRecipeRequest partialUpdate = UpdateRecipeRequest.builder()
        .title("Only Title Update")
        .build();

    // Then
    assertThat(partialUpdate.getTitle()).isEqualTo("Only Title Update");
    assertThat(partialUpdate.getDescription()).isNull();
    assertThat(partialUpdate.getServings()).isNull();
    assertThat(partialUpdate.getPreparationTime()).isNull();
    assertThat(partialUpdate.getCookingTime()).isNull();
    assertThat(partialUpdate.getDifficulty()).isNull();
    assertThat(partialUpdate.getOriginUrl()).isNull();
  }

  @Test
  @DisplayName("Should handle update with all difficulty levels")
  @Tag("standard-processing")
  void shouldHandleUpdateWithAllDifficultyLevels() {
    // Given
    UpdateRecipeRequest beginnerUpdate = UpdateRecipeRequest.builder()
        .title("Beginner Recipe")
        .difficulty(DifficultyLevel.BEGINNER)
        .build();

    UpdateRecipeRequest easyUpdate = UpdateRecipeRequest.builder()
        .title("Easy Recipe")
        .difficulty(DifficultyLevel.EASY)
        .build();

    UpdateRecipeRequest mediumUpdate = UpdateRecipeRequest.builder()
        .title("Medium Recipe")
        .difficulty(DifficultyLevel.MEDIUM)
        .build();

    UpdateRecipeRequest hardUpdate = UpdateRecipeRequest.builder()
        .title("Hard Recipe")
        .difficulty(DifficultyLevel.HARD)
        .build();

    UpdateRecipeRequest expertUpdate = UpdateRecipeRequest.builder()
        .title("Expert Recipe")
        .difficulty(DifficultyLevel.EXPERT)
        .build();

    // Then
    assertThat(beginnerUpdate.getDifficulty()).isEqualTo(DifficultyLevel.BEGINNER);
    assertThat(easyUpdate.getDifficulty()).isEqualTo(DifficultyLevel.EASY);
    assertThat(mediumUpdate.getDifficulty()).isEqualTo(DifficultyLevel.MEDIUM);
    assertThat(hardUpdate.getDifficulty()).isEqualTo(DifficultyLevel.HARD);
    assertThat(expertUpdate.getDifficulty()).isEqualTo(DifficultyLevel.EXPERT);
  }

  @Test
  @DisplayName("Should handle update with decimal servings")
  @Tag("standard-processing")
  void shouldHandleUpdateWithDecimalServings() {
    // Given
    UpdateRecipeRequest decimalServingsUpdate = UpdateRecipeRequest.builder()
        .title("Decimal Servings Recipe")
        .servings(BigDecimal.valueOf(2.5))
        .build();

    // Then
    assertThat(decimalServingsUpdate.getServings()).isEqualTo(BigDecimal.valueOf(2.5));
  }

  @Test
  @DisplayName("Should handle update with null values")
  @Tag("standard-processing")
  void shouldHandleUpdateWithNullValues() {
    // Given
    UpdateRecipeRequest nullUpdate = UpdateRecipeRequest.builder()
        .title("Null Update Recipe")
        .description(null)
        .servings(null)
        .preparationTime(null)
        .cookingTime(null)
        .difficulty(null)
        .originUrl(null)
        .build();

    // Then
    assertThat(nullUpdate.getTitle()).isEqualTo("Null Update Recipe");
    assertThat(nullUpdate.getDescription()).isNull();
    assertThat(nullUpdate.getServings()).isNull();
    assertThat(nullUpdate.getPreparationTime()).isNull();
    assertThat(nullUpdate.getCookingTime()).isNull();
    assertThat(nullUpdate.getDifficulty()).isNull();
    assertThat(nullUpdate.getOriginUrl()).isNull();
  }

  @Test
  @DisplayName("Should handle update with long description")
  @Tag("standard-processing")
  void shouldHandleUpdateWithLongDescription() {
    // Given
    String longDescription = "This is a very long description for an updated recipe that contains " +
        "multiple sentences and detailed information about the recipe changes. It might also contain " +
        "special characters like numbers (1, 2, 3) and symbols (@#$%) as well.";

    UpdateRecipeRequest longDescriptionUpdate = UpdateRecipeRequest.builder()
        .title("Long Description Recipe")
        .description(longDescription)
        .build();

    // Then
    assertThat(longDescriptionUpdate.getDescription()).isEqualTo(longDescription);
    assertThat(longDescriptionUpdate.getDescription().length()).isGreaterThan(100);
  }

  @Test
  @DisplayName("Builder should defensively copy mutable lists")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyMutableLists() {
    List<CreateRecipeIngredientRequest> ingredients = new ArrayList<>();
    ingredients.add(new CreateRecipeIngredientRequest());
    List<CreateRecipeStepRequest> steps = new ArrayList<>();
    steps.add(new CreateRecipeStepRequest());

    UpdateRecipeRequest dto = UpdateRecipeRequest.builder()
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
    UpdateRecipeRequest dto = UpdateRecipeRequest.builder()
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
    UpdateRecipeRequest dto = UpdateRecipeRequest.builder()
        .title("Test Title")
        .description("Test Description")
        .originUrl("Test URL")
        .servings(BigDecimal.ONE)
        .preparationTime(10)
        .cookingTime(20)
        .difficulty(DifficultyLevel.EASY)
        .build();
    String str = dto.toString();
    assertThat(str).isNotNull();
    assertThat(str).contains("UpdateRecipeRequest");
    assertThat(str).contains("Test Title");
    assertThat(str).contains("Test Description");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    UpdateRecipeRequest dto = new UpdateRecipeRequest(null, null, null, null, null, null, null, null, null);
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

  @Test
  @DisplayName("Should create UpdateRecipeRequest with all-args constructor and defensively copy lists")
  @Tag("standard-processing")
  void shouldCreateWithAllArgsConstructorAndDefensiveCopy() {
    List<CreateRecipeIngredientRequest> ingredients = new ArrayList<>();
    ingredients.add(new CreateRecipeIngredientRequest());
    List<CreateRecipeStepRequest> steps = new ArrayList<>();
    steps.add(new CreateRecipeStepRequest());
    UpdateRecipeRequest dto = new UpdateRecipeRequest(
        "Title", "Desc", "Url", BigDecimal.ONE, 1, 2, DifficultyLevel.EASY, ingredients, steps);
    ingredients.add(new CreateRecipeIngredientRequest());
    steps.add(new CreateRecipeStepRequest());
    assertThat(dto.getIngredients()).hasSize(1);
    assertThat(dto.getSteps()).hasSize(1);
  }

  @Test
  @DisplayName("All-args constructor should handle null lists as empty")
  @Tag("error-processing")
  void allArgsConstructorShouldHandleNullListsAsEmpty() {
    UpdateRecipeRequest dto = new UpdateRecipeRequest(
        "Title", "Desc", "Url", BigDecimal.ONE, 1, 2, DifficultyLevel.EASY, null, null);
    assertThat(dto.getIngredients()).isEmpty();
    assertThat(dto.getSteps()).isEmpty();
  }

  @Test
  @DisplayName("Setters should defensively copy lists")
  @Tag("standard-processing")
  void settersShouldDefensivelyCopyLists() {
    List<CreateRecipeIngredientRequest> ingredients = new ArrayList<>();
    ingredients.add(new CreateRecipeIngredientRequest());
    List<CreateRecipeStepRequest> steps = new ArrayList<>();
    steps.add(new CreateRecipeStepRequest());
    updateRecipeRequest.setIngredients(ingredients);
    updateRecipeRequest.setSteps(steps);
    ingredients.add(new CreateRecipeIngredientRequest());
    steps.add(new CreateRecipeStepRequest());
    assertThat(updateRecipeRequest.getIngredients()).hasSize(1);
    assertThat(updateRecipeRequest.getSteps()).hasSize(1);
  }

  @Test
  @DisplayName("Getters should return unmodifiable lists")
  @Tag("standard-processing")
  void gettersShouldReturnUnmodifiableLists() {
    updateRecipeRequest.setIngredients(new ArrayList<>());
    updateRecipeRequest.setSteps(new ArrayList<>());
    assertThat(updateRecipeRequest.getIngredients()).isUnmodifiable();
    assertThat(updateRecipeRequest.getSteps()).isUnmodifiable();
  }

  @Test
  @DisplayName("Setters should handle null lists as empty")
  @Tag("error-processing")
  void settersShouldHandleNullListsAsEmpty() {
    updateRecipeRequest.setIngredients(null);
    updateRecipeRequest.setSteps(null);
    assertThat(updateRecipeRequest.getIngredients()).isEmpty();
    assertThat(updateRecipeRequest.getSteps()).isEmpty();
  }

  @Test
  @DisplayName("Equals and hashCode: self, null, different type, different values, nulls")
  @Tag("standard-processing")
  void equalsAndHashCodeEdgeCases() {
    UpdateRecipeRequest dto1 = UpdateRecipeRequest.builder()
        .title("A").description("B").originUrl("C").servings(BigDecimal.ONE)
        .preparationTime(1).cookingTime(2).difficulty(DifficultyLevel.EASY)
        .ingredients(new ArrayList<>()).steps(new ArrayList<>()).build();
    UpdateRecipeRequest dto2 = UpdateRecipeRequest.builder()
        .title("A").description("B").originUrl("C").servings(BigDecimal.ONE)
        .preparationTime(1).cookingTime(2).difficulty(DifficultyLevel.EASY)
        .ingredients(new ArrayList<>()).steps(new ArrayList<>()).build();
    UpdateRecipeRequest dto3 = UpdateRecipeRequest.builder()
        .title("Different").build();
    assertThat(dto1).isEqualTo(dto1);
    assertThat(dto1).isEqualTo(dto2);
    assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    assertThat(dto1).isNotEqualTo(dto3);
    assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
    assertThat(dto1).isNotEqualTo(null);
    assertThat(dto1).isNotEqualTo(new Object());
    UpdateRecipeRequest dtoNulls1 = new UpdateRecipeRequest(null, null, null, null, null, null, null, null, null);
    UpdateRecipeRequest dtoNulls2 = new UpdateRecipeRequest(null, null, null, null, null, null, null, null, null);
    assertThat(dtoNulls1).isEqualTo(dtoNulls2);
    assertThat(dtoNulls1.hashCode()).isEqualTo(dtoNulls2.hashCode());
  }
}
