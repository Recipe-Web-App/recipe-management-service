package com.recipe_manager.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.collection.CollectionTagDto;
import com.recipe_manager.model.dto.request.AddTagRequest;
import com.recipe_manager.model.dto.request.RemoveTagRequest;
import com.recipe_manager.model.dto.response.CollectionTagResponse;
import com.recipe_manager.model.entity.collection.CollectionTag;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.mapper.CollectionTagMapper;
import com.recipe_manager.repository.collection.CollectionTagRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;

/** Service for collection tag-related operations. */
@Service
public class CollectionTagService {

  /** Repository for managing collection data. */
  private final RecipeCollectionRepository collectionRepository;

  /** Repository for managing collection tag data. */
  private final CollectionTagRepository collectionTagRepository;

  /** Mapper for converting between CollectionTag entities and DTOs. */
  private final CollectionTagMapper collectionTagMapper;

  /**
   * Constructs the service with required dependencies.
   *
   * @param collectionRepository the collection repository
   * @param collectionTagRepository the collection tag repository
   * @param collectionTagMapper the collection tag mapper
   */
  public CollectionTagService(
      final RecipeCollectionRepository collectionRepository,
      final CollectionTagRepository collectionTagRepository,
      final CollectionTagMapper collectionTagMapper) {
    this.collectionRepository = collectionRepository;
    this.collectionTagRepository = collectionTagRepository;
    this.collectionTagMapper = collectionTagMapper;
  }

  /**
   * Add a tag to a collection.
   *
   * @param collectionId the collection ID
   * @param request the add tag request
   * @return response with updated tags
   * @throws ResourceNotFoundException if collection not found
   */
  @Transactional
  public CollectionTagResponse addTag(final Long collectionId, final AddTagRequest request) {
    RecipeCollection collection =
        collectionRepository
            .findById(collectionId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Collection not found with ID: " + collectionId));

    // Find or create the tag
    CollectionTag tag =
        collectionTagRepository
            .findByNameIgnoreCase(request.getName())
            .orElseGet(
                () -> {
                  CollectionTag newTag =
                      CollectionTag.builder().name(request.getName().trim()).build();
                  return collectionTagRepository.save(newTag);
                });

    // Add tag to collection if not already present
    if (!collection.getCollectionTags().contains(tag)) {
      // Create a new mutable list if the current one is immutable
      List<CollectionTag> updatedTags = new ArrayList<>(collection.getCollectionTags());
      updatedTags.add(tag);
      collection.setCollectionTags(updatedTags);
      collectionRepository.save(collection);
    }

    // Return updated tag list
    return getTagsResponse(collectionId, collection.getCollectionTags());
  }

  /**
   * Remove a tag from a collection.
   *
   * @param collectionId the collection ID
   * @param request the remove tag request
   * @return response with updated tags
   * @throws ResourceNotFoundException if collection or tag not found
   */
  @Transactional
  public CollectionTagResponse removeTag(final Long collectionId, final RemoveTagRequest request) {
    RecipeCollection collection =
        collectionRepository
            .findById(collectionId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Collection not found with ID: " + collectionId));

    CollectionTag tag =
        collectionTagRepository
            .findByNameIgnoreCase(request.getTagName())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Tag not found with name: " + request.getTagName()));

    // Remove tag from collection
    List<CollectionTag> updatedTags = new ArrayList<>(collection.getCollectionTags());
    updatedTags.remove(tag);
    collection.setCollectionTags(updatedTags);
    collectionRepository.save(collection);

    // Return updated tag list
    return getTagsResponse(collectionId, collection.getCollectionTags());
  }

  /**
   * Get tags for a collection.
   *
   * @param collectionId the collection ID
   * @return response with all tags for the collection
   * @throws ResourceNotFoundException if collection not found
   */
  public CollectionTagResponse getTags(final Long collectionId) {
    RecipeCollection collection =
        collectionRepository
            .findById(collectionId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Collection not found with ID: " + collectionId));

    return getTagsResponse(collectionId, collection.getCollectionTags());
  }

  /**
   * Helper method to create CollectionTagResponse from collection tags.
   *
   * @param collectionId the collection ID
   * @param tags the list of collection tags
   * @return CollectionTagResponse with mapped DTOs
   */
  private CollectionTagResponse getTagsResponse(
      final Long collectionId, final List<CollectionTag> tags) {
    List<CollectionTagDto> tagDtos = collectionTagMapper.toDtoList(tags);
    return CollectionTagResponse.builder().collectionId(collectionId).tags(tagDtos).build();
  }
}
