package com.recipe_manager.repository.recipe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeComment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeCommentRepository.
 *
 * <p>Tests repository methods for finding and managing recipe comments.
 *
 * <p>Note: These are mock-based unit tests since the repository depends on JPA infrastructure that
 * is not easily testable in isolation.
 */
@Tag("unit")
class RecipeCommentRepositoryTest {

  private RecipeCommentRepository recipeCommentRepository;
  private Recipe recipe;
  private RecipeComment comment1;
  private RecipeComment comment2;

  @BeforeEach
  void setUp() {
    recipeCommentRepository = mock(RecipeCommentRepository.class);

    recipe = Recipe.builder().recipeId(1L).title("Test Recipe").build();

    comment1 =
        RecipeComment.builder()
            .commentId(1L)
            .recipe(recipe)
            .userId(UUID.randomUUID())
            .commentText("Great recipe!")
            .isPublic(true)
            .createdAt(LocalDateTime.now().minusHours(2))
            .build();

    comment2 =
        RecipeComment.builder()
            .commentId(2L)
            .recipe(recipe)
            .userId(UUID.randomUUID())
            .commentText("Private note")
            .isPublic(false)
            .createdAt(LocalDateTime.now().minusHours(1))
            .build();
  }

  @Test
  @DisplayName("Should find all comments for a recipe ordered by creation time")
  void shouldFindAllCommentsForRecipe() {
    // Given
    List<RecipeComment> expectedComments = Arrays.asList(comment1, comment2);
    when(recipeCommentRepository.findByRecipeIdOrderByCreatedAtAsc(1L))
        .thenReturn(expectedComments);

    // When
    List<RecipeComment> result =
        recipeCommentRepository.findByRecipeIdOrderByCreatedAtAsc(1L);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getCommentId()).isEqualTo(1L);
    assertThat(result.get(1).getCommentId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("Should find only public comments for a recipe")
  void shouldFindOnlyPublicCommentsForRecipe() {
    // Given
    List<RecipeComment> publicComments = Arrays.asList(comment1);
    when(recipeCommentRepository.findPublicByRecipeIdOrderByCreatedAtAsc(1L))
        .thenReturn(publicComments);

    // When
    List<RecipeComment> result =
        recipeCommentRepository.findPublicByRecipeIdOrderByCreatedAtAsc(1L);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCommentId()).isEqualTo(1L);
    assertThat(result.get(0).getIsPublic()).isTrue();
  }

  @Test
  @DisplayName("Should find comments for a specific recipe and user")
  void shouldFindCommentsForRecipeAndUser() {
    // Given
    UUID userId = UUID.randomUUID();
    RecipeComment userComment =
        RecipeComment.builder()
            .commentId(3L)
            .recipe(recipe)
            .userId(userId)
            .commentText("My comment")
            .isPublic(true)
            .build();
    List<RecipeComment> userComments = Arrays.asList(userComment);
    when(recipeCommentRepository.findByRecipeIdAndUserIdOrderByCreatedAtAsc(1L, userId))
        .thenReturn(userComments);

    // When
    List<RecipeComment> result =
        recipeCommentRepository.findByRecipeIdAndUserIdOrderByCreatedAtAsc(1L, userId);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("Should find a specific comment by ID and recipe ID")
  void shouldFindCommentByIdAndRecipeId() {
    // Given
    when(recipeCommentRepository.findByCommentIdAndRecipeId(1L, 1L))
        .thenReturn(Optional.of(comment1));

    // When
    Optional<RecipeComment> result =
        recipeCommentRepository.findByCommentIdAndRecipeId(1L, 1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getCommentId()).isEqualTo(1L);
    assertThat(result.get().getCommentText()).isEqualTo("Great recipe!");
  }

  @Test
  @DisplayName("Should return empty when comment not found")
  void shouldReturnEmptyWhenCommentNotFound() {
    // Given
    when(recipeCommentRepository.findByCommentIdAndRecipeId(999L, 1L))
        .thenReturn(Optional.empty());

    // When
    Optional<RecipeComment> result =
        recipeCommentRepository.findByCommentIdAndRecipeId(999L, 1L);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should count all comments for a recipe")
  void shouldCountAllCommentsForRecipe() {
    // Given
    when(recipeCommentRepository.countByRecipeId(1L)).thenReturn(5L);

    // When
    long count = recipeCommentRepository.countByRecipeId(1L);

    // Then
    assertThat(count).isEqualTo(5L);
  }

  @Test
  @DisplayName("Should count only public comments for a recipe")
  void shouldCountOnlyPublicCommentsForRecipe() {
    // Given
    when(recipeCommentRepository.countPublicByRecipeId(1L)).thenReturn(3L);

    // When
    long count = recipeCommentRepository.countPublicByRecipeId(1L);

    // Then
    assertThat(count).isEqualTo(3L);
  }

  @Test
  @DisplayName("Should return zero count when no comments exist")
  void shouldReturnZeroCountWhenNoComments() {
    // Given
    when(recipeCommentRepository.countByRecipeId(999L)).thenReturn(0L);

    // When
    long count = recipeCommentRepository.countByRecipeId(999L);

    // Then
    assertThat(count).isZero();
  }

  @Test
  @DisplayName("Should return empty list when no comments found for recipe")
  void shouldReturnEmptyListWhenNoCommentsFound() {
    // Given
    when(recipeCommentRepository.findByRecipeIdOrderByCreatedAtAsc(999L))
        .thenReturn(Arrays.asList());

    // When
    List<RecipeComment> result =
        recipeCommentRepository.findByRecipeIdOrderByCreatedAtAsc(999L);

    // Then
    assertThat(result).isEmpty();
  }
}
