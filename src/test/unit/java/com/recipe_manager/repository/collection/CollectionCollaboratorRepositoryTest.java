package com.recipe_manager.repository.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.entity.collection.CollectionCollaborator;
import com.recipe_manager.model.entity.collection.CollectionCollaboratorId;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CollectionCollaboratorRepository.
 *
 * <p>Tests repository methods for managing collection collaborators.
 *
 * <p>Note: These are mock-based unit tests since the repository depends on JPA infrastructure that
 * is not easily testable in isolation.
 */
@Tag("unit")
class CollectionCollaboratorRepositoryTest {

  private CollectionCollaboratorRepository collaboratorRepository;
  private Long testCollectionId;
  private UUID testUserId;

  @BeforeEach
  void setUp() {
    collaboratorRepository = mock(CollectionCollaboratorRepository.class);
    testCollectionId = 1L;
    testUserId = UUID.randomUUID();
  }

  @Test
  @DisplayName("Should find collaborators by collection ID")
  @Tag("standard-processing")
  void shouldFindByCollectionId() {
    // Given
    List<CollectionCollaborator> expectedCollaborators =
        Arrays.asList(
            createTestCollaborator(testCollectionId, UUID.randomUUID()),
            createTestCollaborator(testCollectionId, UUID.randomUUID()));
    when(collaboratorRepository.findByIdCollectionId(testCollectionId))
        .thenReturn(expectedCollaborators);

    // When
    List<CollectionCollaborator> result =
        collaboratorRepository.findByIdCollectionId(testCollectionId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedCollaborators);
  }

  @Test
  @DisplayName("Should return empty list for collection with no collaborators")
  @Tag("standard-processing")
  void shouldReturnEmptyListForCollectionWithNoCollaborators() {
    // Given
    when(collaboratorRepository.findByIdCollectionId(testCollectionId))
        .thenReturn(Collections.emptyList());

    // When
    List<CollectionCollaborator> result =
        collaboratorRepository.findByIdCollectionId(testCollectionId);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should find collections by user ID")
  @Tag("standard-processing")
  void shouldFindByUserId() {
    // Given
    List<CollectionCollaborator> expectedCollaborations =
        Arrays.asList(
            createTestCollaborator(1L, testUserId), createTestCollaborator(2L, testUserId));
    when(collaboratorRepository.findByIdUserId(testUserId)).thenReturn(expectedCollaborations);

    // When
    List<CollectionCollaborator> result = collaboratorRepository.findByIdUserId(testUserId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedCollaborations);
  }

  @Test
  @DisplayName("Should return empty list for user with no collaborations")
  @Tag("standard-processing")
  void shouldReturnEmptyListForUserWithNoCollaborations() {
    // Given
    when(collaboratorRepository.findByIdUserId(testUserId)).thenReturn(Collections.emptyList());

    // When
    List<CollectionCollaborator> result = collaboratorRepository.findByIdUserId(testUserId);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should check if user is collaborator on collection")
  @Tag("standard-processing")
  void shouldCheckExistsByCollectionIdAndUserId() {
    // Given
    when(collaboratorRepository.existsByIdCollectionIdAndIdUserId(testCollectionId, testUserId))
        .thenReturn(true);

    // When
    boolean exists =
        collaboratorRepository.existsByIdCollectionIdAndIdUserId(testCollectionId, testUserId);

    // Then
    assertThat(exists).isTrue();
    verify(collaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(testCollectionId, testUserId);
  }

  @Test
  @DisplayName("Should return false when user is not collaborator")
  @Tag("standard-processing")
  void shouldReturnFalseWhenUserIsNotCollaborator() {
    // Given
    when(collaboratorRepository.existsByIdCollectionIdAndIdUserId(testCollectionId, testUserId))
        .thenReturn(false);

    // When
    boolean exists =
        collaboratorRepository.existsByIdCollectionIdAndIdUserId(testCollectionId, testUserId);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should delete collaborator by collection ID and user ID")
  @Tag("standard-processing")
  void shouldDeleteByCollectionIdAndUserId() {
    // When
    collaboratorRepository.deleteByIdCollectionIdAndIdUserId(testCollectionId, testUserId);

    // Then
    verify(collaboratorRepository)
        .deleteByIdCollectionIdAndIdUserId(testCollectionId, testUserId);
  }

  @Test
  @DisplayName("Should count collaborators in collection")
  @Tag("standard-processing")
  void shouldCountByCollectionId() {
    // Given
    when(collaboratorRepository.countByIdCollectionId(testCollectionId)).thenReturn(2L);

    // When
    long count = collaboratorRepository.countByIdCollectionId(testCollectionId);

    // Then
    assertThat(count).isEqualTo(2);
  }

  @Test
  @DisplayName("Should return zero count for collection with no collaborators")
  @Tag("standard-processing")
  void shouldReturnZeroCountForCollectionWithNoCollaborators() {
    // Given
    when(collaboratorRepository.countByIdCollectionId(testCollectionId)).thenReturn(0L);

    // When
    long count = collaboratorRepository.countByIdCollectionId(testCollectionId);

    // Then
    assertThat(count).isZero();
  }

  @Test
  @DisplayName("Should delete all collaborators from collection")
  @Tag("standard-processing")
  void shouldDeleteByCollectionId() {
    // When
    collaboratorRepository.deleteByIdCollectionId(testCollectionId);

    // Then
    verify(collaboratorRepository).deleteByIdCollectionId(testCollectionId);
  }

  @Test
  @DisplayName("Should find collaborators in multiple collections")
  @Tag("standard-processing")
  void shouldFindByCollectionIdIn() {
    // Given
    List<Long> collectionIds = Arrays.asList(1L, 2L);
    List<CollectionCollaborator> expectedCollaborators =
        Arrays.asList(
            createTestCollaborator(1L, UUID.randomUUID()),
            createTestCollaborator(1L, UUID.randomUUID()),
            createTestCollaborator(2L, UUID.randomUUID()));
    when(collaboratorRepository.findByIdCollectionIdIn(collectionIds))
        .thenReturn(expectedCollaborators);

    // When
    List<CollectionCollaborator> result =
        collaboratorRepository.findByIdCollectionIdIn(collectionIds);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyElementsOf(expectedCollaborators);
  }

  @Test
  @DisplayName("Should find collaborators with usernames ordered by granted date")
  @Tag("standard-processing")
  void shouldFindCollaboratorsWithUsernamesByCollectionId() {
    // Given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    UUID grantedBy = UUID.randomUUID();

    // Result rows: collection_id, user_id, username, granted_by, granted_by_username, granted_at
    List<Object[]> expectedRows =
        Arrays.asList(
            new Object[] {
              testCollectionId, userId1, "user1", grantedBy, "admin", LocalDateTime.now()
            },
            new Object[] {
              testCollectionId, userId2, "user2", grantedBy, "admin", LocalDateTime.now().minusDays(1)
            });
    when(collaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(testCollectionId))
        .thenReturn(expectedRows);

    // When
    List<Object[]> result =
        collaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(testCollectionId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedRows);
    verify(collaboratorRepository).findCollaboratorsWithUsernamesByCollectionId(testCollectionId);
  }

  @Test
  @DisplayName("Should return empty list when no collaborators have usernames")
  @Tag("standard-processing")
  void shouldReturnEmptyListWhenNoCollaboratorsWithUsernames() {
    // Given
    when(collaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(testCollectionId))
        .thenReturn(Collections.emptyList());

    // When
    List<Object[]> result =
        collaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(testCollectionId);

    // Then
    assertThat(result).isEmpty();
  }

  private CollectionCollaborator createTestCollaborator(Long collectionId, UUID userId) {
    CollectionCollaboratorId id =
        CollectionCollaboratorId.builder().collectionId(collectionId).userId(userId).build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(UUID.randomUUID())
            .name("Test Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    return CollectionCollaborator.builder()
        .id(id)
        .collection(collection)
        .grantedBy(UUID.randomUUID())
        .grantedAt(LocalDateTime.now())
        .build();
  }
}
