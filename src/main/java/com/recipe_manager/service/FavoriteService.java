package com.recipe_manager.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipe_manager.client.usermanagement.UserManagementClient;
import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.external.usermanagement.GetFollowersResponseDto;
import com.recipe_manager.model.dto.external.usermanagement.UserPreferenceResponseDto;
import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.recipe.RecipeFavoriteDto;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeFavorite;
import com.recipe_manager.model.entity.recipe.RecipeFavoriteId;
import com.recipe_manager.model.enums.ProfileVisibilityEnum;
import com.recipe_manager.model.mapper.RecipeFavoriteMapper;
import com.recipe_manager.model.mapper.RecipeMapper;
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

  /** Mapper for recipe favorites. */
  private final RecipeFavoriteMapper recipeFavoriteMapper;

  /** Mapper for recipes. */
  private final RecipeMapper recipeMapper;

  /** Client for user management service. */
  private final UserManagementClient userManagementClient;

  /**
   * Constructs the service with required dependencies.
   *
   * @param recipeFavoriteRepository repository for recipe favorites
   * @param recipeRepository repository for recipes
   * @param recipeFavoriteMapper mapper for recipe favorites
   * @param recipeMapper mapper for recipes
   * @param userManagementClient client for user management service
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "All dependencies are Spring-managed beans injected via constructor")
  public FavoriteService(
      final RecipeFavoriteRepository recipeFavoriteRepository,
      final RecipeRepository recipeRepository,
      final RecipeFavoriteMapper recipeFavoriteMapper,
      final RecipeMapper recipeMapper,
      final UserManagementClient userManagementClient) {
    this.recipeFavoriteRepository = recipeFavoriteRepository;
    this.recipeRepository = recipeRepository;
    this.recipeFavoriteMapper = recipeFavoriteMapper;
    this.recipeMapper = recipeMapper;
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
    UserPreferenceResponseDto preferences = userManagementClient.getUserPreferences();

    // Extract profile visibility
    ProfileVisibilityEnum visibility =
        preferences.getPreferences().getPrivacyPreferences().getProfileVisibility();

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
}
