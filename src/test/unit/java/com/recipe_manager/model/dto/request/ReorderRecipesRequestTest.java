package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/** Unit tests for ReorderRecipesRequest. */
@Tag("unit")
class ReorderRecipesRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Builder sets recipe orders")
  @Tag("standard-processing")
  void builderSetsRecipeOrders() {
    ReorderRecipesRequest.RecipeOrder order1 =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(1L).displayOrder(10).build();
    ReorderRecipesRequest.RecipeOrder order2 =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(2L).displayOrder(20).build();

    ReorderRecipesRequest request =
        ReorderRecipesRequest.builder().recipes(List.of(order1, order2)).build();

    assertThat(request.getRecipes()).hasSize(2);
    assertThat(request.getRecipes().get(0).getRecipeId()).isEqualTo(1L);
    assertThat(request.getRecipes().get(0).getDisplayOrder()).isEqualTo(10);
  }

  @Test
  @DisplayName("Validation fails when recipes list is empty")
  @Tag("standard-processing")
  void validationFailsWhenRecipesListIsEmpty() {
    ReorderRecipesRequest request = ReorderRecipesRequest.builder().recipes(List.of()).build();

    Set<ConstraintViolation<ReorderRecipesRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
  }

  @Test
  @DisplayName("Validation fails when recipes list is null")
  @Tag("standard-processing")
  void validationFailsWhenRecipesListIsNull() {
    ReorderRecipesRequest request = ReorderRecipesRequest.builder().recipes(null).build();

    Set<ConstraintViolation<ReorderRecipesRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
  }

  @Test
  @DisplayName("Validation fails when RecipeOrder has null recipeId")
  @Tag("standard-processing")
  void validationFailsWhenRecipeOrderHasNullRecipeId() {
    ReorderRecipesRequest.RecipeOrder order =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(null).displayOrder(10).build();

    ReorderRecipesRequest request = ReorderRecipesRequest.builder().recipes(List.of(order)).build();

    Set<ConstraintViolation<ReorderRecipesRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
  }

  @Test
  @DisplayName("Validation fails when RecipeOrder has null displayOrder")
  @Tag("standard-processing")
  void validationFailsWhenRecipeOrderHasNullDisplayOrder() {
    ReorderRecipesRequest.RecipeOrder order =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(1L).displayOrder(null).build();

    ReorderRecipesRequest request = ReorderRecipesRequest.builder().recipes(List.of(order)).build();

    Set<ConstraintViolation<ReorderRecipesRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
  }

  @Test
  @DisplayName("Validation fails when RecipeOrder has displayOrder less than 1")
  @Tag("standard-processing")
  void validationFailsWhenRecipeOrderHasDisplayOrderLessThanOne() {
    ReorderRecipesRequest.RecipeOrder order =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(1L).displayOrder(0).build();

    ReorderRecipesRequest request = ReorderRecipesRequest.builder().recipes(List.of(order)).build();

    Set<ConstraintViolation<ReorderRecipesRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
  }

  @Test
  @DisplayName("Validation passes with valid data")
  @Tag("standard-processing")
  void validationPassesWithValidData() {
    ReorderRecipesRequest.RecipeOrder order1 =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(1L).displayOrder(10).build();
    ReorderRecipesRequest.RecipeOrder order2 =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(2L).displayOrder(20).build();

    ReorderRecipesRequest request =
        ReorderRecipesRequest.builder().recipes(List.of(order1, order2)).build();

    Set<ConstraintViolation<ReorderRecipesRequest>> violations = validator.validate(request);
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("RecipeOrder builder sets fields")
  @Tag("standard-processing")
  void recipeOrderBuilderSetsFields() {
    ReorderRecipesRequest.RecipeOrder order =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(5L).displayOrder(50).build();

    assertThat(order.getRecipeId()).isEqualTo(5L);
    assertThat(order.getDisplayOrder()).isEqualTo(50);
  }
}
