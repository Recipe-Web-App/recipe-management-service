package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/** Unit tests for UpdateRecipeOrderRequest. */
@Tag("unit")
class UpdateRecipeOrderRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Builder sets displayOrder field")
  @Tag("standard-processing")
  void builderSetsDisplayOrderField() {
    UpdateRecipeOrderRequest request =
        UpdateRecipeOrderRequest.builder().displayOrder(15).build();

    assertThat(request.getDisplayOrder()).isEqualTo(15);
  }

  @Test
  @DisplayName("Validation fails when displayOrder is null")
  @Tag("standard-processing")
  void validationFailsWhenDisplayOrderIsNull() {
    UpdateRecipeOrderRequest request =
        UpdateRecipeOrderRequest.builder().displayOrder(null).build();

    Set<ConstraintViolation<UpdateRecipeOrderRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
    assertThat(violations.iterator().next().getMessage()).contains("Display order is required");
  }

  @Test
  @DisplayName("Validation fails when displayOrder is zero")
  @Tag("standard-processing")
  void validationFailsWhenDisplayOrderIsZero() {
    UpdateRecipeOrderRequest request = UpdateRecipeOrderRequest.builder().displayOrder(0).build();

    Set<ConstraintViolation<UpdateRecipeOrderRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
    assertThat(violations.iterator().next().getMessage())
        .contains("Display order must be at least 1");
  }

  @Test
  @DisplayName("Validation fails when displayOrder is negative")
  @Tag("standard-processing")
  void validationFailsWhenDisplayOrderIsNegative() {
    UpdateRecipeOrderRequest request =
        UpdateRecipeOrderRequest.builder().displayOrder(-5).build();

    Set<ConstraintViolation<UpdateRecipeOrderRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
    assertThat(violations.iterator().next().getMessage())
        .contains("Display order must be at least 1");
  }

  @Test
  @DisplayName("Validation passes with valid displayOrder")
  @Tag("standard-processing")
  void validationPassesWithValidDisplayOrder() {
    UpdateRecipeOrderRequest request =
        UpdateRecipeOrderRequest.builder().displayOrder(10).build();

    Set<ConstraintViolation<UpdateRecipeOrderRequest>> violations = validator.validate(request);
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Validation passes with displayOrder of 1")
  @Tag("standard-processing")
  void validationPassesWithDisplayOrderOfOne() {
    UpdateRecipeOrderRequest request = UpdateRecipeOrderRequest.builder().displayOrder(1).build();

    Set<ConstraintViolation<UpdateRecipeOrderRequest>> violations = validator.validate(request);
    assertThat(violations).isEmpty();
  }
}
