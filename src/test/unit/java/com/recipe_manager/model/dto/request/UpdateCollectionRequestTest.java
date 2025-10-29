package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

/** Unit tests for UpdateCollectionRequest. */
@Tag("unit")
class UpdateCollectionRequestTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Builder sets all fields")
  @Tag("standard-processing")
  void builderSetsAllFields() {
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder()
            .name("Updated Name")
            .description("Updated Description")
            .visibility(CollectionVisibility.FRIENDS_ONLY)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    assertThat(request.getName()).isEqualTo("Updated Name");
    assertThat(request.getDescription()).isEqualTo("Updated Description");
    assertThat(request.getVisibility()).isEqualTo(CollectionVisibility.FRIENDS_ONLY);
    assertThat(request.getCollaborationMode()).isEqualTo(CollaborationMode.SPECIFIC_USERS);
  }

  @Test
  @DisplayName("All fields are optional")
  @Tag("standard-processing")
  void allFieldsAreOptional() {
    UpdateCollectionRequest request = UpdateCollectionRequest.builder().build();

    assertThat(request.getName()).isNull();
    assertThat(request.getDescription()).isNull();
    assertThat(request.getVisibility()).isNull();
    assertThat(request.getCollaborationMode()).isNull();
  }

  @Test
  @DisplayName("No-args constructor initializes with null values")
  @Tag("standard-processing")
  void noArgsConstructorSetsDefaults() {
    // When
    UpdateCollectionRequest request = new UpdateCollectionRequest();

    // Then
    assertThat(request.getName()).isNull();
    assertThat(request.getDescription()).isNull();
    assertThat(request.getVisibility()).isNull();
    assertThat(request.getCollaborationMode()).isNull();
  }

  @Test
  @DisplayName("All-args constructor sets all fields correctly")
  @Tag("standard-processing")
  void allArgsConstructorSetsAllFields() {
    // When
    UpdateCollectionRequest request =
        new UpdateCollectionRequest(
            "Test Collection",
            "Test Description",
            CollectionVisibility.PUBLIC,
            CollaborationMode.OWNER_ONLY);

    // Then
    assertThat(request.getName()).isEqualTo("Test Collection");
    assertThat(request.getDescription()).isEqualTo("Test Description");
    assertThat(request.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(request.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
  }

  @Test
  @DisplayName("Setters work correctly")
  @Tag("standard-processing")
  void settersWorkCorrectly() {
    // Given
    UpdateCollectionRequest request = new UpdateCollectionRequest();

    // When
    request.setName("New Name");
    request.setDescription("New Description");
    request.setVisibility(CollectionVisibility.FRIENDS_ONLY);
    request.setCollaborationMode(CollaborationMode.SPECIFIC_USERS);

    // Then
    assertThat(request.getName()).isEqualTo("New Name");
    assertThat(request.getDescription()).isEqualTo("New Description");
    assertThat(request.getVisibility()).isEqualTo(CollectionVisibility.FRIENDS_ONLY);
    assertThat(request.getCollaborationMode()).isEqualTo(CollaborationMode.SPECIFIC_USERS);
  }

  @Test
  @DisplayName("Valid request with all fields passes validation")
  @Tag("standard-processing")
  void validRequestWithAllFieldsPassesValidation() {
    // Given
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder()
            .name("Valid Collection Name")
            .description("A valid description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    // When
    Set<ConstraintViolation<UpdateCollectionRequest>> violations = validator.validate(request);

    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Name with maximum length passes validation")
  @Tag("standard-processing")
  void nameWithMaxLengthPassesValidation() {
    // Given
    String maxLengthName = "a".repeat(255);
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder().name(maxLengthName).build();

    // When
    Set<ConstraintViolation<UpdateCollectionRequest>> violations = validator.validate(request);

    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Name exceeding maximum length fails validation")
  @Tag("error-handling")
  void nameTooLongFailsValidation() {
    // Given
    String tooLongName = "a".repeat(256);
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder().name(tooLongName).build();

    // When
    Set<ConstraintViolation<UpdateCollectionRequest>> violations = validator.validate(request);

    // Then
    assertThat(violations).hasSize(1);
    ConstraintViolation<UpdateCollectionRequest> violation = violations.iterator().next();
    assertThat(violation.getMessage())
        .isEqualTo("Collection name must be between 1 and 255 characters");
    assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
  }

  @Test
  @DisplayName("Empty name fails validation")
  @Tag("error-handling")
  void emptyNameFailsValidation() {
    // Given
    UpdateCollectionRequest request = UpdateCollectionRequest.builder().name("").build();

    // When
    Set<ConstraintViolation<UpdateCollectionRequest>> violations = validator.validate(request);

    // Then
    assertThat(violations).hasSize(1);
    ConstraintViolation<UpdateCollectionRequest> violation = violations.iterator().next();
    assertThat(violation.getMessage())
        .isEqualTo("Collection name must be between 1 and 255 characters");
    assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
  }

  @Test
  @DisplayName("Description with maximum length passes validation")
  @Tag("standard-processing")
  void descriptionWithMaxLengthPassesValidation() {
    // Given
    String maxLengthDescription = "a".repeat(2000);
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder().description(maxLengthDescription).build();

    // When
    Set<ConstraintViolation<UpdateCollectionRequest>> violations = validator.validate(request);

    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Description exceeding maximum length fails validation")
  @Tag("error-handling")
  void descriptionTooLongFailsValidation() {
    // Given
    String tooLongDescription = "a".repeat(2001);
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder().description(tooLongDescription).build();

    // When
    Set<ConstraintViolation<UpdateCollectionRequest>> violations = validator.validate(request);

    // Then
    assertThat(violations).hasSize(1);
    ConstraintViolation<UpdateCollectionRequest> violation = violations.iterator().next();
    assertThat(violation.getMessage())
        .isEqualTo("Collection description must not exceed 2000 characters");
    assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
  }

  @Test
  @DisplayName("Empty description passes validation")
  @Tag("standard-processing")
  void emptyDescriptionPassesValidation() {
    // Given - Empty description should be allowed
    UpdateCollectionRequest request = UpdateCollectionRequest.builder().description("").build();

    // When
    Set<ConstraintViolation<UpdateCollectionRequest>> violations = validator.validate(request);

    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("All visibility enum values are valid")
  @Tag("standard-processing")
  void allVisibilityEnumValuesAreValid() {
    // Test all enum values
    for (CollectionVisibility visibility : CollectionVisibility.values()) {
      // Given
      UpdateCollectionRequest request =
          UpdateCollectionRequest.builder().visibility(visibility).build();

      // When
      Set<ConstraintViolation<UpdateCollectionRequest>> violations = validator.validate(request);

      // Then
      assertThat(violations).isEmpty();
    }
  }

  @Test
  @DisplayName("All collaboration mode enum values are valid")
  @Tag("standard-processing")
  void allCollaborationModeEnumValuesAreValid() {
    // Test all enum values
    for (CollaborationMode mode : CollaborationMode.values()) {
      // Given
      UpdateCollectionRequest request =
          UpdateCollectionRequest.builder().collaborationMode(mode).build();

      // When
      Set<ConstraintViolation<UpdateCollectionRequest>> violations = validator.validate(request);

      // Then
      assertThat(violations).isEmpty();
    }
  }
}
