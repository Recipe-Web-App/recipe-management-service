package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/** Unit tests for AddCollaboratorRequest. */
@Tag("unit")
class AddCollaboratorRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Builder sets userId field")
  @Tag("standard-processing")
  void builderSetsUserIdField() {
    UUID userId = UUID.randomUUID();
    AddCollaboratorRequest request = AddCollaboratorRequest.builder().userId(userId).build();

    assertThat(request.getUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("Validation fails when userId is null")
  @Tag("standard-processing")
  void validationFailsWhenUserIdIsNull() {
    AddCollaboratorRequest request = AddCollaboratorRequest.builder().userId(null).build();

    Set<ConstraintViolation<AddCollaboratorRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
  }

  @Test
  @DisplayName("Validation passes with valid userId")
  @Tag("standard-processing")
  void validationPassesWithValidUserId() {
    UUID userId = UUID.randomUUID();
    AddCollaboratorRequest request = AddCollaboratorRequest.builder().userId(userId).build();

    Set<ConstraintViolation<AddCollaboratorRequest>> violations = validator.validate(request);
    assertThat(violations).isEmpty();
  }
}
