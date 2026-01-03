package com.recipe_manager.controller;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.recipe_manager.model.dto.collection.CollectionFavoriteDto;
import com.recipe_manager.model.dto.recipe.RecipeFavoriteDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.service.FavoriteService;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * REST controller for Recipe Favorites API endpoints.
 *
 * <p>Provides endpoints for managing user recipe favorites, including retrieving favorites with
 * privacy controls, adding recipes to favorites, and removing favorites.
 *
 * <p>Privacy is enforced at the service layer based on user preferences from the
 * user-management-service:
 *
 * <ul>
 *   <li>PUBLIC profiles: Anyone can view favorites
 *   <li>FRIENDS_ONLY profiles: Only followers can view favorites
 *   <li>PRIVATE profiles: Only the user themselves can view favorites
 * </ul>
 */
@RestController
@RequestMapping("/favorites")
public class FavoriteController {

  /** Default page size for pagination. */
  private static final int DEFAULT_PAGE_SIZE = 20;

  /** Service for favorite operations. */
  private final FavoriteService favoriteService;

  /**
   * Constructs the controller with required services.
   *
   * @param favoriteService the favorite service
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "FavoriteService is a Spring-managed bean injected via constructor")
  public FavoriteController(final FavoriteService favoriteService) {
    this.favoriteService = favoriteService;
  }

  /**
   * Get favorited recipes for a user with privacy controls.
   *
   * <p>If userId is null or not provided, returns favorites for the authenticated user. If
   * requesting another user's favorites, privacy preferences are checked via
   * user-management-service.
   *
   * <p>Privacy enforcement:
   *
   * <ul>
   *   <li>Users can always view their own favorites (no userId or userId matches authenticated
   *       user)
   *   <li>PUBLIC profiles: Anyone can view favorites
   *   <li>FRIENDS_ONLY profiles: Only followers can view favorites (403 if not following)
   *   <li>PRIVATE profiles: Only the user themselves can view favorites (403 for all others)
   * </ul>
   *
   * @param userId the user ID whose favorites to retrieve (optional, defaults to authenticated
   *     user)
   * @param pageable pagination parameters (page, size, sort)
   * @return ResponseEntity with paginated search response containing favorite recipes
   * @throws AccessDeniedException if the requesting user is not authorized to view the favorites
   *     (403)
   */
  @GetMapping(value = "/recipes", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SearchRecipesResponse> getUserFavorites(
      @RequestParam(value = "userId", required = false) final UUID userId,
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable)
      throws AccessDeniedException {
    return favoriteService.getUserFavorites(userId, pageable);
  }

  /**
   * Add a recipe to the authenticated user's favorites.
   *
   * <p>Creates a new favorite association between the authenticated user and the specified recipe.
   * If the user has already favorited this recipe, returns 400 Bad Request.
   *
   * @param recipeId the ID of the recipe to favorite
   * @return ResponseEntity with the created favorite DTO and 201 Created status
   * @throws com.recipe_manager.exception.BusinessException if the recipe is already favorited (400)
   * @throws com.recipe_manager.exception.ResourceNotFoundException if the recipe does not exist
   *     (404)
   */
  @PostMapping(value = "/recipes/{recipeId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RecipeFavoriteDto> addFavorite(@PathVariable final Long recipeId) {
    return favoriteService.addFavorite(recipeId);
  }

  /**
   * Remove a recipe from the authenticated user's favorites.
   *
   * <p>Deletes the favorite association between the authenticated user and the specified recipe. If
   * the favorite does not exist, returns 404 Not Found.
   *
   * @param recipeId the ID of the recipe to unfavorite
   * @return ResponseEntity with 204 No Content status
   * @throws com.recipe_manager.exception.ResourceNotFoundException if the favorite does not exist
   *     (404)
   */
  @DeleteMapping("/recipes/{recipeId}")
  public ResponseEntity<Void> removeFavorite(@PathVariable final Long recipeId) {
    return favoriteService.removeFavorite(recipeId);
  }

  /**
   * Check if the authenticated user has favorited a specific recipe.
   *
   * <p>Returns a boolean indicating whether the authenticated user has the specified recipe in
   * their favorites.
   *
   * @param recipeId the ID of the recipe to check
   * @return ResponseEntity with boolean indicating if the recipe is favorited
   */
  @GetMapping(
      value = "/recipes/{recipeId}/is-favorited",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Boolean> isFavorited(@PathVariable final Long recipeId) {
    return favoriteService.isFavorited(recipeId);
  }

  // ==================== Collection Favorites ====================

  /**
   * Get favorited collections for a user with privacy controls.
   *
   * <p>If userId is null or not provided, returns favorites for the authenticated user. If
   * requesting another user's favorites, privacy preferences are checked via
   * user-management-service.
   *
   * <p>Privacy enforcement:
   *
   * <ul>
   *   <li>Users can always view their own favorites (no userId or userId matches authenticated
   *       user)
   *   <li>PUBLIC profiles: Anyone can view favorites
   *   <li>FRIENDS_ONLY profiles: Only followers can view favorites (403 if not following)
   *   <li>PRIVATE profiles: Only the user themselves can view favorites (403 for all others)
   * </ul>
   *
   * @param userId the user ID whose favorites to retrieve (optional, defaults to authenticated
   *     user)
   * @param pageable pagination parameters (page, size, sort)
   * @return ResponseEntity with paginated collection response containing favorite collections
   * @throws AccessDeniedException if the requesting user is not authorized to view the favorites
   *     (403)
   */
  @GetMapping(value = "/collections", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<CollectionDto>> getFavoriteCollections(
      @RequestParam(value = "userId", required = false) final UUID userId,
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable)
      throws AccessDeniedException {
    // TODO: Implement service call
    return ResponseEntity.ok(Page.empty());
  }

  /**
   * Add a collection to the authenticated user's favorites.
   *
   * <p>Creates a new favorite association between the authenticated user and the specified
   * collection. If the user has already favorited this collection, returns 409 Conflict. Users can
   * only favorite collections they have access to view.
   *
   * @param collectionId the ID of the collection to favorite
   * @return ResponseEntity with the created favorite DTO and 201 Created status
   * @throws com.recipe_manager.exception.BusinessException if the collection is already favorited
   *     (409)
   * @throws com.recipe_manager.exception.ResourceNotFoundException if the collection does not exist
   *     (404)
   * @throws AccessDeniedException if the user does not have access to view the collection (403)
   */
  @PostMapping(value = "/collections/{collectionId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CollectionFavoriteDto> favoriteCollection(
      @PathVariable final Long collectionId) {
    // TODO: Implement service call
    return ResponseEntity.status(HttpStatus.CREATED).body(null);
  }

  /**
   * Remove a collection from the authenticated user's favorites.
   *
   * <p>Deletes the favorite association between the authenticated user and the specified
   * collection. If the favorite does not exist, returns 404 Not Found.
   *
   * @param collectionId the ID of the collection to unfavorite
   * @return ResponseEntity with 204 No Content status
   * @throws com.recipe_manager.exception.ResourceNotFoundException if the favorite does not exist
   *     (404)
   */
  @DeleteMapping("/collections/{collectionId}")
  public ResponseEntity<Void> unfavoriteCollection(@PathVariable final Long collectionId) {
    // TODO: Implement service call
    return ResponseEntity.noContent().build();
  }

  /**
   * Check if the authenticated user has favorited a specific collection.
   *
   * <p>Returns a boolean indicating whether the authenticated user has the specified collection in
   * their favorites.
   *
   * @param collectionId the ID of the collection to check
   * @return ResponseEntity with boolean indicating if the collection is favorited
   */
  @GetMapping(
      value = "/collections/{collectionId}/is-favorited",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Boolean> isCollectionFavorited(@PathVariable final Long collectionId) {
    // TODO: Implement service call
    return ResponseEntity.ok(false);
  }
}
