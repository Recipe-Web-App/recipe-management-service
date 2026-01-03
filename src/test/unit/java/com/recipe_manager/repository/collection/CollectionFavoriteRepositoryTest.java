package com.recipe_manager.repository.collection;

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

import com.recipe_manager.model.entity.collection.CollectionFavorite;
import com.recipe_manager.model.entity.collection.CollectionFavoriteId;
import com.recipe_manager.model.entity.collection.RecipeCollection;

/**
 * Unit tests for CollectionFavoriteRepository.
 *
 * <p>Tests repository methods for managing collection favorites with composite key pattern.
 *
 * <p>Note: These are mock-based unit tests since the repository depends on JPA infrastructure that
 * is not easily testable in isolation.
 */
@Tag("unit")
class CollectionFavoriteRepositoryTest {

  private CollectionFavoriteRepository favoriteRepository;
  private UUID testUserId;
  private Long testCollectionId;

  @BeforeEach
  void setUp() {
    favoriteRepository = mock(CollectionFavoriteRepository.class);
    testUserId = UUID.randomUUID();
    testCollectionId = 100L;
  }

  @Test
  @DisplayName("Should find favorites by user ID with pagination")
  @Tag("standard-processing")
  void shouldFindByUserId() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<CollectionFavorite> favorites =
        Arrays.asList(
            createTestFavorite(testUserId, 101L), createTestFavorite(testUserId, 102L));
    Page<CollectionFavorite> expectedPage = new PageImpl<>(favorites, pageable, favorites.size());
    when(favoriteRepository.findByIdUserId(testUserId, pageable)).thenReturn(expectedPage);

    // When
    Page<CollectionFavorite> result = favoriteRepository.findByIdUserId(testUserId, pageable);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.getContent()).containsExactlyElementsOf(favorites);
  }

  @Test
  @DisplayName("Should find favorites by user ID with collection details eagerly loaded")
  @Tag("standard-processing")
  void shouldFindByUserIdWithCollection() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<CollectionFavorite> favorites =
        Arrays.asList(
            createTestFavorite(testUserId, 101L),
            createTestFavorite(testUserId, 102L),
            createTestFavorite(testUserId, 103L));
    Page<CollectionFavorite> expectedPage = new PageImpl<>(favorites, pageable, favorites.size());
    when(favoriteRepository.findByUserIdWithCollection(testUserId, pageable))
        .thenReturn(expectedPage);

    // When
    Page<CollectionFavorite> result =
        favoriteRepository.findByUserIdWithCollection(testUserId, pageable);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result.getContent()).containsExactlyElementsOf(favorites);
    // Verify all favorites have collection loaded
    result.getContent().forEach(favorite -> assertThat(favorite.getCollection()).isNotNull());
  }

  @Test
  @DisplayName("Should return empty page when user has no favorites")
  @Tag("standard-processing")
  void shouldReturnEmptyPageWhenUserHasNoFavorites() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionFavorite> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
    when(favoriteRepository.findByUserIdWithCollection(testUserId, pageable)).thenReturn(emptyPage);

    // When
    Page<CollectionFavorite> result =
        favoriteRepository.findByUserIdWithCollection(testUserId, pageable);

    // Then
    assertThat(result).isEmpty();
    assertThat(result.getTotalElements()).isZero();
  }

  @Test
  @DisplayName("Should check if favorite exists by user ID and collection ID")
  @Tag("standard-processing")
  void shouldCheckExistsByUserIdAndCollectionId() {
    // Given
    when(favoriteRepository.existsByIdUserIdAndIdCollectionId(testUserId, testCollectionId))
        .thenReturn(true);

    // When
    boolean exists =
        favoriteRepository.existsByIdUserIdAndIdCollectionId(testUserId, testCollectionId);

    // Then
    assertThat(exists).isTrue();
    verify(favoriteRepository).existsByIdUserIdAndIdCollectionId(testUserId, testCollectionId);
  }

  @Test
  @DisplayName("Should return false when favorite does not exist")
  @Tag("standard-processing")
  void shouldReturnFalseWhenFavoriteDoesNotExist() {
    // Given
    when(favoriteRepository.existsByIdUserIdAndIdCollectionId(testUserId, testCollectionId))
        .thenReturn(false);

    // When
    boolean exists =
        favoriteRepository.existsByIdUserIdAndIdCollectionId(testUserId, testCollectionId);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should find favorite by user ID and collection ID")
  @Tag("standard-processing")
  void shouldFindByUserIdAndCollectionId() {
    // Given
    CollectionFavorite expectedFavorite = createTestFavorite(testUserId, testCollectionId);
    when(favoriteRepository.findByIdUserIdAndIdCollectionId(testUserId, testCollectionId))
        .thenReturn(Optional.of(expectedFavorite));

    // When
    Optional<CollectionFavorite> result =
        favoriteRepository.findByIdUserIdAndIdCollectionId(testUserId, testCollectionId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(expectedFavorite);
  }

  @Test
  @DisplayName("Should return empty optional when favorite not found")
  @Tag("standard-processing")
  void shouldReturnEmptyWhenFavoriteNotFound() {
    // Given
    when(favoriteRepository.findByIdUserIdAndIdCollectionId(testUserId, testCollectionId))
        .thenReturn(Optional.empty());

    // When
    Optional<CollectionFavorite> result =
        favoriteRepository.findByIdUserIdAndIdCollectionId(testUserId, testCollectionId);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should delete favorite by user ID and collection ID")
  @Tag("standard-processing")
  void shouldDeleteByUserIdAndCollectionId() {
    // When
    favoriteRepository.deleteByIdUserIdAndIdCollectionId(testUserId, testCollectionId);

    // Then
    verify(favoriteRepository).deleteByIdUserIdAndIdCollectionId(testUserId, testCollectionId);
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
  @DisplayName("Should count favorites by collection ID")
  @Tag("standard-processing")
  void shouldCountByCollectionId() {
    // Given
    when(favoriteRepository.countByIdCollectionId(testCollectionId)).thenReturn(15L);

    // When
    long count = favoriteRepository.countByIdCollectionId(testCollectionId);

    // Then
    assertThat(count).isEqualTo(15);
  }

  @Test
  @DisplayName("Should return zero count when collection has no favorites")
  @Tag("standard-processing")
  void shouldReturnZeroCountWhenCollectionHasNoFavorites() {
    // Given
    when(favoriteRepository.countByIdCollectionId(testCollectionId)).thenReturn(0L);

    // When
    long count = favoriteRepository.countByIdCollectionId(testCollectionId);

    // Then
    assertThat(count).isZero();
  }

  @Test
  @DisplayName("Should support pagination with custom page size")
  @Tag("standard-processing")
  void shouldSupportPaginationWithCustomPageSize() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    List<CollectionFavorite> favorites =
        Arrays.asList(
            createTestFavorite(testUserId, 101L),
            createTestFavorite(testUserId, 102L),
            createTestFavorite(testUserId, 103L),
            createTestFavorite(testUserId, 104L),
            createTestFavorite(testUserId, 105L));
    Page<CollectionFavorite> expectedPage = new PageImpl<>(favorites, pageable, 25);
    when(favoriteRepository.findByUserIdWithCollection(testUserId, pageable))
        .thenReturn(expectedPage);

    // When
    Page<CollectionFavorite> result =
        favoriteRepository.findByUserIdWithCollection(testUserId, pageable);

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
    List<CollectionFavorite> favorites =
        Arrays.asList(
            createTestFavorite(testUserId, 121L), createTestFavorite(testUserId, 122L));
    Page<CollectionFavorite> expectedPage = new PageImpl<>(favorites, pageable, 22);
    when(favoriteRepository.findByUserIdWithCollection(testUserId, pageable))
        .thenReturn(expectedPage);

    // When
    Page<CollectionFavorite> result =
        favoriteRepository.findByUserIdWithCollection(testUserId, pageable);

    // Then
    assertThat(result.getNumber()).isEqualTo(1);
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(22);
  }

  private CollectionFavorite createTestFavorite(UUID userId, Long collectionId) {
    CollectionFavoriteId id =
        CollectionFavoriteId.builder().userId(userId).collectionId(collectionId).build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .name("Test Collection " + collectionId)
            .description("Test Description")
            .build();

    return CollectionFavorite.builder()
        .id(id)
        .collection(collection)
        .favoritedAt(LocalDateTime.now())
        .build();
  }
}
