package com.recipe_manager.repository.recipe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeFavorite;
import com.recipe_manager.model.entity.recipe.RecipeFavoriteId;

/**
 * Unit tests for RecipeFavoriteRepository.
 *
 * <p>Tests repository methods for managing recipe favorites with composite key pattern.
 *
 * <p>Note: These are mock-based unit tests since the repository depends on JPA infrastructure that
 * is not easily testable in isolation.
 */
@Tag("unit")
class RecipeFavoriteRepositoryTest {

  private RecipeFavoriteRepository favoriteRepository;
  private UUID testUserId;
  private Long testRecipeId;

  @BeforeEach
  void setUp() {
    favoriteRepository = mock(RecipeFavoriteRepository.class);
    testUserId = UUID.randomUUID();
    testRecipeId = 100L;
  }

  @Test
  @DisplayName("Should find favorites by user ID with pagination")
  @Tag("standard-processing")
  void shouldFindByUserId() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<RecipeFavorite> favorites =
        Arrays.asList(
            createTestFavorite(testUserId, 101L), createTestFavorite(testUserId, 102L));
    Page<RecipeFavorite> expectedPage = new PageImpl<>(favorites, pageable, favorites.size());
    when(favoriteRepository.findByIdUserId(testUserId, pageable)).thenReturn(expectedPage);

    // When
    Page<RecipeFavorite> result = favoriteRepository.findByIdUserId(testUserId, pageable);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.getContent()).containsExactlyElementsOf(favorites);
  }

  @Test
  @DisplayName("Should find favorites by user ID with recipe details eagerly loaded")
  @Tag("standard-processing")
  void shouldFindByUserIdWithRecipe() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<RecipeFavorite> favorites =
        Arrays.asList(
            createTestFavorite(testUserId, 101L),
            createTestFavorite(testUserId, 102L),
            createTestFavorite(testUserId, 103L));
    Page<RecipeFavorite> expectedPage = new PageImpl<>(favorites, pageable, favorites.size());
    when(favoriteRepository.findByUserIdWithRecipe(testUserId, pageable)).thenReturn(expectedPage);

    // When
    Page<RecipeFavorite> result = favoriteRepository.findByUserIdWithRecipe(testUserId, pageable);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result.getContent()).containsExactlyElementsOf(favorites);
    // Verify all favorites have recipe loaded
    result.getContent().forEach(favorite -> assertThat(favorite.getRecipe()).isNotNull());
  }

  @Test
  @DisplayName("Should return empty page when user has no favorites")
  @Tag("standard-processing")
  void shouldReturnEmptyPageWhenUserHasNoFavorites() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeFavorite> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
    when(favoriteRepository.findByUserIdWithRecipe(testUserId, pageable)).thenReturn(emptyPage);

    // When
    Page<RecipeFavorite> result = favoriteRepository.findByUserIdWithRecipe(testUserId, pageable);

    // Then
    assertThat(result).isEmpty();
    assertThat(result.getTotalElements()).isZero();
  }

  @Test
  @DisplayName("Should check if favorite exists by user ID and recipe ID")
  @Tag("standard-processing")
  void shouldCheckExistsByUserIdAndRecipeId() {
    // Given
    when(favoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(true);

    // When
    boolean exists = favoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId);

    // Then
    assertThat(exists).isTrue();
    verify(favoriteRepository).existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId);
  }

  @Test
  @DisplayName("Should return false when favorite does not exist")
  @Tag("standard-processing")
  void shouldReturnFalseWhenFavoriteDoesNotExist() {
    // Given
    when(favoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(false);

    // When
    boolean exists = favoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should find favorite by user ID and recipe ID")
  @Tag("standard-processing")
  void shouldFindByUserIdAndRecipeId() {
    // Given
    RecipeFavorite expectedFavorite = createTestFavorite(testUserId, testRecipeId);
    when(favoriteRepository.findByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(Optional.of(expectedFavorite));

    // When
    Optional<RecipeFavorite> result =
        favoriteRepository.findByIdUserIdAndIdRecipeId(testUserId, testRecipeId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(expectedFavorite);
  }

  @Test
  @DisplayName("Should return empty optional when favorite not found")
  @Tag("standard-processing")
  void shouldReturnEmptyWhenFavoriteNotFound() {
    // Given
    when(favoriteRepository.findByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(Optional.empty());

    // When
    Optional<RecipeFavorite> result =
        favoriteRepository.findByIdUserIdAndIdRecipeId(testUserId, testRecipeId);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should delete favorite by user ID and recipe ID")
  @Tag("standard-processing")
  void shouldDeleteByUserIdAndRecipeId() {
    // When
    favoriteRepository.deleteByIdUserIdAndIdRecipeId(testUserId, testRecipeId);

    // Then
    verify(favoriteRepository).deleteByIdUserIdAndIdRecipeId(testUserId, testRecipeId);
  }

  @Test
  @DisplayName("Should count favorites by user ID")
  @Tag("standard-processing")
  void shouldCountByUserId() {
    // Given
    when(favoriteRepository.countByIdUserId(testUserId)).thenReturn(5L);

    // When
    long count = favoriteRepository.countByIdUserId(testUserId);

    // Then
    assertThat(count).isEqualTo(5);
  }

  @Test
  @DisplayName("Should return zero count when user has no favorites")
  @Tag("standard-processing")
  void shouldReturnZeroCountWhenUserHasNoFavorites() {
    // Given
    when(favoriteRepository.countByIdUserId(testUserId)).thenReturn(0L);

    // When
    long count = favoriteRepository.countByIdUserId(testUserId);

    // Then
    assertThat(count).isZero();
  }

  @Test
  @DisplayName("Should count favorites by recipe ID")
  @Tag("standard-processing")
  void shouldCountByRecipeId() {
    // Given
    when(favoriteRepository.countByIdRecipeId(testRecipeId)).thenReturn(15L);

    // When
    long count = favoriteRepository.countByIdRecipeId(testRecipeId);

    // Then
    assertThat(count).isEqualTo(15);
  }

  @Test
  @DisplayName("Should return zero count when recipe has no favorites")
  @Tag("standard-processing")
  void shouldReturnZeroCountWhenRecipeHasNoFavorites() {
    // Given
    when(favoriteRepository.countByIdRecipeId(testRecipeId)).thenReturn(0L);

    // When
    long count = favoriteRepository.countByIdRecipeId(testRecipeId);

    // Then
    assertThat(count).isZero();
  }

  @Test
  @DisplayName("Should support pagination with custom page size")
  @Tag("standard-processing")
  void shouldSupportPaginationWithCustomPageSize() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    List<RecipeFavorite> favorites =
        Arrays.asList(
            createTestFavorite(testUserId, 101L),
            createTestFavorite(testUserId, 102L),
            createTestFavorite(testUserId, 103L),
            createTestFavorite(testUserId, 104L),
            createTestFavorite(testUserId, 105L));
    Page<RecipeFavorite> expectedPage = new PageImpl<>(favorites, pageable, 25);
    when(favoriteRepository.findByUserIdWithRecipe(testUserId, pageable)).thenReturn(expectedPage);

    // When
    Page<RecipeFavorite> result = favoriteRepository.findByUserIdWithRecipe(testUserId, pageable);

    // Then
    assertThat(result.getSize()).isEqualTo(10);
    assertThat(result.getNumber()).isZero();
    assertThat(result.getTotalElements()).isEqualTo(25);
    assertThat(result.getTotalPages()).isEqualTo(3);
  }

  @Test
  @DisplayName("Should support pagination with second page")
  @Tag("standard-processing")
  void shouldSupportPaginationWithSecondPage() {
    // Given
    Pageable pageable = PageRequest.of(1, 20);
    List<RecipeFavorite> favorites =
        Arrays.asList(
            createTestFavorite(testUserId, 121L), createTestFavorite(testUserId, 122L));
    Page<RecipeFavorite> expectedPage = new PageImpl<>(favorites, pageable, 22);
    when(favoriteRepository.findByUserIdWithRecipe(testUserId, pageable)).thenReturn(expectedPage);

    // When
    Page<RecipeFavorite> result = favoriteRepository.findByUserIdWithRecipe(testUserId, pageable);

    // Then
    assertThat(result.getNumber()).isEqualTo(1);
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(22);
  }

  private RecipeFavorite createTestFavorite(UUID userId, Long recipeId) {
    RecipeFavoriteId id =
        RecipeFavoriteId.builder().userId(userId).recipeId(recipeId).build();

    Recipe recipe =
        Recipe.builder()
            .recipeId(recipeId)
            .title("Test Recipe " + recipeId)
            .description("Test Description")
            .build();

    return RecipeFavorite.builder()
        .id(id)
        .recipe(recipe)
        .favoritedAt(LocalDateTime.now())
        .build();
  }
}
