package com.recipe_manager.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipe_manager.exception.DuplicateResourceException;
import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.collection.CollectionCollaboratorDto;
import com.recipe_manager.model.dto.collection.RecipeCollectionItemDto;
import com.recipe_manager.model.dto.request.CreateCollectionRequest;
import com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder;
import com.recipe_manager.model.dto.request.SearchCollectionsRequest;
import com.recipe_manager.model.dto.request.UpdateCollectionRequest;
import com.recipe_manager.model.dto.response.CollectionDetailsDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.entity.collection.CollectionCollaborator;
import com.recipe_manager.model.entity.collection.CollectionCollaboratorId;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.model.entity.collection.RecipeCollectionItemId;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeCollectionItemMapper;
import com.recipe_manager.model.mapper.RecipeCollectionMapper;
import com.recipe_manager.repository.collection.CollectionCollaboratorRepository;
import com.recipe_manager.repository.collection.CollectionSummaryProjection;
import com.recipe_manager.repository.collection.RecipeCollectionItemRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.service.external.notificationservice.NotificationService;
import com.recipe_manager.util.SecurityUtils;

/**
 * Service for recipe collection operations.
 *
 * <p>Provides business logic for managing recipe collections including retrieving accessible
 * collections, creating collections, and managing collection permissions.
 */
@Service
public class CollectionService {

  /** Default display order increment for recipes in collections. */
  private static final int DISPLAY_ORDER_INCREMENT = 10;

  /** Array index for grantedBy field in collaborator query results. */
  private static final int GRANTED_BY_INDEX = 3;

  /** Array index for grantedByUsername field in collaborator query results. */
  private static final int GRANTED_BY_USERNAME_INDEX = 4;

  /** Array index for grantedAt field in collaborator query results. */
  private static final int GRANTED_AT_INDEX = 5;

  /** Repository used for accessing recipe collection data. */
  private final RecipeCollectionRepository recipeCollectionRepository;

  /** Repository used for accessing recipe collection items. */
  private final RecipeCollectionItemRepository recipeCollectionItemRepository;

  /** Repository used for accessing collection collaborators. */
  private final CollectionCollaboratorRepository collectionCollaboratorRepository;

  /** Mapper used for converting between collection projections and DTOs. */
  private final CollectionMapper collectionMapper;

  /** Mapper used for converting between collection entities and DTOs. */
  private final RecipeCollectionMapper recipeCollectionMapper;

  /** Mapper used for converting between recipe collection item entities and DTOs. */
  private final RecipeCollectionItemMapper recipeCollectionItemMapper;

  /** Repository used for accessing recipe data. */
  private final RecipeRepository recipeRepository;

  /** Service for sending notifications about recipe events. */
  private final NotificationService notificationService;

  /**
   * Constructs the collection service with required dependencies.
   *
   * @param recipeCollectionRepository the repository used for accessing collection data
   * @param recipeCollectionItemRepository the repository used for accessing collection items
   * @param collectionCollaboratorRepository the repository used for accessing collaborators
   * @param collectionMapper the mapper used for converting between projections and DTOs
   * @param recipeCollectionMapper the mapper used for converting between entities and DTOs
   * @param recipeCollectionItemMapper the mapper used for converting between item entities and DTOs
   * @param recipeRepository the repository used for accessing recipe data
   * @param notificationService the service for sending notifications
   */
  public CollectionService(
      final RecipeCollectionRepository recipeCollectionRepository,
      final RecipeCollectionItemRepository recipeCollectionItemRepository,
      final CollectionCollaboratorRepository collectionCollaboratorRepository,
      final CollectionMapper collectionMapper,
      final RecipeCollectionMapper recipeCollectionMapper,
      final RecipeCollectionItemMapper recipeCollectionItemMapper,
      final RecipeRepository recipeRepository,
      final NotificationService notificationService) {
    this.recipeCollectionRepository = recipeCollectionRepository;
    this.recipeCollectionItemRepository = recipeCollectionItemRepository;
    this.collectionCollaboratorRepository = collectionCollaboratorRepository;
    this.collectionMapper = collectionMapper;
    this.recipeCollectionMapper = recipeCollectionMapper;
    this.recipeCollectionItemMapper = recipeCollectionItemMapper;
    this.recipeRepository = recipeRepository;
    this.notificationService = notificationService;
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
   * <p>Supports batch operations:
   *
   * <ul>
   *   <li>If recipeIds are provided, adds those recipes to the collection during creation
   *   <li>If collaboratorIds are provided and collaborationMode is SPECIFIC_USERS, adds those users
   *       as collaborators
   * </ul>
   *
   * @param request the create collection request containing collection details
   * @return ResponseEntity containing the created collection DTO with 201 status
   * @throws ResourceNotFoundException if any recipe ID or collaborator user ID doesn't exist
   */
  @Transactional
  public ResponseEntity<CollectionDetailsDto> createCollection(
      final CreateCollectionRequest request) {
    // Get current authenticated user ID from security context
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Map request to entity
    RecipeCollection collection = recipeCollectionMapper.fromRequest(request);

    // Set the user ID (owner)
    collection.setUserId(currentUserId);

    // Save the collection (createdAt and updatedAt are set by JPA annotations)
    RecipeCollection savedCollection = recipeCollectionRepository.save(collection);

    // Process batch recipe additions if provided
    addRecipesDuringCreation(request.getRecipeIds(), savedCollection, currentUserId);

    // Process batch collaborator additions if applicable
    addCollaboratorsDuringCreation(request.getCollaboratorIds(), savedCollection, currentUserId);

    // Re-fetch collection with items and collaborators eagerly loaded
    RecipeCollection fullCollection =
        recipeCollectionRepository
            .findByIdWithItems(savedCollection.getCollectionId())
            .orElse(savedCollection);

    // Convert to detailed DTO with full recipe and collaborator lists
    CollectionDetailsDto responseDto = collectionMapper.toDetailsDto(fullCollection);

    // Return 201 Created with the new collection
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  /**
   * Adds recipes to a collection during creation.
   *
   * @param recipeIds list of recipe IDs to add (may be null or empty)
   * @param collection the saved collection entity
   * @param currentUserId the current user ID
   * @return the number of recipes added
   * @throws ResourceNotFoundException if any recipe ID doesn't exist
   */
  private int addRecipesDuringCreation(
      final List<Long> recipeIds, final RecipeCollection collection, final UUID currentUserId) {
    if (recipeIds == null || recipeIds.isEmpty()) {
      return 0;
    }

    // Use a set to handle duplicates in the request
    java.util.Set<Long> uniqueRecipeIds = new java.util.LinkedHashSet<>(recipeIds);
    int displayOrder = 0;

    for (Long recipeId : uniqueRecipeIds) {
      // Validate recipe exists
      Recipe recipe =
          recipeRepository
              .findById(recipeId)
              .orElseThrow(
                  () -> new ResourceNotFoundException("Recipe with ID " + recipeId + " not found"));

      // Calculate display order
      displayOrder += DISPLAY_ORDER_INCREMENT;

      // Create the collection item entity
      RecipeCollectionItemId itemId =
          RecipeCollectionItemId.builder()
              .collectionId(collection.getCollectionId())
              .recipeId(recipeId)
              .build();

      RecipeCollectionItem collectionItem =
          RecipeCollectionItem.builder()
              .id(itemId)
              .collection(collection)
              .recipe(recipe)
              .displayOrder(displayOrder)
              .addedBy(currentUserId)
              .build();

      // Save the entity
      recipeCollectionItemRepository.save(collectionItem);

      // Trigger async notification to recipe author
      notificationService.notifyRecipeCollectedAsync(
          recipe.getUserId(), recipeId, collection.getCollectionId(), currentUserId);
    }

    return uniqueRecipeIds.size();
  }

  /**
   * Adds collaborators to a collection during creation.
   *
   * <p>Only processes collaborators if the collection's collaboration mode is SPECIFIC_USERS.
   * Silently skips the owner if included in the list.
   *
   * @param collaboratorIds list of user IDs to add as collaborators (may be null or empty)
   * @param collection the saved collection entity
   * @param currentUserId the current user ID (owner)
   * @return the number of collaborators added
   * @throws ResourceNotFoundException if any user ID doesn't exist
   */
  private int addCollaboratorsDuringCreation(
      final List<UUID> collaboratorIds,
      final RecipeCollection collection,
      final UUID currentUserId) {
    // Only add collaborators for SPECIFIC_USERS mode
    if (collection.getCollaborationMode() != CollaborationMode.SPECIFIC_USERS) {
      return 0;
    }

    if (collaboratorIds == null || collaboratorIds.isEmpty()) {
      return 0;
    }

    // Use a set to handle duplicates in the request
    java.util.Set<UUID> uniqueCollaboratorIds = new java.util.LinkedHashSet<>(collaboratorIds);
    int addedCount = 0;

    for (UUID userId : uniqueCollaboratorIds) {
      // Skip if this is the owner (owner cannot be a collaborator)
      if (userId.equals(currentUserId)) {
        continue;
      }

      // Create collaborator entity
      CollectionCollaboratorId collaboratorId =
          new CollectionCollaboratorId(collection.getCollectionId(), userId);
      CollectionCollaborator collaborator =
          CollectionCollaborator.builder()
              .id(collaboratorId)
              .collection(collection)
              .grantedBy(currentUserId)
              .build();

      // Save the collaborator - this will throw DataIntegrityViolationException if user doesn't
      // exist
      try {
        collectionCollaboratorRepository.save(collaborator);
        addedCount++;
      } catch (org.springframework.dao.DataIntegrityViolationException e) {
        throw new ResourceNotFoundException("User with ID " + userId + " not found");
      }
    }

    return addedCount;
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

  /**
   * Searches collections with advanced filtering.
   *
   * <p>Supports filtering by text query (searches name and description), visibility, collaboration
   * mode, owner, and recipe count ranges. All filters are optional and applied cumulatively using
   * AND logic. Results are paginated.
   *
   * @param request the search request containing filter criteria
   * @param pageable pagination information
   * @return ResponseEntity containing paginated search results
   */
  @Transactional(readOnly = true)
  public ResponseEntity<Page<CollectionDto>> searchCollections(
      final SearchCollectionsRequest request, final Pageable pageable) {
    // Convert enum lists to String arrays for repository query
    String[] visibilityArray = toStringArray(request.getVisibility());
    String[] collaborationModeArray = toStringArray(request.getCollaborationMode());

    // Call repository with all search parameters
    Page<RecipeCollection> collections =
        recipeCollectionRepository.searchCollections(
            request.getQuery(),
            visibilityArray,
            collaborationModeArray,
            request.getOwnerUserId(),
            request.getMinRecipeCount(),
            request.getMaxRecipeCount(),
            pageable);

    // Map entities to DTOs
    Page<CollectionDto> collectionDtos = collections.map(collectionMapper::toDto);

    return ResponseEntity.ok(collectionDtos);
  }

  /**
   * Adds a recipe to a collection. User must have edit permission on the collection.
   *
   * @param collectionId the ID of the collection to add the recipe to
   * @param recipeId the ID of the recipe to add
   * @return ResponseEntity containing the created collection item DTO with 201 Created status
   * @throws ResourceNotFoundException if collection doesn't exist
   * @throws AccessDeniedException if user doesn't have edit permission
   * @throws DuplicateResourceException if recipe is already in the collection
   */
  @Transactional
  public ResponseEntity<RecipeCollectionItemDto> addRecipeToCollection(
      final Long collectionId, final Long recipeId) {
    // Get current authenticated user ID
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Fetch the collection and verify it exists
    RecipeCollection collection =
        recipeCollectionRepository
            .findById(collectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

    // Check if user has edit permission
    checkEditPermission(collection, currentUserId);

    // Check if recipe already exists in collection
    if (recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(
        collectionId, recipeId)) {
      throw new DuplicateResourceException("Recipe is already in this collection");
    }

    // Calculate display order (max + 10, or 10 if collection is empty)
    Integer maxDisplayOrder =
        recipeCollectionItemRepository.findMaxDisplayOrderByCollectionId(collectionId);
    int newDisplayOrder =
        (maxDisplayOrder == null)
            ? DISPLAY_ORDER_INCREMENT
            : maxDisplayOrder + DISPLAY_ORDER_INCREMENT;

    // Create the collection item entity
    RecipeCollectionItemId itemId =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId).build();

    RecipeCollectionItem collectionItem =
        RecipeCollectionItem.builder()
            .id(itemId)
            .displayOrder(newDisplayOrder)
            .addedBy(currentUserId)
            .build();

    // Save the entity
    RecipeCollectionItem savedItem = recipeCollectionItemRepository.save(collectionItem);

    // Trigger async notification to recipe author
    Recipe recipe =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

    notificationService.notifyRecipeCollectedAsync(
        recipe.getUserId(), // recipe author (recipient)
        recipeId, // recipe being collected
        collectionId, // collection it's added to
        currentUserId // user who added it (collector)
        );

    // Map to DTO and return 201 Created
    RecipeCollectionItemDto responseDto = recipeCollectionItemMapper.toDto(savedItem);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  /**
   * Removes a recipe from a collection. User must have edit permission on the collection.
   *
   * <p>This method permanently deletes the recipe-to-collection association. The remaining recipes
   * in the collection maintain their display order (gaps are allowed and do not require
   * reordering).
   *
   * @param collectionId the ID of the collection to remove the recipe from
   * @param recipeId the ID of the recipe to remove
   * @return ResponseEntity with 204 No Content status
   * @throws ResourceNotFoundException if collection doesn't exist or recipe is not in collection
   * @throws AccessDeniedException if user doesn't have edit permission
   */
  @Transactional
  public ResponseEntity<Void> removeRecipeFromCollection(
      final Long collectionId, final Long recipeId) {
    // Get current authenticated user ID
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Fetch the collection and verify it exists
    RecipeCollection collection =
        recipeCollectionRepository
            .findById(collectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

    // Check if user has edit permission
    checkEditPermission(collection, currentUserId);

    // Verify recipe exists in the collection
    if (!recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(
        collectionId, recipeId)) {
      throw new ResourceNotFoundException("Recipe not found in this collection");
    }

    // Delete the recipe from the collection
    recipeCollectionItemRepository.deleteByIdCollectionIdAndIdRecipeId(collectionId, recipeId);

    // Return 204 No Content
    return ResponseEntity.noContent().build();
  }

  /**
   * Updates the display order for a single recipe in a collection. User must have edit permission
   * on the collection.
   *
   * <p>This method updates the display order of a specific recipe without affecting other recipes
   * in the collection. Gaps in display order are allowed.
   *
   * @param collectionId the ID of the collection
   * @param recipeId the ID of the recipe to update
   * @param request the update request containing the new display order
   * @return ResponseEntity containing CollectionRecipeDto with updated display order and recipe
   *     metadata
   * @throws ResourceNotFoundException if collection or recipe not found in collection
   * @throws AccessDeniedException if user doesn't have edit permission
   */
  @Transactional
  public ResponseEntity<com.recipe_manager.model.dto.collection.CollectionRecipeDto>
      updateRecipeOrder(
          final Long collectionId,
          final Long recipeId,
          final com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest request) {
    // Get current authenticated user ID
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Fetch the collection and verify it exists
    RecipeCollection collection =
        recipeCollectionRepository
            .findById(collectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

    // Check if user has edit permission
    checkEditPermission(collection, currentUserId);

    // Find the recipe collection item
    RecipeCollectionItem item =
        recipeCollectionItemRepository
            .findByIdCollectionIdAndIdRecipeId(collectionId, recipeId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found in this collection"));

    // Update display order
    item.setDisplayOrder(request.getDisplayOrder());
    recipeCollectionItemRepository.save(item);

    // Fetch the item with recipe metadata loaded
    RecipeCollectionItem updatedItem =
        recipeCollectionItemRepository.findByIdCollectionIdWithRecipe(collectionId).stream()
            .filter(i -> i.getId().getRecipeId().equals(recipeId))
            .findFirst()
            .orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found in this collection"));

    // Map to DTO and return 200 OK
    com.recipe_manager.model.dto.collection.CollectionRecipeDto responseDto =
        collectionMapper.toRecipeDto(updatedItem);

    return ResponseEntity.ok(responseDto);
  }

  /**
   * Batch reorders recipes in a collection by updating their display orders. User must have edit
   * permission on the collection.
   *
   * <p>This method:
   *
   * <ul>
   *   <li>Validates all recipes exist in the collection
   *   <li>Validates no duplicate display orders in the request
   *   <li>Updates all display orders atomically in a transaction
   *   <li>Returns updated recipes with metadata (title, description) in display order
   * </ul>
   *
   * @param collectionId the ID of the collection
   * @param request the reorder request containing recipe IDs and new display orders
   * @return ResponseEntity containing list of CollectionRecipeDto with updated orders
   * @throws ResourceNotFoundException if collection not found or any recipe not in collection
   * @throws AccessDeniedException if user lacks edit permission
   * @throws IllegalArgumentException if request contains duplicate display orders or invalid data
   */
  @Transactional
  public ResponseEntity<List<com.recipe_manager.model.dto.collection.CollectionRecipeDto>>
      reorderRecipes(
          final Long collectionId,
          final com.recipe_manager.model.dto.request.ReorderRecipesRequest request) {
    // Get current authenticated user ID
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Fetch the collection and verify it exists
    RecipeCollection collection =
        recipeCollectionRepository
            .findById(collectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

    // Check if user has edit permission
    checkEditPermission(collection, currentUserId);

    // Validate no duplicate display orders in the request
    long uniqueDisplayOrders =
        request.getRecipes().stream()
            .map(
                com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder
                    ::getDisplayOrder)
            .distinct()
            .count();

    if (uniqueDisplayOrders != request.getRecipes().size()) {
      throw new IllegalArgumentException("Request contains duplicate display orders");
    }

    // Fetch all existing items in the collection to validate recipes exist
    List<RecipeCollectionItem> existingItems =
        recipeCollectionItemRepository.findByIdCollectionId(collectionId);

    // Create a map of existing recipe IDs for quick lookup
    java.util.Set<Long> existingRecipeIds =
        existingItems.stream()
            .map(item -> item.getId().getRecipeId())
            .collect(java.util.stream.Collectors.toSet());

    // Validate all requested recipes exist in the collection
    for (RecipeOrder recipeOrder : request.getRecipes()) {
      if (!existingRecipeIds.contains(recipeOrder.getRecipeId())) {
        throw new ResourceNotFoundException(
            "Recipe with ID " + recipeOrder.getRecipeId() + " not found in this collection");
      }
    }

    // Update display orders for all recipes in the request
    for (RecipeOrder recipeOrder : request.getRecipes()) {
      RecipeCollectionItem item =
          recipeCollectionItemRepository
              .findByIdCollectionIdAndIdRecipeId(collectionId, recipeOrder.getRecipeId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Recipe " + recipeOrder.getRecipeId() + " not found in collection"));
      item.setDisplayOrder(recipeOrder.getDisplayOrder());
      recipeCollectionItemRepository.save(item);
    }

    // Fetch all items with recipe metadata and return in display order
    List<RecipeCollectionItem> updatedItems =
        recipeCollectionItemRepository.findByIdCollectionIdWithRecipe(collectionId);

    // Map to DTOs
    List<com.recipe_manager.model.dto.collection.CollectionRecipeDto> responseDtos =
        updatedItems.stream()
            .map(collectionMapper::toRecipeDto)
            .collect(java.util.stream.Collectors.toList());

    return ResponseEntity.ok(responseDtos);
  }

  /**
   * Retrieves all collaborators for a specific collection.
   *
   * <p>This endpoint is only available for collections with SPECIFIC_USERS collaboration mode. User
   * must have view permission for the collection.
   *
   * @param collectionId the ID of the collection
   * @return ResponseEntity containing a list of collection collaborators with usernames
   * @throws ResourceNotFoundException if collection is not found
   * @throws AccessDeniedException if user lacks view permission or collection doesn't use
   *     SPECIFIC_USERS mode
   */
  @Transactional(readOnly = true)
  public ResponseEntity<List<CollectionCollaboratorDto>> getCollaborators(final Long collectionId) {
    // Get current authenticated user ID
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Fetch the collection and verify it exists
    RecipeCollection collection =
        recipeCollectionRepository
            .findById(collectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

    // Check if user has view permission
    verifyViewPermission(collection, currentUserId);

    // Verify collection uses SPECIFIC_USERS collaboration mode
    if (collection.getCollaborationMode() != CollaborationMode.SPECIFIC_USERS) {
      throw new AccessDeniedException("Collection doesn't use SPECIFIC_USERS collaboration mode");
    }

    // Fetch collaborators with usernames ordered by granted_at DESC
    List<Object[]> collaboratorRows =
        collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(collectionId);

    // Convert Object[] rows to DTOs
    // Row format: collection_id, user_id, username, granted_by,
    // granted_by_username, granted_at
    List<CollectionCollaboratorDto> collaborators =
        collaboratorRows.stream()
            .map(
                row ->
                    CollectionCollaboratorDto.builder()
                        .collectionId((Long) row[0])
                        .userId((UUID) row[1])
                        .username((String) row[2])
                        .grantedBy((UUID) row[GRANTED_BY_INDEX])
                        .grantedByUsername((String) row[GRANTED_BY_USERNAME_INDEX])
                        .grantedAt(((java.sql.Timestamp) row[GRANTED_AT_INDEX]).toLocalDateTime())
                        .build())
            .collect(java.util.stream.Collectors.toList());

    return ResponseEntity.ok(collaborators);
  }

  /**
   * Adds a collaborator to a collection.
   *
   * <p>Only the collection owner can add collaborators. The collection must use SPECIFIC_USERS
   * collaboration mode.
   *
   * @param collectionId the ID of the collection
   * @param request the request containing the user ID to add
   * @return ResponseEntity with the newly added collaborator details and 201 status
   * @throws ResourceNotFoundException if collection or user not found
   * @throws AccessDeniedException if user is not the owner or wrong collaboration mode
   * @throws DuplicateResourceException if user is already a collaborator or is the owner
   */
  @Transactional
  public ResponseEntity<CollectionCollaboratorDto> addCollaborator(
      final Long collectionId,
      final com.recipe_manager.model.dto.request.AddCollaboratorRequest request) {
    // Get current authenticated user ID
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Fetch the collection and verify it exists
    RecipeCollection collection =
        recipeCollectionRepository
            .findById(collectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

    // Verify current user is the collection owner
    if (!collection.getUserId().equals(currentUserId)) {
      throw new AccessDeniedException("Only the collection owner can add collaborators");
    }

    // Verify collection uses SPECIFIC_USERS collaboration mode
    if (collection.getCollaborationMode() != CollaborationMode.SPECIFIC_USERS) {
      throw new AccessDeniedException(
          "Can only add collaborators to collections with SPECIFIC_USERS mode");
    }

    UUID targetUserId = request.getUserId();

    // Prevent owner from being added as collaborator
    if (collection.getUserId().equals(targetUserId)) {
      throw new DuplicateResourceException("Collection owner cannot be added as a collaborator");
    }

    // Check if user is already a collaborator
    if (collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
        collectionId, targetUserId)) {
      throw new DuplicateResourceException("User is already a collaborator on this collection");
    }

    // Create new collaborator entity
    CollectionCollaboratorId collaboratorId =
        new CollectionCollaboratorId(collectionId, targetUserId);
    CollectionCollaborator collaborator =
        CollectionCollaborator.builder()
            .id(collaboratorId)
            .collection(collection)
            .grantedBy(currentUserId)
            .build(); // grantedAt will be set by @CreationTimestamp

    // Save the collaborator
    try {
      collectionCollaboratorRepository.save(collaborator);

      // Fetch with usernames using the native query
      List<Object[]> collaboratorRows =
          collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
              collectionId);

      // Find the newly added collaborator in the results
      CollectionCollaboratorDto dto =
          collaboratorRows.stream()
              .filter(row -> ((UUID) row[1]).equals(targetUserId))
              .map(
                  row ->
                      CollectionCollaboratorDto.builder()
                          .collectionId((Long) row[0])
                          .userId((UUID) row[1])
                          .username((String) row[2])
                          .grantedBy((UUID) row[GRANTED_BY_INDEX])
                          .grantedByUsername((String) row[GRANTED_BY_USERNAME_INDEX])
                          .grantedAt(((java.sql.Timestamp) row[GRANTED_AT_INDEX]).toLocalDateTime())
                          .build())
              .findFirst()
              .orElseThrow(
                  () -> new RuntimeException("Failed to retrieve newly added collaborator"));

      return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      // Handle FK constraint violation (user doesn't exist)
      throw new ResourceNotFoundException("User with ID " + targetUserId + " not found");
    }
  }

  /**
   * Removes a collaborator from a collection.
   *
   * <p>Only the collection owner can remove collaborators. This endpoint removes a specific user
   * from the collection's collaborator list. No collaboration mode check is required - the owner
   * can always remove collaborators regardless of the collection's collaboration mode.
   *
   * @param collectionId the ID of the collection
   * @param userId the ID of the user to remove as collaborator
   * @return ResponseEntity with 204 No Content status
   * @throws ResourceNotFoundException if the collection is not found (404)
   * @throws ResourceNotFoundException if the collaborator is not found in the collection (404)
   * @throws AccessDeniedException if the current user is not the collection owner (403)
   */
  @Transactional
  public ResponseEntity<Void> removeCollaborator(final Long collectionId, final UUID userId) {
    // Get current authenticated user ID
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Fetch the collection and verify it exists
    RecipeCollection collection =
        recipeCollectionRepository
            .findById(collectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

    // Verify current user is the collection owner
    if (!collection.getUserId().equals(currentUserId)) {
      throw new AccessDeniedException("Only the collection owner can remove collaborators");
    }

    // Check if the collaborator exists in the collection
    boolean collaboratorExists =
        collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(collectionId, userId);

    if (!collaboratorExists) {
      throw new ResourceNotFoundException(
          "User with ID " + userId + " is not a collaborator on this collection");
    }

    // Delete the collaborator
    collectionCollaboratorRepository.deleteByIdCollectionIdAndIdUserId(collectionId, userId);

    return ResponseEntity.noContent().build();
  }

  /**
   * Verifies that the given user has view permission for the collection.
   *
   * <p>View permission is granted if:
   *
   * <ul>
   *   <li>User is the collection owner, OR
   *   <li>User is a collaborator, OR
   *   <li>Collection visibility is PUBLIC
   * </ul>
   *
   * @param collection the collection to check permission for
   * @param userId the user ID to check
   * @throws AccessDeniedException if user doesn't have view permission
   */
  private void verifyViewPermission(final RecipeCollection collection, final UUID userId) {
    // Owner always has view permission
    if (collection.getUserId().equals(userId)) {
      return;
    }

    // Check if user is a collaborator
    boolean isCollaborator =
        collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collection.getCollectionId(), userId);
    if (isCollaborator) {
      return;
    }

    // Check visibility
    if (collection.getVisibility() == com.recipe_manager.model.enums.CollectionVisibility.PUBLIC) {
      return;
    }

    // No view permission found
    throw new AccessDeniedException("User doesn't have view permission for this collection");
  }

  /**
   * Checks if the given user has edit permission on the collection.
   *
   * <p>Edit permission is granted if:
   *
   * <ul>
   *   <li>User is the collection owner, OR
   *   <li>Collection collaboration mode is ALL_USERS, OR
   *   <li>Collection collaboration mode is SPECIFIC_USERS and user is a collaborator
   * </ul>
   *
   * @param collection the collection to check permission for
   * @param userId the user ID to check
   * @throws AccessDeniedException if user doesn't have edit permission
   */
  private void checkEditPermission(final RecipeCollection collection, final UUID userId) {
    // Owner always has edit permission
    if (collection.getUserId().equals(userId)) {
      return;
    }

    // Check collaboration mode
    CollaborationMode mode = collection.getCollaborationMode();

    if (mode == CollaborationMode.ALL_USERS) {
      // All authenticated users can edit
      return;
    }

    if (mode == CollaborationMode.SPECIFIC_USERS) {
      // Check if user is a collaborator
      boolean isCollaborator =
          collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
              collection.getCollectionId(), userId);
      if (isCollaborator) {
        return;
      }
    }

    // No permission found
    throw new AccessDeniedException("User doesn't have edit permission for this collection");
  }

  /**
   * Converts a list of enums to a String array for use in native queries.
   *
   * @param enumList the list of enums to convert
   * @return String array of enum names, or empty array if list is null or empty
   */
  private String[] toStringArray(final List<? extends Enum<?>> enumList) {
    if (enumList == null || enumList.isEmpty()) {
      return new String[0];
    }
    return enumList.stream().map(Enum::name).toArray(String[]::new);
  }
}
