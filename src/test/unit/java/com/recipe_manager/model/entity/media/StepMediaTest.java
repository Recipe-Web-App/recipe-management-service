package com.recipe_manager.model.entity.media;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class StepMediaTest {

  private Recipe recipe;
  private RecipeStep step;
  private Media media;
  private StepMediaId stepMediaId;

  @BeforeEach
  void setUp() {
    recipe = Recipe.builder()
        .recipeId(1L)
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .build();

    step = RecipeStep.builder()
        .stepId(2L)
        .stepNumber(1)
        .instruction("Test instructions")
        .build();

    media = Media.builder()
        .mediaId(3L)
        .userId(UUID.randomUUID())
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    stepMediaId = StepMediaId.builder()
        .recipeId(1L)
        .stepId(2L)
        .mediaId(3L)
        .build();
  }

  @Test
  @DisplayName("Should create StepMedia with builder pattern")
  void shouldCreateStepMediaWithBuilder() {
    // When
    StepMedia stepMedia = StepMedia.builder()
        .id(stepMediaId)
        .recipe(recipe)
        .step(step)
        .media(media)
        .build();

    // Then
    assertThat(stepMedia.getId()).isEqualTo(stepMediaId);
    assertThat(stepMedia.getRecipe()).isEqualTo(recipe);
    assertThat(stepMedia.getStep()).isEqualTo(step);
    assertThat(stepMedia.getMedia()).isEqualTo(media);
  }

  @Test
  @DisplayName("Should create StepMedia with no-args constructor")
  void shouldCreateStepMediaWithNoArgsConstructor() {
    // When
    StepMedia stepMedia = new StepMedia();

    // Then
    assertThat(stepMedia.getId()).isNull();
    assertThat(stepMedia.getRecipe()).isNull();
    assertThat(stepMedia.getStep()).isNull();
    assertThat(stepMedia.getMedia()).isNull();
  }

  @Test
  @DisplayName("Should create StepMedia with all-args constructor")
  void shouldCreateStepMediaWithAllArgsConstructor() {
    // When
    StepMedia stepMedia = new StepMedia(stepMediaId, recipe, step, media);

    // Then
    assertThat(stepMedia.getId()).isEqualTo(stepMediaId);
    assertThat(stepMedia.getRecipe()).isEqualTo(recipe);
    assertThat(stepMedia.getStep()).isEqualTo(step);
    assertThat(stepMedia.getMedia()).isEqualTo(media);
  }

  @Test
  @DisplayName("Should set and get all properties")
  void shouldSetAndGetAllProperties() {
    // Given
    StepMedia stepMedia = new StepMedia();

    // When
    stepMedia.setId(stepMediaId);
    stepMedia.setRecipe(recipe);
    stepMedia.setStep(step);
    stepMedia.setMedia(media);

    // Then
    assertThat(stepMedia.getId()).isEqualTo(stepMediaId);
    assertThat(stepMedia.getRecipe()).isEqualTo(recipe);
    assertThat(stepMedia.getStep()).isEqualTo(step);
    assertThat(stepMedia.getMedia()).isEqualTo(media);
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    StepMedia stepMedia1 = StepMedia.builder()
        .id(stepMediaId)
        .recipe(recipe)
        .step(step)
        .media(media)
        .build();

    StepMedia stepMedia2 = StepMedia.builder()
        .id(stepMediaId)
        .recipe(recipe)
        .step(step)
        .media(media)
        .build();

    StepMediaId differentId = StepMediaId.builder()
        .recipeId(2L)
        .stepId(3L)
        .mediaId(4L)
        .build();

    StepMedia stepMedia3 = StepMedia.builder()
        .id(differentId)
        .recipe(recipe)
        .step(step)
        .media(media)
        .build();

    // Then
    assertThat(stepMedia1)
        .isEqualTo(stepMedia2)
        .hasSameHashCodeAs(stepMedia2)
        .isNotEqualTo(stepMedia3);
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    // Given
    StepMedia stepMedia = StepMedia.builder()
        .id(stepMediaId)
        .recipe(recipe)
        .step(step)
        .media(media)
        .build();

    // When
    String toString = stepMedia.toString();

    // Then
    assertThat(toString)
        .contains("StepMedia")
        .contains("id=")
        .contains("recipe=")
        .contains("step=")
        .contains("media=");
  }
}
