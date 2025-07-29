package com.recipe_manager.service;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.request.CreateRecipeRequest;
import com.recipe_manager.model.dto.request.SearchRecipesRequest;
import com.recipe_manager.model.dto.request.UpdateRecipeRequest;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.entity.recipe.RecipeIngredientId;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.util.SecurityUtils;

import jakarta.validation.Valid;

/**
 * Service for core recipe operations.
 *
 * <p>All methods are placeholders and should be implemented.
 */
@Service
@Validated
public class RecipeService {

  /** Repository used for accessing recipe data. */
  private final RecipeRepository recipeRepository;

  /** Repository used for accessing ingredient data. */
  private final IngredientRepository ingredientRepository;

  /** Mapper used for converting between recipe entities and DTOs. */
  private final RecipeMapper recipeMapper;

  /**
   * Service class for managing recipes.
   *
   * @param recipeRepository the repository used for accessing recipe data
   * @param ingredientRepository the repository used for accessing ingredient data
   * @param recipeMapper the mapper used for converting between recipe entities and DTOs
   */
  public RecipeService(
      final RecipeRepository recipeRepository,
      final IngredientRepository ingredientRepository,
      final RecipeMapper recipeMapper) {
    this.recipeRepository = recipeRepository;
    this.ingredientRepository = ingredientRepository;
    this.recipeMapper = recipeMapper;
  }

  /**
   * Create a new recipe.
   *
   * @param request the create recipe request DTO
   * @return ResponseEntity with the created recipe ID
   */
  @Transactional
  public ResponseEntity<RecipeDto> createRecipe(
      @Valid @RequestBody final CreateRecipeRequest request) {
    Recipe recipe =
        new Recipe(
            null, // recipeId (auto-generated)
            SecurityUtils.getCurrentUserId(),
            request.getTitle(),
            request.getDescription(),
            request.getOriginUrl(),
            request.getServings(),
            request.getPreparationTime(),
            request.getCookingTime(),
            request.getDifficulty(),
            null, // createdAt (auto-generated)
            null, // updatedAt (auto-generated)
            null, // ingredients (set below)
            null, // steps (handled separately)
            null, // revisions (empty on create)
            null, // favorites (empty on create)
            null // tags (empty on create)
            );

    // Map and persist ingredients
    if (!request.getIngredients().isEmpty()) {
      final Recipe savedRecipe = recipe; // for lambda capture
      var recipeIngredients =
          request.getIngredients().stream()
              .map(
                  ingredientReq -> {
                    Ingredient ingredient = null;
                    if (ingredientReq.getIngredientId() != null) {
                      Optional<Ingredient> found =
                          ingredientRepository.findById(ingredientReq.getIngredientId());
                      ingredient = found.orElse(null);
                    }
                    if (ingredient == null && ingredientReq.getIngredientName() != null) {
                      ingredient =
                          ingredientRepository
                              .findByNameIgnoreCase(ingredientReq.getIngredientName())
                              .orElseGet(
                                  () ->
                                      ingredientRepository.save(
                                          Ingredient.builder()
                                              .name(ingredientReq.getIngredientName())
                                              .build()));
                    }
                    if (ingredient == null) {
                      throw new IllegalArgumentException(
                          "Ingredient must have either a valid ID or name");
                    }
                    RecipeIngredientId id =
                        RecipeIngredientId.builder()
                            .recipeId(null) // will be set by JPA after recipe is saved
                            .ingredientId(ingredient.getIngredientId())
                            .build();
                    return RecipeIngredient.builder()
                        .id(id)
                        .recipe(savedRecipe)
                        .ingredient(ingredient)
                        .quantity(ingredientReq.getQuantity())
                        .unit(ingredientReq.getUnit())
                        .isOptional(Boolean.TRUE.equals(ingredientReq.getIsOptional()))
                        .build();
                  })
              .collect(Collectors.toList());
      recipe.setRecipeIngredients(recipeIngredients);
    }

    Recipe saved = recipeRepository.save(recipe);
    RecipeDto response = recipeMapper.toDto(saved);
    return ResponseEntity.ok(response);
  }

  /**
   * Update an existing recipe.
   *
   * @param recipeId the recipe ID
   * @param request the update recipe request DTO
   * @return ResponseEntity with the updated recipe ID
   */
  @Transactional
  public ResponseEntity<RecipeDto> updateRecipe(
      final String recipeId, final UpdateRecipeRequest request) {
    // Parse recipeId
    Long id;
    try {
      id = Long.parseLong(recipeId);
    } catch (NumberFormatException e) {
      throw new BusinessException("Invalid recipe ID: " + recipeId);
    }

    // Fetch existing recipe
    Recipe recipe =
        recipeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId));

    if (!recipe.getUserId().equals(SecurityUtils.getCurrentUserId())) {
      throw new AccessDeniedException("User does not have permission to update this recipe");
    }

    // Update fields and nested collections using MapStruct
    recipeMapper.updateRecipeFromRequest(request, recipe);
    Recipe saved = recipeRepository.save(recipe);
    RecipeDto response = recipeMapper.toDto(saved);

    return ResponseEntity.ok(response);
  }

  /**
   * Delete a recipe.
   *
   * @param recipeId the recipe ID
   * @return ResponseEntity with success message
   */
  @Transactional
  public ResponseEntity<Void> deleteRecipe(final String recipeId) {
    // Parse recipeId
    Long id;
    try {
      id = Long.parseLong(recipeId);
    } catch (NumberFormatException e) {
      throw new BusinessException("Invalid recipe ID: " + recipeId);
    }

    // Fetch the recipe to verify it exists and check ownership
    Recipe recipe =
        recipeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId));

    // Check if the current user owns this recipe
    if (!recipe.getUserId().equals(SecurityUtils.getCurrentUserId())) {
      throw new AccessDeniedException("User does not have permission to delete this recipe");
    }

    // Delete the recipe
    recipeRepository.delete(recipe);

    return ResponseEntity.noContent().build();
  }

  /**
   * Get a recipe by ID.
   *
   * @param recipeId the recipe ID
   * @return ResponseEntity with the recipe data
   */
  public ResponseEntity<RecipeDto> getRecipe(final String recipeId) {
    // Parse recipeId
    Long id;
    try {
      id = Long.parseLong(recipeId);
    } catch (NumberFormatException e) {
      throw new BusinessException("Invalid recipe ID: " + recipeId);
    }

    // Fetch the recipe
    Recipe recipe =
        recipeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId));

    // Convert to DTO using mapper
    RecipeDto response = recipeMapper.toDto(recipe);
    return ResponseEntity.ok(response);
  }

  /**
   * Search for recipes based on flexible criteria.
   *
   * @param searchRequest the search criteria
   * @param pageable pagination information
   * @return ResponseEntity with paginated search results
   */
  public ResponseEntity<SearchRecipesResponse> searchRecipes(
      @Valid final SearchRecipesRequest searchRequest, final Pageable pageable) {

    // Perform search using repository
    Page<Recipe> recipePage = recipeRepository.searchRecipes(searchRequest, pageable);

    return ResponseEntity.ok(buildSearchRecipesResponse(recipePage));
  }

  /**
   * Get all recipes with pagination.
   *
   * @param pageable pagination information
   * @return ResponseEntity with paginated list of recipes
   */
  public ResponseEntity<SearchRecipesResponse> getAllRecipes(final Pageable pageable) {
    // Fetch all recipes with pagination
    Page<Recipe> recipePage = recipeRepository.findAll(pageable);

    return ResponseEntity.ok(buildSearchRecipesResponse(recipePage));
  }

  /**
   * Builds a SearchRecipesResponse from a Page of Recipe entities.
   *
   * @param recipePage the page of recipes
   * @return SearchRecipesResponse with pagination metadata
   */
  private SearchRecipesResponse buildSearchRecipesResponse(final Page<Recipe> recipePage) {
    // Convert recipes to DTOs
    var recipeDtos = recipePage.getContent().stream().map(recipeMapper::toDto).toList();

    // Build response with pagination metadata
    return SearchRecipesResponse.builder()
        .recipes(recipeDtos)
        .page(recipePage.getNumber())
        .size(recipePage.getSize())
        .totalElements(recipePage.getTotalElements())
        .totalPages(recipePage.getTotalPages())
        .first(recipePage.isFirst())
        .last(recipePage.isLast())
        .numberOfElements(recipePage.getNumberOfElements())
        .empty(recipePage.isEmpty())
        .build();
  }
}
