package com.recipe_manager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.request.CreateCollectionRequest;
import com.recipe_manager.model.dto.request.UpdateCollectionRequest;
import com.recipe_manager.model.dto.response.CollectionDetailsDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeCollectionMapper;
import com.recipe_manager.repository.collection.CollectionSummaryProjection;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.util.SecurityUtils;

/**
 * Service for recipe collection operations.
 *
 * <p>Provides business logic for managing recipe collections including retrieving accessible
 * collections, creating collections, and managing collection permissions.
 */
@Service
public class CollectionService {

  /** Repository used for accessing recipe collection data. */
  private final RecipeCollectionRepository recipeCollectionRepository;

  /** Mapper used for converting between collection projections and DTOs. */
  private final CollectionMapper collectionMapper;

  /** Mapper used for converting between collection entities and DTOs. */
  private final RecipeCollectionMapper recipeCollectionMapper;

  /**
   * Constructs the collection service with required dependencies.
   *
   * @param recipeCollectionRepository the repository used for accessing collection data
   * @param collectionMapper the mapper used for converting between projections and DTOs
   * @param recipeCollectionMapper the mapper used for converting between entities and DTOs
   */
  public CollectionService(
      final RecipeCollectionRepository recipeCollectionRepository,
      final CollectionMapper collectionMapper,
      final RecipeCollectionMapper recipeCollectionMapper) {
    this.recipeCollectionRepository = recipeCollectionRepository;
    this.collectionMapper = collectionMapper;
    this.recipeCollectionMapper = recipeCollectionMapper;
  }

  /**
   * Retrieves all collections accessible to the authenticated user with pagination.
   *
   * <p>This includes collections the user owns, collaborates on, and public collections based on
   * the vw_user_collection_access database view.
   *
   * @param pageable pagination parameters including page number, size, and sort criteria
   * @return ResponseEntity containing a page of collection DTOs
   */
  @Transactional(readOnly = true)
  public ResponseEntity<Page<CollectionDto>> getAccessibleCollections(final Pageable pageable) {
    // Get current authenticated user ID from security context
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Fetch accessible collections from repository
    Page<CollectionSummaryProjection> projectionPage =
        recipeCollectionRepository.findAccessibleCollections(currentUserId, pageable);

    // Map projections to DTOs
    Page<CollectionDto> dtoPage = projectionPage.map(collectionMapper::fromProjection);

    return ResponseEntity.ok(dtoPage);
  }

  /**
   * Creates a new recipe collection owned by the authenticated user.
   *
   * <p>Extracts the user ID from the security context, maps the request to an entity, saves it to
   * the database, and returns the created collection with a 201 Created status.
   *
   * @param request the create collection request containing collection details
   * @return ResponseEntity containing the created collection DTO with 201 status
   */
  @Transactional
  public ResponseEntity<CollectionDto> createCollection(final CreateCollectionRequest request) {
    // Get current authenticated user ID from security context
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Map request to entity
    RecipeCollection collection = recipeCollectionMapper.fromRequest(request);

    // Set the user ID (owner)
    collection.setUserId(currentUserId);

    // Save the collection (createdAt and updatedAt are set by JPA annotations)
    RecipeCollection savedCollection = recipeCollectionRepository.save(collection);

    // Convert saved entity to DTO
    CollectionDto responseDto = collectionMapper.toDto(savedCollection);

    // Return 201 Created with the new collection
    return ResponseEntity.status(201).body(responseDto);
  }

  /**
   * Retrieves detailed information about a specific collection by ID.
   *
   * <p>This method checks if the authenticated user has view access to the collection using the
   * vw_user_collection_access database view, fetches the collection with all recipes eagerly
   * loaded, and returns the detailed collection information including all recipes ordered by
   * display order.
   *
   * @param collectionId the ID of the collection to retrieve
   * @return ResponseEntity containing the detailed collection DTO with all recipes
   * @throws ResourceNotFoundException if the collection doesn't exist or user has no access
   */
  @Transactional(readOnly = true)
  public ResponseEntity<CollectionDetailsDto> getCollectionById(final Long collectionId) {
    // Get current authenticated user ID from security context
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Check view permission using database view
    if (!recipeCollectionRepository.hasViewAccess(collectionId, currentUserId)) {
      throw new ResourceNotFoundException("Collection not found or access denied");
    }

    // Fetch collection with items eagerly loaded
    RecipeCollection collection =
        recipeCollectionRepository
            .findByIdWithItems(collectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

    // Map entity to detailed DTO with all recipes
    CollectionDetailsDto dto = collectionMapper.toDetailsDto(collection);

    return ResponseEntity.ok(dto);
  }

  /**
   * Updates collection metadata (name, description, visibility, collaboration mode).
   *
   * <p>This method performs a partial update - only provided fields will be updated. The
   * authenticated user must be the collection owner. The updatedAt timestamp is automatically set.
   *
   * @param collectionId the ID of the collection to update
   * @param request the update request containing optional fields to update
   * @return ResponseEntity containing the updated collection DTO
   * @throws ResourceNotFoundException if the collection doesn't exist
   * @throws AccessDeniedException if the user is not the collection owner
   */
  @Transactional
  public ResponseEntity<CollectionDto> updateCollection(
      final Long collectionId, final UpdateCollectionRequest request) {
    // Get current authenticated user ID from security context
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Fetch the collection
    RecipeCollection collection =
        recipeCollectionRepository
            .findById(collectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

    // Verify ownership - only the owner can update the collection
    if (!collection.getUserId().equals(currentUserId)) {
      throw new AccessDeniedException("Only the collection owner can update it");
    }

    // Update only provided fields (partial update)
    if (request.getName() != null) {
      collection.setName(request.getName());
    }
    if (request.getDescription() != null) {
      collection.setDescription(request.getDescription());
    }
    if (request.getVisibility() != null) {
      collection.setVisibility(request.getVisibility());
    }
    if (request.getCollaborationMode() != null) {
      collection.setCollaborationMode(request.getCollaborationMode());
    }

    // Save the updated collection (updatedAt will be set automatically by JPA)
    RecipeCollection updatedCollection = recipeCollectionRepository.save(collection);

    // Convert updated entity to DTO
    CollectionDto responseDto = collectionMapper.toDto(updatedCollection);

    return ResponseEntity.ok(responseDto);
  }

  /**
   * Deletes a collection permanently.
   *
   * <p>This method permanently removes a collection and all associated items and collaborators (via
   * JPA cascade delete). Only the collection owner can delete the collection.
   *
   * @param collectionId the ID of the collection to delete
   * @return ResponseEntity with 204 No Content status
   * @throws ResourceNotFoundException if the collection doesn't exist
   * @throws AccessDeniedException if the user is not the collection owner
   */
  @Transactional
  public ResponseEntity<Void> deleteCollection(final Long collectionId) {
    // Get current authenticated user ID from security context
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Fetch the collection
    RecipeCollection collection =
        recipeCollectionRepository
            .findById(collectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

    // Verify ownership - only the owner can delete the collection
    if (!collection.getUserId().equals(currentUserId)) {
      throw new AccessDeniedException("Only the collection owner can delete it");
    }

    // Delete the collection (cascade will delete items and collaborators)
    recipeCollectionRepository.delete(collection);

    // Return 204 No Content
    return ResponseEntity.noContent().build();
  }
}
