package com.recipe_manager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.service.CollectionService;
import com.recipe_manager.service.RecipeService;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * REST controller for user-scoped endpoints.
 *
 * <p>Provides endpoints for accessing authenticated user's own resources.
 */
@RestController
@RequestMapping("/users/me")
public class UserController {

  /** Default page size for pagination. */
  private static final int DEFAULT_PAGE_SIZE = 20;

  /** Service for recipe operations. */
  private final RecipeService recipeService;

  /** Service for collection operations. */
  private final CollectionService collectionService;

  /**
   * Constructs the controller with required services.
   *
   * @param recipeService the recipe service
   * @param collectionService the collection service
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Spring-managed beans are safe to inject and not exposed externally")
  public UserController(
      final RecipeService recipeService, final CollectionService collectionService) {
    this.recipeService = recipeService;
    this.collectionService = collectionService;
  }

  /**
   * Get all recipes owned by the authenticated user.
   *
   * <p>Retrieves all recipes created by the authenticated user with pagination support. Returns
   * recipes in the same format as the main recipes list endpoint.
   *
   * @param pageable pagination parameters
   * @return ResponseEntity with paginated list of user's recipes
   */
  @GetMapping(value = "/recipes", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SearchRecipesResponse> getMyRecipes(
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
    return recipeService.getMyRecipes(pageable);
  }

  /**
   * Get all collections for the authenticated user.
   *
   * <p>Retrieves collections associated with the authenticated user with pagination support. By
   * default, returns only collections owned by the user. Use the includeCollaborations parameter to
   * also include collections where the user is defined as a collaborator.
   *
   * @param includeCollaborations if true, includes collections where user is a collaborator
   * @param pageable pagination parameters
   * @return ResponseEntity with paginated list of user's collections
   */
  @GetMapping(value = "/collections", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<CollectionDto>> getMyCollections(
      @RequestParam(defaultValue = "false") final boolean includeCollaborations,
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
    return collectionService.getMyCollections(includeCollaborations, pageable);
  }
}
