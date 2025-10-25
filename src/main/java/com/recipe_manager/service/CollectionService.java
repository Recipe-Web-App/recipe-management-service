package com.recipe_manager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.mapper.CollectionMapper;
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

  /**
   * Constructs the collection service with required dependencies.
   *
   * @param recipeCollectionRepository the repository used for accessing collection data
   * @param collectionMapper the mapper used for converting between projections and DTOs
   */
  public CollectionService(
      final RecipeCollectionRepository recipeCollectionRepository,
      final CollectionMapper collectionMapper) {
    this.recipeCollectionRepository = recipeCollectionRepository;
    this.collectionMapper = collectionMapper;
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
}
