package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/** Unit tests for CreateCollectionRequest. */
@Tag("unit")
class CreateCollectionRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Builder sets all fields")
  @Tag("standard-processing")
  void builderSetsAllFields() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("My Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    assertThat(request.getName()).isEqualTo("My Collection");
    assertThat(request.getDescription()).isEqualTo("Test Description");
    assertThat(request.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(request.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
  }

  @Test
  @DisplayName("Validation fails when name is blank")
  @Tag("standard-processing")
  void validationFailsWhenNameIsBlank() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Set<ConstraintViolation<CreateCollectionRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
  }

  @Test
  @DisplayName("Validation fails when visibility is null")
  @Tag("standard-processing")
  void validationFailsWhenVisibilityIsNull() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Test")
            .visibility(null)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Set<ConstraintViolation<CreateCollectionRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
  }

  @Test
  @DisplayName("Validation fails when collaborationMode is null")
  @Tag("standard-processing")
  void validationFailsWhenCollaborationModeIsNull() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Test")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(null)
            .build();

    Set<ConstraintViolation<CreateCollectionRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
  }

  @Test
  @DisplayName("Validation passes with valid data")
  @Tag("standard-processing")
  void validationPassesWithValidData() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Test Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .build();

    Set<ConstraintViolation<CreateCollectionRequest>> violations = validator.validate(request);
    assertThat(violations).isEmpty();
  }
}
