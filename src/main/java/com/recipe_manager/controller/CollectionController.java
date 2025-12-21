package com.recipe_manager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recipe_manager.model.dto.collection.CollectionCollaboratorDto;
import com.recipe_manager.model.dto.collection.RecipeCollectionItemDto;
import com.recipe_manager.model.dto.request.CreateCollectionRequest;
import com.recipe_manager.model.dto.request.SearchCollectionsRequest;
import com.recipe_manager.model.dto.request.UpdateCollectionRequest;
import com.recipe_manager.model.dto.response.CollectionDetailsDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.service.CollectionService;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "CollectionService is a Spring-managed bean injected via constructor")
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
  public ResponseEntity<CollectionDetailsDto> createCollection(
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

  /**
   * Delete a collection permanently.
   *
   * <p>Permanently removes a collection and all associated items and collaborators. Only the
   * collection owner can delete the collection. Deletion is performed via cascade in the database,
   * ensuring all related data (recipe items and collaborators) are also removed.
   *
   * @param collectionId the ID of the collection to delete
   * @return ResponseEntity with 204 No Content status
   */
  @DeleteMapping("/{collectionId}")
  public ResponseEntity<Void> deleteCollection(@PathVariable final Long collectionId) {
    return collectionService.deleteCollection(collectionId);
  }

  /**
   * Search collections with advanced filtering.
   *
   * <p>Supports filtering by text query (searches name and description), visibility, collaboration
   * mode, owner, and recipe count ranges. All filters are optional and applied cumulatively using
   * AND logic. Results are paginated.
   *
   * @param request the search request containing filter criteria
   * @param pageable pagination parameters
   * @return ResponseEntity containing paginated search results
   */
  @PostMapping(
      value = "/search",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<CollectionDto>> searchCollections(
      @Valid @RequestBody final SearchCollectionsRequest request,
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
    return collectionService.searchCollections(request, pageable);
  }

  /**
   * Add a recipe to a collection.
   *
   * <p>Adds a recipe to a collection with automatic display order assignment. The user must have
   * edit permission on the collection (owner, or collaborator based on collaboration mode). If the
   * collection is empty, the recipe is assigned displayOrder 10. Otherwise, it's assigned max
   * displayOrder + 10 to maintain ordering gaps.
   *
   * @param collectionId the ID of the collection to add the recipe to
   * @param recipeId the ID of the recipe to add
   * @return ResponseEntity with the created collection item and 201 Created status
   */
  @PostMapping(
      value = "/{collectionId}/recipes/{recipeId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RecipeCollectionItemDto> addRecipeToCollection(
      @PathVariable final Long collectionId, @PathVariable final Long recipeId) {
    return collectionService.addRecipeToCollection(collectionId, recipeId);
  }

  /**
   * Remove a recipe from a collection.
   *
   * <p>Removes a recipe from a collection. The user must have edit permission on the collection
   * (owner, or collaborator based on collaboration mode). The recipe-to-collection association is
   * permanently deleted. Remaining recipes maintain their display order (gaps are allowed and do
   * not need to be reordered).
   *
   * @param collectionId the ID of the collection to remove the recipe from
   * @param recipeId the ID of the recipe to remove
   * @return ResponseEntity with 204 No Content status
   */
  @DeleteMapping("/{collectionId}/recipes/{recipeId}")
  public ResponseEntity<Void> removeRecipeFromCollection(
      @PathVariable final Long collectionId, @PathVariable final Long recipeId) {
    return collectionService.removeRecipeFromCollection(collectionId, recipeId);
  }

  /**
   * Update recipe display order in a collection.
   *
   * <p>Updates the display order for a single recipe in the collection. The user must have edit
   * permission on the collection (owner, or collaborator based on collaboration mode).
   *
   * <p>Returns the updated recipe with metadata including title and description.
   *
   * @param collectionId the ID of the collection
   * @param recipeId the ID of the recipe to update
   * @param request the update request containing the new display order
   * @return ResponseEntity containing the updated CollectionRecipeDto with recipe metadata
   */
  @PatchMapping(
      value = "/{collectionId}/recipes/{recipeId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<com.recipe_manager.model.dto.collection.CollectionRecipeDto>
      updateRecipeOrder(
          @PathVariable final Long collectionId,
          @PathVariable final Long recipeId,
          @Valid @RequestBody
              final com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest request) {
    return collectionService.updateRecipeOrder(collectionId, recipeId, request);
  }

  /**
   * Batch reorder recipes in a collection.
   *
   * <p>Updates the display order for multiple recipes in a collection at once. The user must have
   * edit permission on the collection. All recipes in the request must already exist in the
   * collection. The request must not contain duplicate display orders.
   *
   * <p>The endpoint validates that:
   *
   * <ul>
   *   <li>All requested recipes exist in the collection
   *   <li>No duplicate display orders in the request
   *   <li>User has edit permission on the collection
   * </ul>
   *
   * <p>Returns all recipes in the collection (not just the reordered ones) with updated metadata
   * including recipe titles and descriptions, sorted by display order.
   *
   * @param collectionId the ID of the collection
   * @param request the reorder request containing recipe IDs and new display orders
   * @return ResponseEntity containing list of all recipes in the collection with metadata
   */
  @PutMapping(
      value = "/{collectionId}/recipes/reorder",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<java.util.List<com.recipe_manager.model.dto.collection.CollectionRecipeDto>>
      reorderRecipes(
          @PathVariable final Long collectionId,
          @Valid @RequestBody
              final com.recipe_manager.model.dto.request.ReorderRecipesRequest request) {
    return collectionService.reorderRecipes(collectionId, request);
  }

  /**
   * Get all collaborators for a specific collection.
   *
   * <p>Returns a list of users who have been granted collaborator access to the collection. This
   * endpoint is only available for collections with SPECIFIC_USERS collaboration mode. The user
   * must have view permission for the collection (owner, collaborator, or public visibility).
   *
   * <p>Collaborators are returned ordered by when they were granted access (newest first),
   * including both the collaborator's username and the username of the user who granted access.
   *
   * @param collectionId the ID of the collection
   * @return ResponseEntity containing a list of collaborators with usernames
   */
  @GetMapping(value = "/{collectionId}/collaborators", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<java.util.List<CollectionCollaboratorDto>> getCollaborators(
      @PathVariable final Long collectionId) {
    return collectionService.getCollaborators(collectionId);
  }

  /**
   * Adds a collaborator to a collection.
   *
   * <p>POST /collections/{collectionId}/collaborators
   *
   * <p>Only the collection owner can add collaborators. The collection must use SPECIFIC_USERS
   * collaboration mode.
   *
   * @param collectionId the collection ID
   * @param request the request containing the user ID to add as collaborator
   * @return ResponseEntity with the newly added collaborator details and 201 Created status
   * @throws com.recipe_manager.exception.ResourceNotFoundException if collection or user not found
   *     (404)
   * @throws org.springframework.security.access.AccessDeniedException if user is not the owner or
   *     wrong collaboration mode (403)
   * @throws com.recipe_manager.exception.DuplicateResourceException if user is already a
   *     collaborator or is the owner (409)
   */
  @PostMapping(
      value = "/{collectionId}/collaborators",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CollectionCollaboratorDto> addCollaborator(
      @PathVariable final Long collectionId,
      @Valid @RequestBody
          final com.recipe_manager.model.dto.request.AddCollaboratorRequest request) {
    return collectionService.addCollaborator(collectionId, request);
  }

  /**
   * Removes a collaborator from a collection.
   *
   * <p>DELETE /collections/{collectionId}/collaborators/{userId}
   *
   * <p>Only the collection owner can remove collaborators. This endpoint removes a specific user
   * from the collection's collaborator list, regardless of the collection's collaboration mode.
   *
   * @param collectionId the collection ID
   * @param userId the user ID of the collaborator to remove
   * @return ResponseEntity with 204 No Content status
   * @throws com.recipe_manager.exception.ResourceNotFoundException if collection not found (404)
   * @throws com.recipe_manager.exception.ResourceNotFoundException if collaborator not found (404)
   * @throws org.springframework.security.access.AccessDeniedException if user is not the owner
   *     (403)
   */
  @DeleteMapping("/{collectionId}/collaborators/{userId}")
  public ResponseEntity<Void> removeCollaborator(
      @PathVariable final Long collectionId, @PathVariable final java.util.UUID userId) {
    return collectionService.removeCollaborator(collectionId, userId);
  }
}
