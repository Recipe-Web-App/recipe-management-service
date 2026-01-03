package com.recipe_manager.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipe_manager.client.usermanagement.UserManagementClient;
import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.collection.CollectionFavoriteDto;
import com.recipe_manager.model.dto.external.usermanagement.GetFollowersResponseDto;
import com.recipe_manager.model.dto.external.usermanagement.UserPreferencesDto;
import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.recipe.RecipeFavoriteDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.model.entity.collection.CollectionFavorite;
import com.recipe_manager.model.entity.collection.CollectionFavoriteId;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeFavorite;
import com.recipe_manager.model.entity.recipe.RecipeFavoriteId;
import com.recipe_manager.model.enums.ProfileVisibilityEnum;
import com.recipe_manager.model.mapper.CollectionFavoriteMapper;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeFavoriteMapper;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.repository.collection.CollectionFavoriteRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.repository.recipe.RecipeFavoriteRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.util.SecurityUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Service for managing recipe favorites.
 *
 * <p>Handles favorite operations including adding, removing, and retrieving user favorites with
 * privacy controls. Integrates with user-management-service to enforce privacy settings.
 *
 * <p>Privacy Rules:
 *
 * <ul>
 *   <li>Users can always view their own favorites
 *   <li>PUBLIC profiles: Anyone can view favorites
 *   <li>FRIENDS_ONLY profiles: Only followers can view favorites
 *   <li>PRIVATE profiles: Only the user themselves can view favorites
 * </ul>
 */
@Service
public class FavoriteService {

  /** Repository for recipe favorites. */
  private final RecipeFavoriteRepository recipeFavoriteRepository;

  /** Repository for recipes. */
  private final RecipeRepository recipeRepository;

  /** Repository for collection favorites. */
  private final CollectionFavoriteRepository collectionFavoriteRepository;

  /** Repository for recipe collections. */
  private final RecipeCollectionRepository recipeCollectionRepository;

  /** Mapper for recipe favorites. */
  private final RecipeFavoriteMapper recipeFavoriteMapper;

  /** Mapper for recipes. */
  private final RecipeMapper recipeMapper;

  /** Mapper for collection favorites. */
  private final CollectionFavoriteMapper collectionFavoriteMapper;

  /** Mapper for collections. */
  private final CollectionMapper collectionMapper;

  /** Client for user management service. */
  private final UserManagementClient userManagementClient;

  /**
   * Constructs the service with required dependencies.
   *
   * @param recipeFavoriteRepository repository for recipe favorites
   * @param recipeRepository repository for recipes
   * @param collectionFavoriteRepository repository for collection favorites
   * @param recipeCollectionRepository repository for recipe collections
   * @param recipeFavoriteMapper mapper for recipe favorites
   * @param recipeMapper mapper for recipes
   * @param collectionFavoriteMapper mapper for collection favorites
   * @param collectionMapper mapper for collections
   * @param userManagementClient client for user management service
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "All dependencies are Spring-managed beans injected via constructor")
  public FavoriteService(
      final RecipeFavoriteRepository recipeFavoriteRepository,
      final RecipeRepository recipeRepository,
      final CollectionFavoriteRepository collectionFavoriteRepository,
      final RecipeCollectionRepository recipeCollectionRepository,
      final RecipeFavoriteMapper recipeFavoriteMapper,
      final RecipeMapper recipeMapper,
      final CollectionFavoriteMapper collectionFavoriteMapper,
      final CollectionMapper collectionMapper,
      final UserManagementClient userManagementClient) {
    this.recipeFavoriteRepository = recipeFavoriteRepository;
    this.recipeRepository = recipeRepository;
    this.collectionFavoriteRepository = collectionFavoriteRepository;
    this.recipeCollectionRepository = recipeCollectionRepository;
    this.recipeFavoriteMapper = recipeFavoriteMapper;
    this.recipeMapper = recipeMapper;
    this.collectionFavoriteMapper = collectionFavoriteMapper;
    this.collectionMapper = collectionMapper;
    this.userManagementClient = userManagementClient;
  }

  /**
   * Get favorited recipes for a user with privacy controls.
   *
   * <p>If userId is null, returns favorites for the authenticated user. If requesting another
   * user's favorites, privacy preferences are checked via user-management-service.
   *
   * @param userId the user ID whose favorites to retrieve (null for authenticated user)
   * @param pageable pagination parameters
   * @return ResponseEntity with paginated search response containing favorite recipes
   * @throws AccessDeniedException if the requesting user is not authorized to view the favorites
   */
  public ResponseEntity<SearchRecipesResponse> getUserFavorites(
      final UUID userId, final Pageable pageable) throws AccessDeniedException {

    final UUID authenticatedUserId = SecurityUtils.getCurrentUserId();
    final UUID targetUserId = (userId != null) ? userId : authenticatedUserId;

    // Check privacy if requesting another user's favorites
    if (!targetUserId.equals(authenticatedUserId)) {
      checkPrivacyAuthorization(targetUserId, authenticatedUserId);
    }

    // Fetch favorites with recipe details (using JOIN FETCH to avoid N+1)
    Page<RecipeFavorite> favoritesPage =
        recipeFavoriteRepository.findByUserIdWithRecipe(targetUserId, pageable);

    // Convert to Recipe DTOs
    List<RecipeDto> recipeDtos =
        favoritesPage.getContent().stream()
            .map(RecipeFavorite::getRecipe)
            .map(recipeMapper::toDto)
            .toList();

    // Build response matching SearchRecipesResponse format
    SearchRecipesResponse response =
        SearchRecipesResponse.builder()
            .recipes(recipeDtos)
            .page(favoritesPage.getNumber())
            .size(favoritesPage.getSize())
            .totalElements(favoritesPage.getTotalElements())
            .totalPages(favoritesPage.getTotalPages())
            .first(favoritesPage.isFirst())
            .last(favoritesPage.isLast())
            .numberOfElements(favoritesPage.getNumberOfElements())
            .empty(favoritesPage.isEmpty())
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Add a recipe to the authenticated user's favorites.
   *
   * @param recipeId the ID of the recipe to favorite
   * @return ResponseEntity with the created favorite DTO and 201 Created status
   * @throws BusinessException if the recipe is already favorited
   * @throws ResourceNotFoundException if the recipe does not exist
   */
  @Transactional
  public ResponseEntity<RecipeFavoriteDto> addFavorite(final Long recipeId) {
    final UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Check if already favorited
    if (recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(currentUserId, recipeId)) {
      throw new BusinessException("User has already favorited this recipe");
    }

    // Verify recipe exists
    Recipe recipe =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with ID: " + recipeId));

    // Create composite key
    RecipeFavoriteId id =
        RecipeFavoriteId.builder().userId(currentUserId).recipeId(recipeId).build();

    // Create favorite entity
    RecipeFavorite favorite = RecipeFavorite.builder().id(id).recipe(recipe).build();

    // Save and return DTO
    RecipeFavorite savedFavorite = recipeFavoriteRepository.save(favorite);
    RecipeFavoriteDto dto = recipeFavoriteMapper.toDto(savedFavorite);

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  /**
   * Remove a recipe from the authenticated user's favorites.
   *
   * @param recipeId the ID of the recipe to unfavorite
   * @return ResponseEntity with 204 No Content status
   * @throws ResourceNotFoundException if the favorite does not exist
   */
  @Transactional
  public ResponseEntity<Void> removeFavorite(final Long recipeId) {
    final UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Verify favorite exists
    if (!recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(currentUserId, recipeId)) {
      throw new ResourceNotFoundException("Favorite not found for this user and recipe");
    }

    // Delete favorite
    recipeFavoriteRepository.deleteByIdUserIdAndIdRecipeId(currentUserId, recipeId);

    return ResponseEntity.noContent().build();
  }

  /**
   * Check if the authenticated user has favorited a specific recipe.
   *
   * @param recipeId the ID of the recipe to check
   * @return ResponseEntity with boolean indicating if the recipe is favorited
   */
  public ResponseEntity<Boolean> isFavorited(final Long recipeId) {
    final UUID currentUserId = SecurityUtils.getCurrentUserId();
    boolean favorited =
        recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(currentUserId, recipeId);
    return ResponseEntity.ok(favorited);
  }

  /**
   * Check privacy authorization for viewing another user's favorites.
   *
   * <p>Calls user-management-service to get privacy preferences and enforces access control based
   * on profile visibility settings.
   *
   * @param targetUserId the user whose favorites are being requested
   * @param requestingUserId the user making the request
   * @throws AccessDeniedException if the requesting user is not authorized
   */
  private void checkPrivacyAuthorization(final UUID targetUserId, final UUID requestingUserId)
      throws AccessDeniedException {

    // Get user preferences from user-management-service
    UserPreferencesDto preferences = userManagementClient.getUserPreferences(targetUserId);

    // Extract profile visibility
    ProfileVisibilityEnum visibility = preferences.getPrivacy().getProfileVisibility();

    // PUBLIC: Anyone can view
    if (visibility == ProfileVisibilityEnum.PUBLIC) {
      return;
    }

    // PRIVATE: Only the user themselves can view
    if (visibility == ProfileVisibilityEnum.PRIVATE) {
      throw new AccessDeniedException(
          "User's favorites are private. Only the user can view their own favorites.");
    }

    // FRIENDS_ONLY: Check if requesting user follows target user
    if (visibility == ProfileVisibilityEnum.FRIENDS_ONLY) {
      GetFollowersResponseDto followers =
          userManagementClient.getFollowers(targetUserId, null, null, false);

      boolean isFollowing =
          followers.getFollowedUsers().stream()
              .anyMatch(user -> user.getUserId().equals(requestingUserId));

      if (!isFollowing) {
        throw new AccessDeniedException(
            "User's favorites are private. Only followers can view them.");
      }
    }
  }

  // ==================== Collection Favorites ====================

  /**
   * Get favorited collections for a user with privacy controls.
   *
   * <p>If userId is null, returns favorites for the authenticated user. If requesting another
   * user's favorites, privacy preferences are checked via user-management-service.
   *
   * @param userId the user ID whose favorites to retrieve (null for authenticated user)
   * @param pageable pagination parameters
   * @return ResponseEntity with paginated collection response containing favorite collections
   * @throws AccessDeniedException if the requesting user is not authorized to view the favorites
   */
  public ResponseEntity<Page<CollectionDto>> getFavoriteCollections(
      final UUID userId, final Pageable pageable) throws AccessDeniedException {

    final UUID authenticatedUserId = SecurityUtils.getCurrentUserId();
    final UUID targetUserId = (userId != null) ? userId : authenticatedUserId;

    // Check privacy if requesting another user's favorites
    if (!targetUserId.equals(authenticatedUserId)) {
      checkPrivacyAuthorization(targetUserId, authenticatedUserId);
    }

    // Fetch favorites with collection details (using JOIN FETCH to avoid N+1)
    Page<CollectionFavorite> favoritesPage =
        collectionFavoriteRepository.findByUserIdWithCollection(targetUserId, pageable);

    // Convert to Collection DTOs
    List<CollectionDto> collectionDtos =
        favoritesPage.getContent().stream()
            .map(CollectionFavorite::getCollection)
            .map(collectionMapper::toDto)
            .toList();

    // Build paginated response
    Page<CollectionDto> response =
        new PageImpl<>(collectionDtos, pageable, favoritesPage.getTotalElements());

    return ResponseEntity.ok(response);
  }

  /**
   * Add a collection to the authenticated user's favorites.
   *
   * <p>Validates that the collection exists and that the user has view access to it. Users can only
   * favorite collections they can view (PUBLIC collections or collections where they have access).
   *
   * @param collectionId the ID of the collection to favorite
   * @return ResponseEntity with the created favorite DTO and 201 Created status
   * @throws BusinessException if the collection is already favorited
   * @throws ResourceNotFoundException if the collection does not exist
   * @throws AccessDeniedException if the user does not have view access to the collection
   */
  @Transactional
  public ResponseEntity<CollectionFavoriteDto> favoriteCollection(final Long collectionId)
      throws AccessDeniedException {
    final UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Check if already favorited
    if (collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
        currentUserId, collectionId)) {
      throw new BusinessException("User has already favorited this collection");
    }

    // Verify collection exists
    RecipeCollection collection =
        recipeCollectionRepository
            .findById(collectionId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Collection not found with ID: " + collectionId));

    // Verify user has view access to the collection
    if (!recipeCollectionRepository.hasViewAccess(collectionId, currentUserId)) {
      throw new AccessDeniedException("You do not have access to view this collection");
    }

    // Create composite key
    CollectionFavoriteId id =
        CollectionFavoriteId.builder().userId(currentUserId).collectionId(collectionId).build();

    // Create favorite entity
    CollectionFavorite favorite =
        CollectionFavorite.builder().id(id).collection(collection).build();

    // Save and return DTO
    CollectionFavorite savedFavorite = collectionFavoriteRepository.save(favorite);
    CollectionFavoriteDto dto = collectionFavoriteMapper.toDto(savedFavorite);

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  /**
   * Remove a collection from the authenticated user's favorites.
   *
   * @param collectionId the ID of the collection to unfavorite
   * @return ResponseEntity with 204 No Content status
   * @throws ResourceNotFoundException if the favorite does not exist
   */
  @Transactional
  public ResponseEntity<Void> unfavoriteCollection(final Long collectionId) {
    final UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Verify favorite exists
    if (!collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
        currentUserId, collectionId)) {
      throw new ResourceNotFoundException("Favorite not found for this user and collection");
    }

    // Delete favorite
    collectionFavoriteRepository.deleteByIdUserIdAndIdCollectionId(currentUserId, collectionId);

    return ResponseEntity.noContent().build();
  }

  /**
   * Check if the authenticated user has favorited a specific collection.
   *
   * @param collectionId the ID of the collection to check
   * @return ResponseEntity with boolean indicating if the collection is favorited
   */
  public ResponseEntity<Boolean> isCollectionFavorited(final Long collectionId) {
    final UUID currentUserId = SecurityUtils.getCurrentUserId();
    boolean favorited =
        collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(currentUserId, collectionId);
    return ResponseEntity.ok(favorited);
  }
}
