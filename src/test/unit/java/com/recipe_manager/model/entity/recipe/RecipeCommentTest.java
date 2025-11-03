package com.recipe_manager.model.entity.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class RecipeCommentTest {

  private RecipeComment recipeComment;
  private Recipe recipe;

  @BeforeEach
  void setUp() {
    recipe = Recipe.builder().recipeId(1L).title("Test Recipe").build();

    recipeComment =
        RecipeComment.builder()
            .commentId(1L)
            .recipe(recipe)
            .userId(UUID.randomUUID())
            .commentText("Test comment")
            .isPublic(true)
            .build();
  }

  @Test
  void testBuilder() {
    UUID userId = UUID.randomUUID();
    RecipeComment comment =
        RecipeComment.builder()
            .commentId(2L)
            .recipe(recipe)
            .userId(userId)
            .commentText("Another test comment")
            .isPublic(false)
            .build();

    assertThat(comment.getCommentId()).isEqualTo(2L);
    assertThat(comment.getRecipe()).isEqualTo(recipe);
    assertThat(comment.getUserId()).isEqualTo(userId);
    assertThat(comment.getCommentText()).isEqualTo("Another test comment");
    assertThat(comment.getIsPublic()).isFalse();
  }

  @Test
  void testDefaultIsPublicValue() {
    RecipeComment comment =
        RecipeComment.builder()
            .commentId(1L)
            .recipe(recipe)
            .userId(UUID.randomUUID())
            .commentText("Test comment")
            .build();

    assertThat(comment.getIsPublic()).isTrue();
  }

  @Test
  void testGettersAndSetters() {
    UUID newUserId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    Recipe newRecipe = Recipe.builder().recipeId(2L).title("New Recipe").build();

    recipeComment.setCommentId(5L);
    recipeComment.setRecipe(newRecipe);
    recipeComment.setUserId(newUserId);
    recipeComment.setCommentText("Updated comment");
    recipeComment.setIsPublic(false);
    recipeComment.setCreatedAt(now);
    recipeComment.setUpdatedAt(now);

    assertThat(recipeComment.getCommentId()).isEqualTo(5L);
    assertThat(recipeComment.getRecipe()).isEqualTo(newRecipe);
    assertThat(recipeComment.getUserId()).isEqualTo(newUserId);
    assertThat(recipeComment.getCommentText()).isEqualTo("Updated comment");
    assertThat(recipeComment.getIsPublic()).isFalse();
    assertThat(recipeComment.getCreatedAt()).isEqualTo(now);
    assertThat(recipeComment.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void testOnCreate() {
    RecipeComment comment = new RecipeComment();
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
    RecipeComment comment = new RecipeComment();
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

    RecipeComment comment1 =
        RecipeComment.builder()
            .commentId(1L)
            .recipe(recipe)
            .userId(userId)
            .commentText("Test comment")
            .isPublic(true)
            .build();

    RecipeComment comment2 =
        RecipeComment.builder()
            .commentId(1L)
            .recipe(recipe)
            .userId(userId)
            .commentText("Test comment")
            .isPublic(true)
            .build();

    assertThat(comment1).isEqualTo(comment2);
    assertThat(comment1.hashCode()).isEqualTo(comment2.hashCode());
  }

  @Test
  void testToString() {
    String toStringResult = recipeComment.toString();

    assertThat(toStringResult).contains("RecipeComment");
    assertThat(toStringResult).contains("commentId=" + recipeComment.getCommentId());
    assertThat(toStringResult).contains("commentText=" + recipeComment.getCommentText());
  }

  @Test
  void testNoArgsConstructor() {
    RecipeComment comment = new RecipeComment();

    assertThat(comment.getCommentId()).isNull();
    assertThat(comment.getRecipe()).isNull();
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

    RecipeComment comment = new RecipeComment(1L, recipe, userId, "Test comment", true, now, now);

    assertThat(comment.getCommentId()).isEqualTo(1L);
    assertThat(comment.getRecipe()).isEqualTo(recipe);
    assertThat(comment.getUserId()).isEqualTo(userId);
    assertThat(comment.getCommentText()).isEqualTo("Test comment");
    assertThat(comment.getIsPublic()).isTrue();
    assertThat(comment.getCreatedAt()).isEqualTo(now);
    assertThat(comment.getUpdatedAt()).isEqualTo(now);
  }
}
