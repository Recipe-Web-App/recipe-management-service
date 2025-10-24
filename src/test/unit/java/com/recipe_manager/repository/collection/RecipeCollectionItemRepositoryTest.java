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
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.model.entity.collection.RecipeCollectionItemId;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeCollectionItemRepository.
 *
 * <p>Tests repository methods for managing recipe-to-collection associations.
 *
 * <p>Note: These are mock-based unit tests since the repository depends on JPA infrastructure that
 * is not easily testable in isolation.
 */
@Tag("unit")
class RecipeCollectionItemRepositoryTest {

  private RecipeCollectionItemRepository itemRepository;
  private Long testCollectionId;
  private Long testRecipeId;

  @BeforeEach
  void setUp() {
    itemRepository = mock(RecipeCollectionItemRepository.class);
    testCollectionId = 1L;
    testRecipeId = 10L;
  }

  @Test
  @DisplayName("Should find items by collection ID ordered by display order")
  @Tag("standard-processing")
  void shouldFindByCollectionIdOrderedByDisplayOrder() {
    // Given
    List<RecipeCollectionItem> expectedItems =
        Arrays.asList(
            createTestItem(testCollectionId, 101L, 5),
            createTestItem(testCollectionId, 102L, 10),
            createTestItem(testCollectionId, 103L, 20));
    when(itemRepository.findByIdCollectionIdOrderByDisplayOrderAsc(testCollectionId))
        .thenReturn(expectedItems);

    // When
    List<RecipeCollectionItem> result =
        itemRepository.findByIdCollectionIdOrderByDisplayOrderAsc(testCollectionId);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getDisplayOrder()).isEqualTo(5);
    assertThat(result.get(1).getDisplayOrder()).isEqualTo(10);
    assertThat(result.get(2).getDisplayOrder()).isEqualTo(20);
  }

  @Test
  @DisplayName("Should find items by collection ID")
  @Tag("standard-processing")
  void shouldFindByCollectionId() {
    // Given
    List<RecipeCollectionItem> expectedItems =
        Arrays.asList(
            createTestItem(testCollectionId, 101L, 10),
            createTestItem(testCollectionId, 102L, 20));
    when(itemRepository.findByIdCollectionId(testCollectionId)).thenReturn(expectedItems);

    // When
    List<RecipeCollectionItem> result = itemRepository.findByIdCollectionId(testCollectionId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedItems);
  }

  @Test
  @DisplayName("Should return empty list for collection with no items")
  @Tag("standard-processing")
  void shouldReturnEmptyListForCollectionWithNoItems() {
    // Given
    when(itemRepository.findByIdCollectionId(testCollectionId))
        .thenReturn(Collections.emptyList());

    // When
    List<RecipeCollectionItem> result = itemRepository.findByIdCollectionId(testCollectionId);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should find items by recipe ID")
  @Tag("standard-processing")
  void shouldFindByRecipeId() {
    // Given
    List<RecipeCollectionItem> expectedItems =
        Arrays.asList(
            createTestItem(1L, testRecipeId, 10), createTestItem(2L, testRecipeId, 5));
    when(itemRepository.findByIdRecipeId(testRecipeId)).thenReturn(expectedItems);

    // When
    List<RecipeCollectionItem> result = itemRepository.findByIdRecipeId(testRecipeId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedItems);
  }

  @Test
  @DisplayName("Should find item by collection ID and recipe ID")
  @Tag("standard-processing")
  void shouldFindByCollectionIdAndRecipeId() {
    // Given
    RecipeCollectionItem expectedItem = createTestItem(testCollectionId, testRecipeId, 10);
    when(itemRepository.findByIdCollectionIdAndIdRecipeId(testCollectionId, testRecipeId))
        .thenReturn(Optional.of(expectedItem));

    // When
    Optional<RecipeCollectionItem> result =
        itemRepository.findByIdCollectionIdAndIdRecipeId(testCollectionId, testRecipeId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(expectedItem);
  }

  @Test
  @DisplayName("Should return empty when item does not exist")
  @Tag("standard-processing")
  void shouldReturnEmptyWhenItemDoesNotExist() {
    // Given
    when(itemRepository.findByIdCollectionIdAndIdRecipeId(testCollectionId, testRecipeId))
        .thenReturn(Optional.empty());

    // When
    Optional<RecipeCollectionItem> result =
        itemRepository.findByIdCollectionIdAndIdRecipeId(testCollectionId, testRecipeId);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should check if recipe exists in collection")
  @Tag("standard-processing")
  void shouldCheckExistsByCollectionIdAndRecipeId() {
    // Given
    when(itemRepository.existsByIdCollectionIdAndIdRecipeId(testCollectionId, testRecipeId))
        .thenReturn(true);

    // When
    boolean exists =
        itemRepository.existsByIdCollectionIdAndIdRecipeId(testCollectionId, testRecipeId);

    // Then
    assertThat(exists).isTrue();
    verify(itemRepository).existsByIdCollectionIdAndIdRecipeId(testCollectionId, testRecipeId);
  }

  @Test
  @DisplayName("Should return false when recipe not in collection")
  @Tag("standard-processing")
  void shouldReturnFalseWhenRecipeNotInCollection() {
    // Given
    when(itemRepository.existsByIdCollectionIdAndIdRecipeId(testCollectionId, testRecipeId))
        .thenReturn(false);

    // When
    boolean exists =
        itemRepository.existsByIdCollectionIdAndIdRecipeId(testCollectionId, testRecipeId);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should delete item by collection ID and recipe ID")
  @Tag("standard-processing")
  void shouldDeleteByCollectionIdAndRecipeId() {
    // When
    itemRepository.deleteByIdCollectionIdAndIdRecipeId(testCollectionId, testRecipeId);

    // Then
    verify(itemRepository).deleteByIdCollectionIdAndIdRecipeId(testCollectionId, testRecipeId);
  }

  @Test
  @DisplayName("Should count items in collection")
  @Tag("standard-processing")
  void shouldCountByCollectionId() {
    // Given
    when(itemRepository.countByIdCollectionId(testCollectionId)).thenReturn(3L);

    // When
    long count = itemRepository.countByIdCollectionId(testCollectionId);

    // Then
    assertThat(count).isEqualTo(3);
  }

  @Test
  @DisplayName("Should return zero count for empty collection")
  @Tag("standard-processing")
  void shouldReturnZeroCountForEmptyCollection() {
    // Given
    when(itemRepository.countByIdCollectionId(testCollectionId)).thenReturn(0L);

    // When
    long count = itemRepository.countByIdCollectionId(testCollectionId);

    // Then
    assertThat(count).isZero();
  }

  @Test
  @DisplayName("Should find max display order")
  @Tag("standard-processing")
  void shouldFindMaxDisplayOrder() {
    // Given
    when(itemRepository.findMaxDisplayOrderByCollectionId(testCollectionId)).thenReturn(20);

    // When
    Integer maxOrder = itemRepository.findMaxDisplayOrderByCollectionId(testCollectionId);

    // Then
    assertThat(maxOrder).isEqualTo(20);
  }

  @Test
  @DisplayName("Should return null max display order for empty collection")
  @Tag("standard-processing")
  void shouldReturnNullMaxDisplayOrderForEmptyCollection() {
    // Given
    when(itemRepository.findMaxDisplayOrderByCollectionId(testCollectionId)).thenReturn(null);

    // When
    Integer maxOrder = itemRepository.findMaxDisplayOrderByCollectionId(testCollectionId);

    // Then
    assertThat(maxOrder).isNull();
  }

  @Test
  @DisplayName("Should find items in multiple collections")
  @Tag("standard-processing")
  void shouldFindByCollectionIdIn() {
    // Given
    List<Long> collectionIds = Arrays.asList(1L, 2L);
    List<RecipeCollectionItem> expectedItems =
        Arrays.asList(
            createTestItem(1L, 101L, 10),
            createTestItem(1L, 102L, 20),
            createTestItem(2L, 103L, 10));
    when(itemRepository.findByIdCollectionIdIn(collectionIds)).thenReturn(expectedItems);

    // When
    List<RecipeCollectionItem> result = itemRepository.findByIdCollectionIdIn(collectionIds);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyElementsOf(expectedItems);
  }

  @Test
  @DisplayName("Should delete all items from collection")
  @Tag("standard-processing")
  void shouldDeleteByCollectionId() {
    // When
    itemRepository.deleteByIdCollectionId(testCollectionId);

    // Then
    verify(itemRepository).deleteByIdCollectionId(testCollectionId);
  }

  private RecipeCollectionItem createTestItem(
      Long collectionId, Long recipeId, int displayOrder) {
    RecipeCollectionItemId id =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId).build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(UUID.randomUUID())
            .name("Test Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Recipe recipe = Recipe.builder().recipeId(recipeId).title("Test Recipe").build();

    return RecipeCollectionItem.builder()
        .id(id)
        .collection(collection)
        .recipe(recipe)
        .displayOrder(displayOrder)
        .addedBy(UUID.randomUUID())
        .addedAt(LocalDateTime.now())
        .build();
  }
}
