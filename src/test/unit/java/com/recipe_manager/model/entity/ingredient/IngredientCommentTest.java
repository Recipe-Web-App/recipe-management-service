package com.recipe_manager.model.entity.ingredient;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for IngredientComment entity. */
@Tag("unit")
class IngredientCommentTest {

  private Ingredient testIngredient;
  private UUID testUserId;
  private Long testRecipeId;

  @BeforeEach
  void setUp() {
    testIngredient = Ingredient.builder()
        .ingredientId(1L)
        .name("Test Ingredient")
        .build();

    testUserId = UUID.randomUUID();
    testRecipeId = 123L;
  }

  @Test
  void shouldCreateIngredientCommentWithAllFields() {
    final IngredientComment comment = IngredientComment.builder()
        .commentId(1L)
        .ingredient(testIngredient)
        .recipeId(testRecipeId)
        .userId(testUserId)
        .commentText("Test comment")
        .isPublic(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    assertThat(comment.getCommentId()).isEqualTo(1L);
    assertThat(comment.getIngredient()).isEqualTo(testIngredient);
    assertThat(comment.getRecipeId()).isEqualTo(testRecipeId);
    assertThat(comment.getUserId()).isEqualTo(testUserId);
    assertThat(comment.getCommentText()).isEqualTo("Test comment");
    assertThat(comment.getIsPublic()).isTrue();
    assertThat(comment.getCreatedAt()).isNotNull();
    assertThat(comment.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldSetDefaultPublicValue() {
    final IngredientComment comment = IngredientComment.builder()
        .ingredient(testIngredient)
        .recipeId(testRecipeId)
        .userId(testUserId)
        .commentText("Test comment")
        .build();

    assertThat(comment.getIsPublic()).isTrue();
  }

  @Test
  void shouldCreateIngredientCommentWithNoArgsConstructor() {
    final IngredientComment comment = new IngredientComment();

    assertThat(comment).isNotNull();
    assertThat(comment.getCommentId()).isNull();
    assertThat(comment.getIngredient()).isNull();
    assertThat(comment.getRecipeId()).isNull();
    assertThat(comment.getUserId()).isNull();
    assertThat(comment.getCommentText()).isNull();
    assertThat(comment.getIsPublic()).isTrue();
    assertThat(comment.getCreatedAt()).isNull();
    assertThat(comment.getUpdatedAt()).isNull();
  }

  @Test
  void shouldCreateIngredientCommentWithAllArgsConstructor() {
    final LocalDateTime now = LocalDateTime.now();
    final IngredientComment comment = new IngredientComment(
        1L, testIngredient, testRecipeId, testUserId, "Test comment", true, now, now);

    assertThat(comment.getCommentId()).isEqualTo(1L);
    assertThat(comment.getIngredient()).isEqualTo(testIngredient);
    assertThat(comment.getRecipeId()).isEqualTo(testRecipeId);
    assertThat(comment.getUserId()).isEqualTo(testUserId);
    assertThat(comment.getCommentText()).isEqualTo("Test comment");
    assertThat(comment.getIsPublic()).isTrue();
    assertThat(comment.getCreatedAt()).isEqualTo(now);
    assertThat(comment.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void shouldSetAndGetAllFields() {
    final IngredientComment comment = new IngredientComment();
    final LocalDateTime now = LocalDateTime.now();

    comment.setCommentId(1L);
    comment.setIngredient(testIngredient);
    comment.setRecipeId(testRecipeId);
    comment.setUserId(testUserId);
    comment.setCommentText("Test comment");
    comment.setIsPublic(false);
    comment.setCreatedAt(now);
    comment.setUpdatedAt(now);

    assertThat(comment.getCommentId()).isEqualTo(1L);
    assertThat(comment.getIngredient()).isEqualTo(testIngredient);
    assertThat(comment.getRecipeId()).isEqualTo(testRecipeId);
    assertThat(comment.getUserId()).isEqualTo(testUserId);
    assertThat(comment.getCommentText()).isEqualTo("Test comment");
    assertThat(comment.getIsPublic()).isFalse();
    assertThat(comment.getCreatedAt()).isEqualTo(now);
    assertThat(comment.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void shouldImplementEqualsAndHashCode() {
    final LocalDateTime now = LocalDateTime.now();
    final IngredientComment comment1 = IngredientComment.builder()
        .commentId(1L)
        .ingredient(testIngredient)
        .recipeId(testRecipeId)
        .userId(testUserId)
        .commentText("Test comment")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    final IngredientComment comment2 = IngredientComment.builder()
        .commentId(1L)
        .ingredient(testIngredient)
        .recipeId(testRecipeId)
        .userId(testUserId)
        .commentText("Test comment")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    assertThat(comment1).isEqualTo(comment2);
    assertThat(comment1.hashCode()).isEqualTo(comment2.hashCode());
  }

  @Test
  void shouldImplementToString() {
    final IngredientComment comment = IngredientComment.builder()
        .commentId(1L)
        .ingredient(testIngredient)
        .recipeId(testRecipeId)
        .userId(testUserId)
        .commentText("Test comment")
        .isPublic(true)
        .build();

    final String toString = comment.toString();

    assertThat(toString).contains("commentId=1");
    assertThat(toString).contains("recipeId=" + testRecipeId);
    assertThat(toString).contains("userId=" + testUserId);
    assertThat(toString).contains("commentText=Test comment");
    assertThat(toString).contains("isPublic=true");
  }
}
