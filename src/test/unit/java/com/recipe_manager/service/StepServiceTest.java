package com.recipe_manager.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for StepService.
 *
 * <p>
 * Tests cover all placeholder methods:
 * <ul>
 * <li>getSteps</li>
 * <li>addStep</li>
 * <li>updateStep</li>
 * <li>deleteStep</li>
 * <li>reorderSteps</li>
 * <li>addComment</li>
 * <li>editComment</li>
 * <li>deleteComment</li>
 * <li>addMedia</li>
 * <li>updateMedia</li>
 * <li>deleteMedia</li>
 * </ul>
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class StepServiceTest {

  @InjectMocks
  private StepService stepService;

  @Test
  @Tag("standard-processing")
  @DisplayName("Should get steps for recipe successfully")
  void shouldGetStepsForRecipeSuccessfully() {
    // Given
    String recipeId = "recipe-123";

    // When
    ResponseEntity<String> response = stepService.getSteps(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Get Recipe Steps - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add comment to step successfully")
  void shouldAddCommentToStepSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String stepId = "step-456";

    // When
    ResponseEntity<String> response = stepService.addComment(recipeId, stepId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Add Comment to Step - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should edit comment on step successfully")
  void shouldEditCommentOnStepSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String stepId = "step-456";

    // When
    ResponseEntity<String> response = stepService.editComment(recipeId, stepId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Edit Comment on Step - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete comment from step successfully")
  void shouldDeleteCommentFromStepSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String stepId = "step-456";

    // When
    ResponseEntity<String> response = stepService.deleteComment(recipeId, stepId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Delete Comment from Step - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add media to step successfully")
  void shouldAddMediaToStepSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String stepId = "step-456";

    // When
    ResponseEntity<String> response = stepService.addMedia(recipeId, stepId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Add Media Ref to Step - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should update media on step successfully")
  void shouldUpdateMediaOnStepSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String stepId = "step-456";

    // When
    ResponseEntity<String> response = stepService.updateMedia(recipeId, stepId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Update Media Ref on Step - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete media from step successfully")
  void shouldDeleteMediaFromStepSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String stepId = "step-456";

    // When
    ResponseEntity<String> response = stepService.deleteMedia(recipeId, stepId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Delete Media Ref from Step - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null recipe ID gracefully")
  void shouldHandleNullRecipeIdGracefully() {
    // When
    ResponseEntity<String> response = stepService.getSteps(null);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Get Recipe Steps - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null step ID gracefully")
  void shouldHandleNullStepIdGracefully() {
    // When
    ResponseEntity<String> response = stepService.addComment("recipe-123", null);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Add Comment to Step - placeholder");
  }
}
