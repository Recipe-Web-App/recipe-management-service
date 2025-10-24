package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

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

/** Unit tests for SearchCollectionsRequest. */
@Tag("unit")
class SearchCollectionsRequestTest {

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
    UUID ownerId = UUID.randomUUID();
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder()
            .query("dessert")
            .visibility(List.of(CollectionVisibility.PUBLIC, CollectionVisibility.PRIVATE))
            .collaborationMode(List.of(CollaborationMode.OWNER_ONLY))
            .ownerUserId(ownerId)
            .minRecipeCount(1)
            .maxRecipeCount(100)
            .build();

    assertThat(request.getQuery()).isEqualTo("dessert");
    assertThat(request.getVisibility())
        .containsExactly(CollectionVisibility.PUBLIC, CollectionVisibility.PRIVATE);
    assertThat(request.getCollaborationMode()).containsExactly(CollaborationMode.OWNER_ONLY);
    assertThat(request.getOwnerUserId()).isEqualTo(ownerId);
    assertThat(request.getMinRecipeCount()).isEqualTo(1);
    assertThat(request.getMaxRecipeCount()).isEqualTo(100);
  }

  @Test
  @DisplayName("Builder works with minimal fields")
  @Tag("standard-processing")
  void builderWorksWithMinimalFields() {
    SearchCollectionsRequest request = SearchCollectionsRequest.builder().build();

    assertThat(request.getQuery()).isNull();
    assertThat(request.getVisibility()).isNull();
    assertThat(request.getCollaborationMode()).isNull();
    assertThat(request.getOwnerUserId()).isNull();
    assertThat(request.getMinRecipeCount()).isNull();
    assertThat(request.getMaxRecipeCount()).isNull();
  }

  @Test
  @DisplayName("Validation passes with all fields null")
  @Tag("standard-processing")
  void validationPassesWithAllFieldsNull() {
    SearchCollectionsRequest request = SearchCollectionsRequest.builder().build();

    Set<ConstraintViolation<SearchCollectionsRequest>> violations = validator.validate(request);
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Validation fails when minRecipeCount is negative")
  @Tag("standard-processing")
  void validationFailsWhenMinRecipeCountIsNegative() {
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder().minRecipeCount(-1).build();

    Set<ConstraintViolation<SearchCollectionsRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
    assertThat(violations.iterator().next().getMessage())
        .contains("Minimum recipe count must be non-negative");
  }

  @Test
  @DisplayName("Validation fails when maxRecipeCount is negative")
  @Tag("standard-processing")
  void validationFailsWhenMaxRecipeCountIsNegative() {
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder().maxRecipeCount(-1).build();

    Set<ConstraintViolation<SearchCollectionsRequest>> violations = validator.validate(request);
    assertThat(violations).isNotEmpty();
    assertThat(violations.iterator().next().getMessage())
        .contains("Maximum recipe count must be non-negative");
  }

  @Test
  @DisplayName("Validation passes with valid recipe counts")
  @Tag("standard-processing")
  void validationPassesWithValidRecipeCounts() {
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder().minRecipeCount(0).maxRecipeCount(50).build();

    Set<ConstraintViolation<SearchCollectionsRequest>> violations = validator.validate(request);
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Validation passes with query only")
  @Tag("standard-processing")
  void validationPassesWithQueryOnly() {
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder().query("chocolate").build();

    Set<ConstraintViolation<SearchCollectionsRequest>> violations = validator.validate(request);
    assertThat(violations).isEmpty();
  }
}
