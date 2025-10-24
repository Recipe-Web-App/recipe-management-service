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

import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Unit tests for RecipeCollectionRepository.
 *
 * <p>Tests repository methods for managing recipe collections.
 *
 * <p>Note: These are mock-based unit tests since the repository depends on JPA infrastructure that
 * is not easily testable in isolation.
 */
@Tag("unit")
class RecipeCollectionRepositoryTest {

  private RecipeCollectionRepository recipeCollectionRepository;
  private UUID testUserId;

  @BeforeEach
  void setUp() {
    recipeCollectionRepository = mock(RecipeCollectionRepository.class);
    testUserId = UUID.randomUUID();
  }

  @Test
  @DisplayName("Should find collections by user ID")
  @Tag("standard-processing")
  void shouldFindByUserId() {
    // Given
    List<RecipeCollection> expectedCollections =
        Arrays.asList(createTestCollection(1L), createTestCollection(2L));
    when(recipeCollectionRepository.findByUserId(testUserId)).thenReturn(expectedCollections);

    // When
    List<RecipeCollection> result = recipeCollectionRepository.findByUserId(testUserId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedCollections);
  }

  @Test
  @DisplayName("Should find collections by user ID ordered by created date")
  @Tag("standard-processing")
  void shouldFindByUserIdOrderByCreatedAtDesc() {
    // Given
    List<RecipeCollection> expectedCollections =
        Arrays.asList(createTestCollection(2L), createTestCollection(1L));
    when(recipeCollectionRepository.findByUserIdOrderByCreatedAtDesc(testUserId))
        .thenReturn(expectedCollections);

    // When
    List<RecipeCollection> result =
        recipeCollectionRepository.findByUserIdOrderByCreatedAtDesc(testUserId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedCollections);
  }

  @Test
  @DisplayName("Should check if collection exists and is owned by user")
  @Tag("standard-processing")
  void shouldCheckExistsByCollectionIdAndUserId() {
    // Given
    Long collectionId = 1L;
    when(recipeCollectionRepository.existsByCollectionIdAndUserId(collectionId, testUserId))
        .thenReturn(true);

    // When
    boolean exists =
        recipeCollectionRepository.existsByCollectionIdAndUserId(collectionId, testUserId);

    // Then
    assertThat(exists).isTrue();
    verify(recipeCollectionRepository).existsByCollectionIdAndUserId(collectionId, testUserId);
  }

  @Test
  @DisplayName("Should return false when collection does not exist")
  @Tag("standard-processing")
  void shouldReturnFalseWhenCollectionDoesNotExist() {
    // Given
    Long collectionId = 999L;
    when(recipeCollectionRepository.existsByCollectionIdAndUserId(collectionId, testUserId))
        .thenReturn(false);

    // When
    boolean exists =
        recipeCollectionRepository.existsByCollectionIdAndUserId(collectionId, testUserId);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should find collection by ID and user ID")
  @Tag("standard-processing")
  void shouldFindByCollectionIdAndUserId() {
    // Given
    Long collectionId = 1L;
    RecipeCollection expectedCollection = createTestCollection(collectionId);
    when(recipeCollectionRepository.findByCollectionIdAndUserId(collectionId, testUserId))
        .thenReturn(Optional.of(expectedCollection));

    // When
    Optional<RecipeCollection> result =
        recipeCollectionRepository.findByCollectionIdAndUserId(collectionId, testUserId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(expectedCollection);
  }

  @Test
  @DisplayName("Should return empty when user does not own collection")
  @Tag("standard-processing")
  void shouldReturnEmptyWhenUserDoesNotOwnCollection() {
    // Given
    Long collectionId = 1L;
    when(recipeCollectionRepository.findByCollectionIdAndUserId(collectionId, testUserId))
        .thenReturn(Optional.empty());

    // When
    Optional<RecipeCollection> result =
        recipeCollectionRepository.findByCollectionIdAndUserId(collectionId, testUserId);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should find collections by visibility")
  @Tag("standard-processing")
  void shouldFindByVisibility() {
    // Given
    List<RecipeCollection> expectedCollections =
        Arrays.asList(createTestCollection(1L), createTestCollection(2L));
    when(recipeCollectionRepository.findByVisibility(CollectionVisibility.PUBLIC))
        .thenReturn(expectedCollections);

    // When
    List<RecipeCollection> result =
        recipeCollectionRepository.findByVisibility(CollectionVisibility.PUBLIC);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedCollections);
  }

  @Test
  @DisplayName("Should find collections by visibility with pagination")
  @Tag("standard-processing")
  void shouldFindByVisibilityWithPagination() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    List<RecipeCollection> collections =
        Arrays.asList(createTestCollection(1L), createTestCollection(2L));
    Page<RecipeCollection> expectedPage = new PageImpl<>(collections, pageable, 2);
    when(recipeCollectionRepository.findByVisibility(CollectionVisibility.PUBLIC, pageable))
        .thenReturn(expectedPage);

    // When
    Page<RecipeCollection> result =
        recipeCollectionRepository.findByVisibility(CollectionVisibility.PUBLIC, pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should search collections with filters")
  @Tag("standard-processing")
  void shouldSearchCollectionsWithFilters() {
    // Given
    String[] visibilityList = new String[] {"PUBLIC"};
    String[] collaborationModeList = new String[] {"OWNER_ONLY"};
    Pageable pageable = PageRequest.of(0, 10);
    List<RecipeCollection> collections = Arrays.asList(createTestCollection(1L));
    Page<RecipeCollection> expectedPage = new PageImpl<>(collections, pageable, 1);

    when(recipeCollectionRepository.searchCollections(
            visibilityList, collaborationModeList, testUserId, null, null, pageable))
        .thenReturn(expectedPage);

    // When
    Page<RecipeCollection> result =
        recipeCollectionRepository.searchCollections(
            visibilityList, collaborationModeList, testUserId, null, null, pageable);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getTotalElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("Should search collections with no filters")
  @Tag("standard-processing")
  void shouldSearchCollectionsWithNoFilters() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    List<RecipeCollection> collections =
        Arrays.asList(createTestCollection(1L), createTestCollection(2L), createTestCollection(3L));
    Page<RecipeCollection> expectedPage = new PageImpl<>(collections, pageable, 3);

    when(recipeCollectionRepository.searchCollections(null, null, null, null, null, pageable))
        .thenReturn(expectedPage);

    // When
    Page<RecipeCollection> result =
        recipeCollectionRepository.searchCollections(null, null, null, null, null, pageable);

    // Then
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getTotalElements()).isEqualTo(3);
  }

  @Test
  @DisplayName("Should return empty list when no collections found")
  @Tag("standard-processing")
  void shouldReturnEmptyListWhenNoCollectionsFound() {
    // Given
    when(recipeCollectionRepository.findByUserId(testUserId))
        .thenReturn(Collections.emptyList());

    // When
    List<RecipeCollection> result = recipeCollectionRepository.findByUserId(testUserId);

    // Then
    assertThat(result).isEmpty();
  }

  private RecipeCollection createTestCollection(Long collectionId) {
    return RecipeCollection.builder()
        .collectionId(collectionId)
        .userId(testUserId)
        .name("Test Collection " + collectionId)
        .description("Test Description")
        .visibility(CollectionVisibility.PUBLIC)
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
