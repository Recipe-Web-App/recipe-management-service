package com.recipe_manager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recipe_manager.model.dto.request.CreateCollectionRequest;
import com.recipe_manager.model.dto.request.UpdateCollectionRequest;
import com.recipe_manager.model.dto.response.CollectionDetailsDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.service.CollectionService;

import jakarta.validation.Valid;

/**
 * REST controller for Recipe Collection API endpoints.
 *
 * <p>Provides endpoints for managing recipe collections including retrieval, creation, updates, and
 * managing collaborators.
 */
@RestController
@RequestMapping("/collections")
public class CollectionController {

  /** Default page size for pagination. */
  private static final int DEFAULT_PAGE_SIZE = 20;

  /** Service for collection operations. */
  private final CollectionService collectionService;

  /**
   * Constructs the controller with required services.
   *
   * @param collectionService the collection service
   */
  public CollectionController(final CollectionService collectionService) {
    this.collectionService = collectionService;
  }

  /**
   * Get all collections accessible to the authenticated user.
   *
   * <p>Returns collections that the user owns, collaborates on, or are publicly accessible. Results
   * are paginated and can be sorted.
   *
   * @param pageable pagination parameters (page, size, sort)
   * @return ResponseEntity with paginated list of collections
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<CollectionDto>> getCollections(
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
    return collectionService.getAccessibleCollections(pageable);
  }

  /**
   * Create a new recipe collection.
   *
   * <p>Creates a new collection owned by the authenticated user. The collection can be public,
   * private, or friends-only, and can have different collaboration modes.
   *
   * @param request the create collection request with required fields
   * @return ResponseEntity with the created collection and 201 Created status
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CollectionDto> createCollection(
      @Valid @RequestBody final CreateCollectionRequest request) {
    return collectionService.createCollection(request);
  }

  /**
   * Get detailed information about a specific collection by ID.
   *
   * <p>Returns detailed collection information including all recipes in the collection. Access is
   * controlled by the collection's visibility settings and the user's permissions (owner,
   * collaborator, or public access).
   *
   * @param collectionId the ID of the collection to retrieve
   * @return ResponseEntity with the collection details including all recipes
   */
  @GetMapping(value = "/{collectionId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CollectionDetailsDto> getCollectionById(
      @PathVariable final Long collectionId) {
    return collectionService.getCollectionById(collectionId);
  }

  /**
   * Update collection metadata.
   *
   * <p>Updates collection properties such as name, description, visibility, and collaboration mode.
   * Only the collection owner can update the collection. This is a partial update - only provided
   * fields will be modified.
   *
   * @param collectionId the ID of the collection to update
   * @param request the update request containing optional fields to modify
   * @return ResponseEntity with the updated collection and 200 OK status
   */
  @PutMapping(
      value = "/{collectionId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CollectionDto> updateCollection(
      @PathVariable final Long collectionId,
      @Valid @RequestBody final UpdateCollectionRequest request) {
    return collectionService.updateCollection(collectionId, request);
  }
}
