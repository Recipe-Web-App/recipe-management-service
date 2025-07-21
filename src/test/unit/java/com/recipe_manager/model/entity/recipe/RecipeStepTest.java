package com.recipe_manager.model.entity.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeStep entity.
 */
@Tag("unit")
class RecipeStepTest {

  private Recipe recipe;
  private RecipeStep recipeStep;

  @BeforeEach
  void setUp() {
    recipe = Recipe.builder()
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .build();

    recipeStep = RecipeStep.builder()
        .recipe(recipe)
        .stepNumber(1)
        .instruction("Test instruction")
        .optional(false)
        .timerSeconds(300)
        .build();
  }

  @Test
  @DisplayName("Should create recipe step with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeStepWithBuilder() {
    // Then
    assertThat(recipeStep.getRecipe()).isEqualTo(recipe);
    assertThat(recipeStep.getStepNumber()).isEqualTo(1);
    assertThat(recipeStep.getInstruction()).isEqualTo("Test instruction");
    assertThat(recipeStep.getOptional()).isFalse();
    assertThat(recipeStep.getTimerSeconds()).isEqualTo(300);
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    Long stepId = 1L;
    Integer newStepNumber = 2;
    String newInstruction = "Updated instruction";
    Boolean newOptional = true;
    Integer newTimerSeconds = 600;
    LocalDateTime createdAt = LocalDateTime.now();

    // When
    recipeStep.setStepId(stepId);
    recipeStep.setStepNumber(newStepNumber);
    recipeStep.setInstruction(newInstruction);
    recipeStep.setOptional(newOptional);
    recipeStep.setTimerSeconds(newTimerSeconds);
    recipeStep.setCreatedAt(createdAt);

    // Then
    assertThat(recipeStep.getStepId()).isEqualTo(stepId);
    assertThat(recipeStep.getStepNumber()).isEqualTo(newStepNumber);
    assertThat(recipeStep.getInstruction()).isEqualTo(newInstruction);
    assertThat(recipeStep.getOptional()).isEqualTo(newOptional);
    assertThat(recipeStep.getTimerSeconds()).isEqualTo(newTimerSeconds);
    assertThat(recipeStep.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should have correct toString representation")
  @Tag("standard-processing")
  void shouldHaveCorrectToStringRepresentation() {
    // When
    String toString = recipeStep.toString();

    // Then
    assertThat(toString).contains("RecipeStep");
    assertThat(toString).contains("1");
    assertThat(toString).contains("Test instruction");
    assertThat(toString).contains("false");
    assertThat(toString).contains("300");
  }

  @Test
  @DisplayName("Should handle optional step")
  @Tag("standard-processing")
  void shouldHandleOptionalStep() {
    // Given
    RecipeStep optionalStep = RecipeStep.builder()
        .recipe(recipe)
        .stepNumber(1)
        .instruction("Optional step")
        .optional(true)
        .build();

    // Then
    assertThat(optionalStep.getOptional()).isTrue();
  }

  @Test
  @DisplayName("Should handle step without timer")
  @Tag("standard-processing")
  void shouldHandleStepWithoutTimer() {
    // Given
    RecipeStep noTimerStep = RecipeStep.builder()
        .recipe(recipe)
        .stepNumber(1)
        .instruction("Step without timer")
        .build();

    // Then
    assertThat(noTimerStep.getTimerSeconds()).isNull();
  }

  @Test
  @DisplayName("Should handle multiple steps with different numbers")
  @Tag("standard-processing")
  void shouldHandleMultipleStepsWithDifferentNumbers() {
    // Given
    RecipeStep step1 = RecipeStep.builder()
        .recipe(recipe)
        .stepNumber(1)
        .instruction("First step")
        .build();

    RecipeStep step2 = RecipeStep.builder()
        .recipe(recipe)
        .stepNumber(2)
        .instruction("Second step")
        .build();

    RecipeStep step3 = RecipeStep.builder()
        .recipe(recipe)
        .stepNumber(3)
        .instruction("Third step")
        .build();

    // Then
    assertThat(step1.getStepNumber()).isEqualTo(1);
    assertThat(step2.getStepNumber()).isEqualTo(2);
    assertThat(step3.getStepNumber()).isEqualTo(3);
  }

  @Test
  @DisplayName("Should handle step with long instruction")
  @Tag("standard-processing")
  void shouldHandleStepWithLongInstruction() {
    // Given
    String longInstruction = "This is a very long instruction that contains multiple sentences " +
        "and should be handled properly by the entity. It might contain special characters " +
        "like numbers (1, 2, 3) and symbols (@#$%) as well.";

    RecipeStep longStep = RecipeStep.builder()
        .recipe(recipe)
        .stepNumber(1)
        .instruction(longInstruction)
        .build();

    // Then
    assertThat(longStep.getInstruction()).isEqualTo(longInstruction);
    assertThat(longStep.getInstruction().length()).isGreaterThan(100);
  }

  @Test
  @DisplayName("Builder should defensively copy recipe")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyRecipe() {
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R").build();
    RecipeStep step = RecipeStep.builder().recipe(recipe).stepNumber(1).instruction("I").build();
    recipe.setTitle("Changed");
    assertThat(step.getRecipe().getTitle()).isNotEqualTo("Changed");
  }

  @Test
  @DisplayName("Getters and setters should defensively copy recipe")
  @Tag("standard-processing")
  void gettersAndSettersShouldDefensivelyCopyRecipe() {
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R2").build();
    recipeStep.setRecipe(recipe);
    recipe.setTitle("Changed2");
    assertThat(recipeStep.getRecipe().getTitle()).isNotEqualTo("Changed2");
  }

  @Test
  @DisplayName("All-args constructor should defensively copy recipe")
  @Tag("standard-processing")
  void allArgsConstructorShouldDefensivelyCopyRecipe() {
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R3").build();
    RecipeStep step = new RecipeStep(1L, recipe, 1, "I", false, 10, java.time.LocalDateTime.now());
    recipe.setTitle("Changed3");
    assertThat(step.getRecipe().getTitle()).isNotEqualTo("Changed3");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    RecipeStep step = new RecipeStep(null, null, null, null, null, null, null);
    assertThat(step.getStepId()).isNull();
    assertThat(step.getRecipe()).isNull();
    assertThat(step.getStepNumber()).isNull();
    assertThat(step.getInstruction()).isNull();
    assertThat(step.getOptional()).isNull();
    assertThat(step.getTimerSeconds()).isNull();
    assertThat(step.getCreatedAt()).isNull();
  }

  @Test
  @DisplayName("Equals and hashCode: self, null, different type, different values, all-null fields")
  @Tag("standard-processing")
  void equalsAndHashCodeEdgeCases() {
    RecipeStep step1 = RecipeStep.builder().stepNumber(1).build();
    RecipeStep step2 = RecipeStep.builder().stepNumber(1).build();
    RecipeStep step3 = RecipeStep.builder().stepNumber(2).build();
    assertThat(step1).isEqualTo(step1);
    assertThat(step1).isEqualTo(step2);
    assertThat(step1.hashCode()).isEqualTo(step2.hashCode());
    assertThat(step1).isNotEqualTo(step3);
    assertThat(step1.hashCode()).isNotEqualTo(step3.hashCode());
    assertThat(step1).isNotEqualTo(null);
    assertThat(step1).isNotEqualTo(new Object());
    RecipeStep stepNulls1 = new RecipeStep(null, null, null, null, null, null, null);
    RecipeStep stepNulls2 = new RecipeStep(null, null, null, null, null, null, null);
    assertThat(stepNulls1).isEqualTo(stepNulls2);
    assertThat(stepNulls1.hashCode()).isEqualTo(stepNulls2.hashCode());
  }
}
