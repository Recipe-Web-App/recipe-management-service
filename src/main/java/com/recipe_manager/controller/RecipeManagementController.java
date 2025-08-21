package com.recipe_manager.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.recipe.StepCommentDto;
import com.recipe_manager.model.dto.request.AddIngredientCommentRequest;
import com.recipe_manager.model.dto.request.AddReviewRequest;
import com.recipe_manager.model.dto.request.AddStepCommentRequest;
import com.recipe_manager.model.dto.request.AddTagRequest;
import com.recipe_manager.model.dto.request.CreateRecipeRequest;
import com.recipe_manager.model.dto.request.DeleteIngredientCommentRequest;
import com.recipe_manager.model.dto.request.DeleteStepCommentRequest;
import com.recipe_manager.model.dto.request.EditIngredientCommentRequest;
import com.recipe_manager.model.dto.request.EditReviewRequest;
import com.recipe_manager.model.dto.request.EditStepCommentRequest;
import com.recipe_manager.model.dto.request.RemoveTagRequest;
import com.recipe_manager.model.dto.request.SearchRecipesRequest;
import com.recipe_manager.model.dto.request.UpdateRecipeRequest;
import com.recipe_manager.model.dto.response.IngredientCommentResponse;
import com.recipe_manager.model.dto.response.IngredientRevisionsResponse;
import com.recipe_manager.model.dto.response.RecipeIngredientsResponse;
import com.recipe_manager.model.dto.response.RecipeRevisionsResponse;
import com.recipe_manager.model.dto.response.ReviewResponse;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.model.dto.response.ShoppingListResponse;
import com.recipe_manager.model.dto.response.StepCommentResponse;
import com.recipe_manager.model.dto.response.StepResponse;
import com.recipe_manager.model.dto.response.StepRevisionsResponse;
import com.recipe_manager.model.dto.response.TagResponse;
import com.recipe_manager.model.dto.review.ReviewDto;
import com.recipe_manager.service.IngredientService;
import com.recipe_manager.service.MediaService;
import com.recipe_manager.service.RecipeService;
import com.recipe_manager.service.ReviewService;
import com.recipe_manager.service.StepService;
import com.recipe_manager.service.TagService;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/**
 * REST controller for Recipe Management API endpoints.
 *
 * <p>All methods are placeholders and should be implemented.
 */
@RestController
@RequestMapping("/recipe-management/recipes")
public class RecipeManagementController {

  /** Default page size for pagination. */
  private static final int DEFAULT_PAGE_SIZE = 20;

  /** Service for core recipe operations. */
  private final RecipeService recipeService;

  /** Service for ingredient-related operations. */
  private final IngredientService ingredientService;

  /** Service for step-related operations. */
  private final StepService stepService;

  /** Service for tag-related operations. */
  private final TagService tagService;

  /** Service for media-related operations. */
  private final MediaService mediaService;

  /** Service for review-related operations. */
  private final ReviewService reviewService;

  /**
   * Constructs the controller with all required services.
   *
   * @param recipeService the recipe service
   * @param ingredientService the ingredient service
   * @param stepService the step service
   * @param tagService the tag service
   * @param mediaService the media service
   * @param reviewService the review service
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Spring-managed beans are safe to inject and not exposed externally")
  public RecipeManagementController(
      final RecipeService recipeService,
      final IngredientService ingredientService,
      final StepService stepService,
      final TagService tagService,
      final MediaService mediaService,
      final ReviewService reviewService) {
    this.recipeService = recipeService;
    this.ingredientService = ingredientService;
    this.stepService = stepService;
    this.tagService = tagService;
    this.mediaService = mediaService;
    this.reviewService = reviewService;
  }

  /**
   * Create a new recipe.
   *
   * @param request the create recipe request DTO
   * @return ResponseEntity with the created recipe ID
   */
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RecipeDto> createRecipe(
      @Valid @RequestBody final CreateRecipeRequest request) {
    return recipeService.createRecipe(request);
  }

  /**
   * Get all recipes.
   *
   * @param pageable pagination parameters
   * @return ResponseEntity with paginated list of recipes
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SearchRecipesResponse> getAllRecipes(
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
    return recipeService.getAllRecipes(pageable);
  }

  /**
   * Update an existing recipe.
   *
   * @param recipeId the recipe ID
   * @param request the update recipe request DTO
   * @return ResponseEntity with the updated recipe ID
   */
  @PutMapping(value = "/{recipeId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RecipeDto> updateRecipe(
      @PathVariable final String recipeId, @Valid @RequestBody final UpdateRecipeRequest request) {
    return recipeService.updateRecipe(recipeId, request);
  }

  /**
   * Remove a recipe.
   *
   * @param recipeId the recipe ID
   * @return placeholder response
   */
  @DeleteMapping("/{recipeId}")
  public ResponseEntity<Void> deleteRecipe(@PathVariable final String recipeId) {
    return recipeService.deleteRecipe(recipeId);
  }

  /**
   * Get a full recipe.
   *
   * @param recipeId the recipe ID
   * @return placeholder response
   */
  @GetMapping(value = "/{recipeId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RecipeDto> getRecipe(@PathVariable final String recipeId) {
    return recipeService.getRecipe(recipeId);
  }

  /**
   * Get recipe description.
   *
   * @param recipeId the recipe ID
   * @return placeholder response
   */
  @GetMapping("/{recipeId}/description")
  public ResponseEntity<String> getRecipeDescription(@PathVariable final String recipeId) {
    // Placeholder: would call recipeService.getDescription(recipeId)
    return ResponseEntity.ok("Get Recipe Description - placeholder");
  }

  /**
   * Get recipe ingredients.
   *
   * @param recipeId the recipe ID
   * @return response with recipe ingredients
   */
  @GetMapping(value = "/{recipeId}/ingredients", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RecipeIngredientsResponse> getRecipeIngredients(
      @PathVariable final String recipeId) {
    return ingredientService.getIngredients(recipeId);
  }

  /**
   * Get recipe steps.
   *
   * @param recipeId the recipe ID
   * @return placeholder response
   */
  @GetMapping("/{recipeId}/steps")
  public ResponseEntity<StepResponse> getRecipeSteps(@PathVariable final Long recipeId) {
    StepResponse response = stepService.getSteps(recipeId);
    return ResponseEntity.ok(response);
  }

  /**
   * Get recipe history.
   *
   * @param recipeId the recipe ID
   * @return placeholder response
   */
  @GetMapping("/{recipeId}/history")
  public ResponseEntity<String> getRecipeHistory(@PathVariable final String recipeId) {
    // Placeholder: would call recipeService.getHistory(recipeId)
    return ResponseEntity.ok("Get Recipe History - placeholder");
  }

  /**
   * Scale recipe ingredients.
   *
   * @param recipeId the recipe ID
   * @param quantity the scale quantity
   * @return response with scaled recipe ingredients
   */
  @GetMapping(value = "/{recipeId}/ingredients/scale", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RecipeIngredientsResponse> scaleRecipeIngredients(
      @PathVariable final String recipeId, @RequestParam @Min(0) final float quantity) {
    return ingredientService.scaleIngredients(recipeId, quantity);
  }

  /**
   * Generate shopping list.
   *
   * @param recipeId the recipe ID
   * @return response with shopping list
   */
  @GetMapping(
      value = "/{recipeId}/ingredients/shopping-list",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ShoppingListResponse> generateShoppingList(
      @PathVariable final String recipeId) {
    return ingredientService.generateShoppingList(recipeId);
  }

  /**
   * Add a tag to a recipe.
   *
   * @param recipeId the recipe ID
   * @param request the add tag request
   * @return response with updated tags
   */
  @PostMapping(value = "/{recipeId}/tags", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TagResponse> addTag(
      @PathVariable final Long recipeId, @Valid @RequestBody final AddTagRequest request) {
    TagResponse response = tagService.addTag(recipeId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Remove a tag from a recipe.
   *
   * @param recipeId the recipe ID
   * @param request the remove tag request
   * @return response with updated tags
   */
  @DeleteMapping(value = "/{recipeId}/tags", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TagResponse> removeTag(
      @PathVariable final Long recipeId, @Valid @RequestBody final RemoveTagRequest request) {
    TagResponse response = tagService.removeTag(recipeId, request);
    return ResponseEntity.ok(response);
  }

  /**
   * Get tags for a recipe.
   *
   * @param recipeId the recipe ID
   * @return response with all tags for the recipe
   */
  @GetMapping(value = "/{recipeId}/tags", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TagResponse> getTags(@PathVariable final Long recipeId) {
    TagResponse response = tagService.getTags(recipeId);
    return ResponseEntity.ok(response);
  }

  /**
   * Add media reference to a recipe.
   *
   * @param recipeId the recipe ID
   * @return placeholder response
   */
  @PostMapping("/{recipeId}/media")
  public ResponseEntity<String> addMediaToRecipe(@PathVariable final String recipeId) {
    return mediaService.addMediaToRecipe(recipeId);
  }

  /**
   * Update media reference on a recipe.
   *
   * @param recipeId the recipe ID
   * @return placeholder response
   */
  @PutMapping("/{recipeId}/media")
  public ResponseEntity<String> updateMediaOnRecipe(@PathVariable final String recipeId) {
    return mediaService.updateMediaOnRecipe(recipeId);
  }

  /**
   * Delete media reference from a recipe.
   *
   * @param recipeId the recipe ID
   * @return placeholder response
   */
  @DeleteMapping("/{recipeId}/media")
  public ResponseEntity<String> deleteMediaFromRecipe(@PathVariable final String recipeId) {
    return mediaService.deleteMediaFromRecipe(recipeId);
  }

  /**
   * Add media reference to an ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @return placeholder response
   */
  @PostMapping("/{recipeId}/ingredients/{ingredientId}/media")
  public ResponseEntity<String> addMediaToIngredient(
      @PathVariable final String recipeId, @PathVariable final String ingredientId) {
    return mediaService.addMediaToIngredient(recipeId, ingredientId);
  }

  /**
   * Update media reference on an ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @return placeholder response
   */
  @PutMapping("/{recipeId}/ingredients/{ingredientId}/media")
  public ResponseEntity<String> updateMediaOnIngredient(
      @PathVariable final String recipeId, @PathVariable final String ingredientId) {
    return mediaService.updateMediaOnIngredient(recipeId, ingredientId);
  }

  /**
   * Delete media reference from an ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @return placeholder response
   */
  @DeleteMapping("/{recipeId}/ingredients/{ingredientId}/media")
  public ResponseEntity<String> deleteMediaFromIngredient(
      @PathVariable final String recipeId, @PathVariable final String ingredientId) {
    return mediaService.deleteMediaFromIngredient(recipeId, ingredientId);
  }

  /**
   * Add media reference to a step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return placeholder response
   */
  @PostMapping("/{recipeId}/steps/{stepId}/media")
  public ResponseEntity<String> addMediaToStep(
      @PathVariable final String recipeId, @PathVariable final String stepId) {
    return mediaService.addMediaToStep(recipeId, stepId);
  }

  /**
   * Update media reference on a step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return placeholder response
   */
  @PutMapping("/{recipeId}/steps/{stepId}/media")
  public ResponseEntity<String> updateMediaOnStep(
      @PathVariable final String recipeId, @PathVariable final String stepId) {
    return mediaService.updateMediaOnStep(recipeId, stepId);
  }

  /**
   * Delete media reference from a step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return placeholder response
   */
  @DeleteMapping("/{recipeId}/steps/{stepId}/media")
  public ResponseEntity<String> deleteMediaFromStep(
      @PathVariable final String recipeId, @PathVariable final String stepId) {
    return mediaService.deleteMediaFromStep(recipeId, stepId);
  }

  /**
   * Search recipes based on flexible criteria.
   *
   * @param searchRequest the search criteria (request body)
   * @param pageable pagination parameters (query parameters)
   * @return ResponseEntity with paginated search results
   */
  @PostMapping("/search")
  public ResponseEntity<SearchRecipesResponse> searchRecipes(
      @Valid @RequestBody final SearchRecipesRequest searchRequest,
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
    return recipeService.searchRecipes(searchRequest, pageable);
  }

  /**
   * Add comment to ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @param request the add comment request
   * @return response with updated comments
   */
  @PostMapping(
      value = "/{recipeId}/ingredients/{ingredientId}/comment",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<IngredientCommentResponse> addCommentToIngredient(
      @PathVariable final String recipeId,
      @PathVariable final String ingredientId,
      @Valid @RequestBody final AddIngredientCommentRequest request) {
    return ingredientService.addComment(recipeId, ingredientId, request);
  }

  /**
   * Edit comment on ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @param request the edit comment request
   * @return response with updated comments
   */
  @PutMapping(
      value = "/{recipeId}/ingredients/{ingredientId}/comment",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<IngredientCommentResponse> editCommentOnIngredient(
      @PathVariable final String recipeId,
      @PathVariable final String ingredientId,
      @Valid @RequestBody final EditIngredientCommentRequest request) {
    return ingredientService.editComment(recipeId, ingredientId, request);
  }

  /**
   * Delete comment from ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @param request the delete comment request
   * @return response with updated comments
   */
  @DeleteMapping(
      value = "/{recipeId}/ingredients/{ingredientId}/comment",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<IngredientCommentResponse> deleteCommentFromIngredient(
      @PathVariable final String recipeId,
      @PathVariable final String ingredientId,
      @Valid @RequestBody final DeleteIngredientCommentRequest request) {
    return ingredientService.deleteComment(recipeId, ingredientId, request);
  }

  /**
   * Get comments for step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return response with all comments for the step
   */
  @GetMapping(
      value = "/{recipeId}/steps/{stepId}/comment",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StepCommentResponse> getStepComments(
      @PathVariable final Long recipeId, @PathVariable final Long stepId) {
    StepCommentResponse response = stepService.getStepComments(recipeId, stepId);
    return ResponseEntity.ok(response);
  }

  /**
   * Add comment to step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @param request the add step comment request
   * @return placeholder response
   */
  @PostMapping("/{recipeId}/steps/{stepId}/comment")
  public ResponseEntity<StepCommentDto> addCommentToStep(
      @PathVariable final Long recipeId,
      @PathVariable final Long stepId,
      @RequestBody @Valid final AddStepCommentRequest request) {
    StepCommentDto comment = stepService.addComment(recipeId, stepId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(comment);
  }

  /**
   * Edit comment on step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @param request the edit step comment request
   * @return placeholder response
   */
  @PutMapping("/{recipeId}/steps/{stepId}/comment")
  public ResponseEntity<StepCommentDto> editCommentOnStep(
      @PathVariable final Long recipeId,
      @PathVariable final Long stepId,
      @RequestBody @Valid final EditStepCommentRequest request) {
    StepCommentDto comment = stepService.editComment(recipeId, stepId, request);
    return ResponseEntity.ok(comment);
  }

  /**
   * Delete comment from step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @param request the delete step comment request
   * @return placeholder response
   */
  @DeleteMapping("/{recipeId}/steps/{stepId}/comment")
  public ResponseEntity<Void> deleteCommentFromStep(
      @PathVariable final Long recipeId,
      @PathVariable final Long stepId,
      @RequestBody @Valid final DeleteStepCommentRequest request) {
    stepService.deleteComment(recipeId, stepId, request);
    return ResponseEntity.noContent().build();
  }

  /**
   * Get recipe reviews.
   *
   * @param recipeId the recipe ID
   * @return response with all reviews for the recipe
   */
  @GetMapping(value = "/{recipeId}/review", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReviewResponse> getRecipeReviews(@PathVariable final Long recipeId) {
    ReviewResponse response = reviewService.getReviews(recipeId);
    return ResponseEntity.ok(response);
  }

  /**
   * Add recipe review.
   *
   * @param recipeId the recipe ID
   * @param request the add review request
   * @return response with the created review
   */
  @PostMapping(value = "/{recipeId}/review", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReviewDto> addRecipeReview(
      @PathVariable final Long recipeId, @Valid @RequestBody final AddReviewRequest request) {
    ReviewDto review = reviewService.addReview(recipeId, request);
    return ResponseEntity.ok(review);
  }

  /**
   * Edit recipe review.
   *
   * @param recipeId the recipe ID
   * @param reviewId the review ID
   * @param request the edit review request
   * @return response with the updated review
   */
  @PutMapping(value = "/{recipeId}/review/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReviewDto> editRecipeReview(
      @PathVariable final Long recipeId,
      @PathVariable final Long reviewId,
      @Valid @RequestBody final EditReviewRequest request) {
    ReviewDto review = reviewService.editReview(recipeId, reviewId, request);
    return ResponseEntity.ok(review);
  }

  /**
   * Delete recipe review.
   *
   * @param recipeId the recipe ID
   * @param reviewId the review ID
   * @return empty response
   */
  @DeleteMapping("/{recipeId}/review/{reviewId}")
  public ResponseEntity<Void> deleteRecipeReview(
      @PathVariable final Long recipeId, @PathVariable final Long reviewId) {
    reviewService.deleteReview(recipeId, reviewId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Get all revisions for a recipe.
   *
   * @param recipeId the recipe ID
   * @return response with all revisions for the recipe
   */
  @GetMapping("/{recipeId}/revisions")
  public ResponseEntity<RecipeRevisionsResponse> getRecipeRevisions(
      @PathVariable final Long recipeId) {
    RecipeRevisionsResponse response = recipeService.getRevisions(recipeId);
    return ResponseEntity.ok(response);
  }

  /**
   * Get all revisions for a specific recipe step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return response with all revisions for the step
   */
  @GetMapping("/{recipeId}/steps/{stepId}/revisions")
  public ResponseEntity<StepRevisionsResponse> getStepRevisions(
      @PathVariable final Long recipeId, @PathVariable final Long stepId) {
    StepRevisionsResponse response = stepService.getStepRevisions(recipeId, stepId);
    return ResponseEntity.ok(response);
  }

  /**
   * Get all revisions for a specific recipe ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @return response with all revisions for the ingredient
   */
  @GetMapping("/{recipeId}/ingredients/{ingredientId}/revisions")
  public ResponseEntity<IngredientRevisionsResponse> getIngredientRevisions(
      @PathVariable final Long recipeId, @PathVariable final Long ingredientId) {
    IngredientRevisionsResponse response =
        ingredientService.getIngredientRevisions(recipeId, ingredientId);
    return ResponseEntity.ok(response);
  }
}
