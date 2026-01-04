package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

  @Test
  @DisplayName("Builder sets tags field")
  @Tag("standard-processing")
  void builderSetsTagsField() {
    List<String> tags = Arrays.asList("breakfast", "quick", "healthy");
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("My Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .tags(tags)
            .build();

    assertThat(request.getTags()).isEqualTo(tags);
    assertThat(request.getTags()).containsExactly("breakfast", "quick", "healthy");
  }

  @Test
  @DisplayName("Tags default to empty list when not set")
  @Tag("standard-processing")
  void tagsDefaultToEmptyListWhenNotSet() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("My Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    assertThat(request.getTags()).isNotNull();
    assertThat(request.getTags()).isEmpty();
  }

  @Test
  @DisplayName("Tags can be set to empty list explicitly")
  @Tag("standard-processing")
  void tagsCanBeSetToEmptyListExplicitly() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("My Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .tags(Collections.emptyList())
            .build();

    assertThat(request.getTags()).isNotNull();
    assertThat(request.getTags()).isEmpty();
  }

  @Test
  @DisplayName("RecipeIds default to empty list when not set")
  @Tag("standard-processing")
  void recipeIdsDefaultToEmptyListWhenNotSet() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("My Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    assertThat(request.getRecipeIds()).isNotNull();
    assertThat(request.getRecipeIds()).isEmpty();
  }

  @Test
  @DisplayName("CollaboratorIds default to empty list when not set")
  @Tag("standard-processing")
  void collaboratorIdsDefaultToEmptyListWhenNotSet() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("My Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    assertThat(request.getCollaboratorIds()).isNotNull();
    assertThat(request.getCollaboratorIds()).isEmpty();
  }

  @Test
  @DisplayName("Builder sets all batch fields together")
  @Tag("standard-processing")
  void builderSetsAllBatchFieldsTogether() {
    List<String> tags = Arrays.asList("tag1", "tag2");
    List<Long> recipeIds = Arrays.asList(1L, 2L, 3L);
    List<UUID> collaboratorIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Full Collection")
            .description("Has everything")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .tags(tags)
            .recipeIds(recipeIds)
            .collaboratorIds(collaboratorIds)
            .build();

    assertThat(request.getName()).isEqualTo("Full Collection");
    assertThat(request.getTags()).hasSize(2);
    assertThat(request.getRecipeIds()).hasSize(3);
    assertThat(request.getCollaboratorIds()).hasSize(2);
  }
}
