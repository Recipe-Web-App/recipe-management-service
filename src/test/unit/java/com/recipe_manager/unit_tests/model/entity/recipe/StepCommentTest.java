package com.recipe_manager.unit_tests.model.entity.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.entity.recipe.StepComment;

@Tag("unit")
class StepCommentTest {

  private StepComment stepComment;
  private RecipeStep recipeStep;
  private Recipe recipe;

  @BeforeEach
  void setUp() {
    recipe = Recipe.builder()
        .recipeId(1L)
        .title("Test Recipe")
        .build();

    recipeStep = RecipeStep.builder()
        .stepId(1L)
        .recipe(recipe)
        .stepNumber(1)
        .instruction("Test instruction")
        .build();

    stepComment = StepComment.builder()
        .commentId(1L)
        .recipeId(1L)
        .step(recipeStep)
        .userId(UUID.randomUUID())
        .commentText("Test comment")
        .isPublic(true)
        .build();
  }

  @Test
  void testBuilder() {
    UUID userId = UUID.randomUUID();
    StepComment comment = StepComment.builder()
        .commentId(2L)
        .recipeId(2L)
        .step(recipeStep)
        .userId(userId)
        .commentText("Another test comment")
        .isPublic(false)
        .build();

    assertThat(comment.getCommentId()).isEqualTo(2L);
    assertThat(comment.getRecipeId()).isEqualTo(2L);
    assertThat(comment.getStep()).isEqualTo(recipeStep);
    assertThat(comment.getUserId()).isEqualTo(userId);
    assertThat(comment.getCommentText()).isEqualTo("Another test comment");
    assertThat(comment.getIsPublic()).isFalse();
  }

  @Test
  void testDefaultIsPublicValue() {
    StepComment comment = StepComment.builder()
        .commentId(1L)
        .recipeId(1L)
        .step(recipeStep)
        .userId(UUID.randomUUID())
        .commentText("Test comment")
        .build();

    assertThat(comment.getIsPublic()).isTrue();
  }

  @Test
  void testGettersAndSetters() {
    UUID newUserId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    stepComment.setCommentId(5L);
    stepComment.setRecipeId(5L);
    stepComment.setUserId(newUserId);
    stepComment.setCommentText("Updated comment");
    stepComment.setIsPublic(false);
    stepComment.setCreatedAt(now);
    stepComment.setUpdatedAt(now);

    assertThat(stepComment.getCommentId()).isEqualTo(5L);
    assertThat(stepComment.getRecipeId()).isEqualTo(5L);
    assertThat(stepComment.getUserId()).isEqualTo(newUserId);
    assertThat(stepComment.getCommentText()).isEqualTo("Updated comment");
    assertThat(stepComment.getIsPublic()).isFalse();
    assertThat(stepComment.getCreatedAt()).isEqualTo(now);
    assertThat(stepComment.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void testOnCreate() {
    StepComment comment = new StepComment();
    LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);

    comment.onCreate();

    LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);

    assertThat(comment.getCreatedAt()).isNotNull();
    assertThat(comment.getUpdatedAt()).isNotNull();
    assertThat(comment.getCreatedAt()).isAfter(beforeCreate);
    assertThat(comment.getCreatedAt()).isBefore(afterCreate);
    assertThat(comment.getUpdatedAt()).isEqualTo(comment.getCreatedAt());
  }

  @Test
  void testOnUpdate() {
    StepComment comment = new StepComment();
    comment.onCreate();
    LocalDateTime originalCreatedAt = comment.getCreatedAt();

    // Simulate a small delay
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);
    comment.onUpdate();
    LocalDateTime afterUpdate = LocalDateTime.now().plusSeconds(1);

    assertThat(comment.getCreatedAt()).isEqualTo(originalCreatedAt);
    assertThat(comment.getUpdatedAt()).isNotNull();
    assertThat(comment.getUpdatedAt()).isAfter(beforeUpdate);
    assertThat(comment.getUpdatedAt()).isBefore(afterUpdate);
    assertThat(comment.getUpdatedAt()).isAfter(comment.getCreatedAt());
  }

  @Test
  void testEqualsAndHashCode() {
    UUID userId = UUID.randomUUID();

    StepComment comment1 = StepComment.builder()
        .commentId(1L)
        .recipeId(1L)
        .step(recipeStep)
        .userId(userId)
        .commentText("Test comment")
        .isPublic(true)
        .build();

    StepComment comment2 = StepComment.builder()
        .commentId(1L)
        .recipeId(1L)
        .step(recipeStep)
        .userId(userId)
        .commentText("Test comment")
        .isPublic(true)
        .build();

    assertThat(comment1).isEqualTo(comment2);
    assertThat(comment1.hashCode()).isEqualTo(comment2.hashCode());
  }

  @Test
  void testToString() {
    String toStringResult = stepComment.toString();

    assertThat(toStringResult).contains("StepComment");
    assertThat(toStringResult).contains("commentId=" + stepComment.getCommentId());
    assertThat(toStringResult).contains("recipeId=" + stepComment.getRecipeId());
    assertThat(toStringResult).contains("commentText=" + stepComment.getCommentText());
  }

  @Test
  void testNoArgsConstructor() {
    StepComment comment = new StepComment();

    assertThat(comment.getCommentId()).isNull();
    assertThat(comment.getRecipeId()).isNull();
    assertThat(comment.getStep()).isNull();
    assertThat(comment.getUserId()).isNull();
    assertThat(comment.getCommentText()).isNull();
    assertThat(comment.getIsPublic()).isTrue(); // @Builder.Default sets this to true
    assertThat(comment.getCreatedAt()).isNull();
    assertThat(comment.getUpdatedAt()).isNull();
  }

  @Test
  void testAllArgsConstructor() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    StepComment comment = new StepComment(
        1L, 1L, recipeStep, userId, "Test comment", true, now, now
    );

    assertThat(comment.getCommentId()).isEqualTo(1L);
    assertThat(comment.getRecipeId()).isEqualTo(1L);
    assertThat(comment.getStep()).isEqualTo(recipeStep);
    assertThat(comment.getUserId()).isEqualTo(userId);
    assertThat(comment.getCommentText()).isEqualTo("Test comment");
    assertThat(comment.getIsPublic()).isTrue();
    assertThat(comment.getCreatedAt()).isEqualTo(now);
    assertThat(comment.getUpdatedAt()).isEqualTo(now);
  }
}
