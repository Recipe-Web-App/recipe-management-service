package com.recipe_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.collection.CollectionTagDto;
import com.recipe_manager.model.dto.request.AddTagRequest;
import com.recipe_manager.model.dto.request.RemoveTagRequest;
import com.recipe_manager.model.dto.response.CollectionTagResponse;
import com.recipe_manager.model.entity.collection.CollectionTag;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.model.mapper.CollectionTagMapper;
import com.recipe_manager.repository.collection.CollectionTagRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;

/** Unit tests for {@link CollectionTagService}. */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CollectionTagServiceTest {

  @Mock private RecipeCollectionRepository collectionRepository;

  @Mock private CollectionTagRepository collectionTagRepository;

  @Mock private CollectionTagMapper collectionTagMapper;

  private CollectionTagService collectionTagService;

  @BeforeEach
  void setUp() {
    collectionTagService =
        new CollectionTagService(collectionRepository, collectionTagRepository, collectionTagMapper);
  }

  private RecipeCollection createTestCollection(Long id) {
    return RecipeCollection.builder()
        .collectionId(id)
        .userId(UUID.randomUUID())
        .name("Test Collection")
        .visibility(CollectionVisibility.PUBLIC)
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .collectionTags(Collections.emptyList())
        .build();
  }

  @Test
  @DisplayName("addTag should create new tag and add to collection")
  @Tag("standard-processing")
  void addTag_shouldCreateNewTagAndAddToCollection() {
    // Given
    Long collectionId = 1L;
    AddTagRequest request = AddTagRequest.builder().name("dessert").build();
    RecipeCollection collection = createTestCollection(collectionId);
    CollectionTag newTag = CollectionTag.builder().tagId(1L).name("dessert").build();
    List<CollectionTagDto> tagDtos =
        Arrays.asList(CollectionTagDto.builder().tagId(1L).name("dessert").build());

    when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionTagRepository.findByNameIgnoreCase("dessert")).thenReturn(Optional.empty());
    when(collectionTagRepository.save(any(CollectionTag.class))).thenReturn(newTag);
    when(collectionTagMapper.toDtoList(any())).thenReturn(tagDtos);

    // When
    CollectionTagResponse response = collectionTagService.addTag(collectionId, request);

    // Then
    assertNotNull(response);
    assertEquals(collectionId, response.getCollectionId());
    assertEquals(1, response.getTags().size());
    assertEquals("dessert", response.getTags().get(0).getName());
    verify(collectionTagRepository).save(any(CollectionTag.class));
    verify(collectionRepository).save(collection);
  }

  @Test
  @DisplayName("addTag should use existing tag if it exists")
  @Tag("standard-processing")
  void addTag_shouldUseExistingTagIfExists() {
    // Given
    Long collectionId = 1L;
    AddTagRequest request = AddTagRequest.builder().name("dessert").build();
    RecipeCollection collection = createTestCollection(collectionId);
    CollectionTag existingTag = CollectionTag.builder().tagId(1L).name("dessert").build();
    List<CollectionTagDto> tagDtos =
        Arrays.asList(CollectionTagDto.builder().tagId(1L).name("dessert").build());

    when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionTagRepository.findByNameIgnoreCase("dessert"))
        .thenReturn(Optional.of(existingTag));
    when(collectionTagMapper.toDtoList(any())).thenReturn(tagDtos);

    // When
    CollectionTagResponse response = collectionTagService.addTag(collectionId, request);

    // Then
    assertNotNull(response);
    assertEquals(collectionId, response.getCollectionId());
    assertEquals(1, response.getTags().size());
    verify(collectionRepository).save(collection);
  }

  @Test
  @DisplayName("addTag should throw exception for non-existent collection")
  @Tag("error-processing")
  void addTag_shouldThrowExceptionForNonExistentCollection() {
    // Given
    Long collectionId = 999L;
    AddTagRequest request = AddTagRequest.builder().name("dessert").build();

    when(collectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(
        ResourceNotFoundException.class, () -> collectionTagService.addTag(collectionId, request));
  }

  @Test
  @DisplayName("addTag should not add duplicate tag to collection")
  @Tag("standard-processing")
  void addTag_shouldNotAddDuplicateTag() {
    // Given
    Long collectionId = 1L;
    AddTagRequest request = AddTagRequest.builder().name("dessert").build();
    CollectionTag existingTag = CollectionTag.builder().tagId(1L).name("dessert").build();
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(UUID.randomUUID())
            .name("Test Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .collectionTags(Arrays.asList(existingTag))
            .build();
    List<CollectionTagDto> tagDtos =
        Arrays.asList(CollectionTagDto.builder().tagId(1L).name("dessert").build());

    when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionTagRepository.findByNameIgnoreCase("dessert"))
        .thenReturn(Optional.of(existingTag));
    when(collectionTagMapper.toDtoList(any())).thenReturn(tagDtos);

    // When
    CollectionTagResponse response = collectionTagService.addTag(collectionId, request);

    // Then
    assertNotNull(response);
    assertEquals(1, response.getTags().size());
    // Collection should not be saved since tag already exists
  }

  @Test
  @DisplayName("removeTag should remove tag from collection")
  @Tag("standard-processing")
  void removeTag_shouldRemoveTagFromCollection() {
    // Given
    Long collectionId = 1L;
    RemoveTagRequest request = RemoveTagRequest.builder().tagName("dessert").build();
    CollectionTag tag = CollectionTag.builder().tagId(1L).name("dessert").build();
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(UUID.randomUUID())
            .name("Test Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .collectionTags(Arrays.asList(tag))
            .build();

    when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionTagRepository.findByNameIgnoreCase("dessert")).thenReturn(Optional.of(tag));
    when(collectionTagMapper.toDtoList(any())).thenReturn(Collections.emptyList());

    // When
    CollectionTagResponse response = collectionTagService.removeTag(collectionId, request);

    // Then
    assertNotNull(response);
    assertEquals(collectionId, response.getCollectionId());
    assertTrue(response.getTags().isEmpty());
    verify(collectionRepository).save(collection);
  }

  @Test
  @DisplayName("removeTag should throw exception for non-existent collection")
  @Tag("error-processing")
  void removeTag_shouldThrowExceptionForNonExistentCollection() {
    // Given
    Long collectionId = 999L;
    RemoveTagRequest request = RemoveTagRequest.builder().tagName("dessert").build();

    when(collectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(
        ResourceNotFoundException.class,
        () -> collectionTagService.removeTag(collectionId, request));
  }

  @Test
  @DisplayName("removeTag should throw exception for non-existent tag")
  @Tag("error-processing")
  void removeTag_shouldThrowExceptionForNonExistentTag() {
    // Given
    Long collectionId = 1L;
    RemoveTagRequest request = RemoveTagRequest.builder().tagName("nonexistent").build();
    RecipeCollection collection = createTestCollection(collectionId);

    when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionTagRepository.findByNameIgnoreCase("nonexistent")).thenReturn(Optional.empty());

    // When & Then
    assertThrows(
        ResourceNotFoundException.class,
        () -> collectionTagService.removeTag(collectionId, request));
  }

  @Test
  @DisplayName("getTags should return all tags for collection")
  @Tag("standard-processing")
  void getTags_shouldReturnAllTagsForCollection() {
    // Given
    Long collectionId = 1L;
    List<CollectionTag> tags =
        Arrays.asList(
            CollectionTag.builder().tagId(1L).name("dessert").build(),
            CollectionTag.builder().tagId(2L).name("quick-meals").build());
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(UUID.randomUUID())
            .name("Test Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .collectionTags(tags)
            .build();
    List<CollectionTagDto> tagDtos =
        Arrays.asList(
            CollectionTagDto.builder().tagId(1L).name("dessert").build(),
            CollectionTagDto.builder().tagId(2L).name("quick-meals").build());

    when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionTagMapper.toDtoList(tags)).thenReturn(tagDtos);

    // When
    CollectionTagResponse response = collectionTagService.getTags(collectionId);

    // Then
    assertNotNull(response);
    assertEquals(collectionId, response.getCollectionId());
    assertEquals(2, response.getTags().size());
    assertEquals("dessert", response.getTags().get(0).getName());
    assertEquals("quick-meals", response.getTags().get(1).getName());
  }

  @Test
  @DisplayName("getTags should throw exception for non-existent collection")
  @Tag("error-processing")
  void getTags_shouldThrowExceptionForNonExistentCollection() {
    // Given
    Long collectionId = 999L;

    when(collectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(
        ResourceNotFoundException.class, () -> collectionTagService.getTags(collectionId));
  }

  @Test
  @DisplayName("getTags should return empty list for collection with no tags")
  @Tag("standard-processing")
  void getTags_shouldReturnEmptyListForCollectionWithNoTags() {
    // Given
    Long collectionId = 1L;
    RecipeCollection collection = createTestCollection(collectionId);

    when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionTagMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

    // When
    CollectionTagResponse response = collectionTagService.getTags(collectionId);

    // Then
    assertNotNull(response);
    assertEquals(collectionId, response.getCollectionId());
    assertTrue(response.getTags().isEmpty());
  }
}
