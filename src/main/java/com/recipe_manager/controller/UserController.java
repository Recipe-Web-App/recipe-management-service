package com.recipe_manager.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recipe_manager.model.dto.response.SearchRecipesResponse;
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

  /**
   * Constructs the controller with required services.
   *
   * @param recipeService the recipe service
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Spring-managed beans are safe to inject and not exposed externally")
  public UserController(final RecipeService recipeService) {
    this.recipeService = recipeService;
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
}
