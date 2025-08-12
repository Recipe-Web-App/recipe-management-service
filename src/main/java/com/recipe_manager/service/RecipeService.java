package com.recipe_manager.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.request.CreateRecipeRequest;
import com.recipe_manager.model.dto.request.SearchRecipesRequest;
import com.recipe_manager.model.dto.request.UpdateRecipeRequest;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.entity.recipe.RecipeIngredientId;
import com.recipe_manager.model.entity.recipe.RecipeTag;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.model.mapper.RecipeStepMapper;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.repository.recipe.RecipeTagRepository;
import com.recipe_manager.util.SecurityUtils;

/**
 * Service for core recipe operations.
 *
 * <p>All methods are placeholders and should be implemented.
 */
@Service
public class RecipeService {

  /** Repository used for accessing recipe data. */
  private final RecipeRepository recipeRepository;

  /** Repository used for accessing ingredient data. */
  private final IngredientRepository ingredientRepository;

  /** Repository used for accessing recipe tag data. */
  private final RecipeTagRepository recipeTagRepository;

  /** Mapper used for converting between recipe entities and DTOs. */
  private final RecipeMapper recipeMapper;

  /** Mapper used for converting between recipe step entities and DTOs. */
  private final RecipeStepMapper recipeStepMapper;

  /**
   * Service class for managing recipes.
   *
   * @param recipeRepository the repository used for accessing recipe data
   * @param ingredientRepository the repository used for accessing ingredient data
   * @param recipeTagRepository the repository used for accessing recipe tag data
   * @param recipeMapper the mapper used for converting between recipe entities and DTOs
   * @param recipeStepMapper the mapper used for converting between recipe step entities and DTOs
   */
  public RecipeService(
      final RecipeRepository recipeRepository,
      final IngredientRepository ingredientRepository,
      final RecipeTagRepository recipeTagRepository,
      final RecipeMapper recipeMapper,
      final RecipeStepMapper recipeStepMapper) {
    this.recipeRepository = recipeRepository;
    this.ingredientRepository = ingredientRepository;
    this.recipeTagRepository = recipeTagRepository;
    this.recipeMapper = recipeMapper;
    this.recipeStepMapper = recipeStepMapper;
  }

  /**
   * Create a new recipe.
   *
   * @param request the create recipe request DTO
   * @return ResponseEntity with the created recipe ID
   */
  @Transactional
  public ResponseEntity<RecipeDto> createRecipe(final CreateRecipeRequest request) {
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
                    Ingredient ingredient = resolveIngredient(ingredientReq);
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

    // Map and persist steps
    if (request.getSteps() != null && !request.getSteps().isEmpty()) {
      var recipeSteps = recipeStepMapper.toEntityList(request.getSteps());
      for (var step : recipeSteps) {
        step.setRecipe(recipe);
      }
      recipe.setRecipeSteps(recipeSteps);
    }

    // Map and persist tags
    if (request.getTags() != null && !request.getTags().isEmpty()) {
      var recipeTags =
          request.getTags().stream()
              .map(
                  tagDto -> {
                    // Find existing tag by name or create new one
                    return recipeTagRepository
                        .findByNameIgnoreCase(tagDto.getName())
                        .orElseGet(
                            () ->
                                recipeTagRepository.save(
                                    RecipeTag.builder().name(tagDto.getName()).build()));
                  })
              .collect(Collectors.toList());
      recipe.setRecipeTags(recipeTags);
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

    // Update basic fields manually to avoid null overwrites
    if (request.getTitle() != null) {
      recipe.setTitle(request.getTitle());
    }
    if (request.getDescription() != null) {
      recipe.setDescription(request.getDescription());
    }
    if (request.getOriginUrl() != null) {
      recipe.setOriginUrl(request.getOriginUrl());
    }
    if (request.getServings() != null) {
      recipe.setServings(request.getServings());
    }
    if (request.getPreparationTime() != null) {
      recipe.setPreparationTime(request.getPreparationTime());
    }
    if (request.getCookingTime() != null) {
      recipe.setCookingTime(request.getCookingTime());
    }
    if (request.getDifficulty() != null) {
      recipe.setDifficulty(request.getDifficulty());
    }

    // Update ingredients if provided
    if (request.getIngredients() != null) {
      // Clear existing ingredients
      recipe.getRecipeIngredients().clear();

      // Add new ingredients
      if (!request.getIngredients().isEmpty()) {
        final Recipe savedRecipe = recipe; // for lambda capture
        var recipeIngredients =
            request.getIngredients().stream()
                .map(
                    ingredientReq -> {
                      Ingredient ingredient = resolveIngredient(ingredientReq);
                      RecipeIngredientId rid =
                          RecipeIngredientId.builder()
                              .recipeId(savedRecipe.getRecipeId())
                              .ingredientId(ingredient.getIngredientId())
                              .build();
                      return RecipeIngredient.builder()
                          .id(rid)
                          .recipe(savedRecipe)
                          .ingredient(ingredient)
                          .quantity(ingredientReq.getQuantity())
                          .unit(ingredientReq.getUnit())
                          .isOptional(ingredientReq.getIsOptional())
                          .build();
                    })
                .collect(Collectors.toList());
        recipe.getRecipeIngredients().addAll(recipeIngredients);
      }
    }

    // Update steps if provided
    if (request.getSteps() != null) {
      // Clear existing steps
      recipe.getRecipeSteps().clear();

      // Add new steps
      var recipeSteps = recipeStepMapper.toEntityList(request.getSteps());
      for (var step : recipeSteps) {
        step.setRecipe(recipe);
      }
      recipe.getRecipeSteps().addAll(recipeSteps);
    }

    // Update tags if provided
    if (request.getTags() != null) {
      // Get existing tag names for this recipe
      var existingTagNames =
          recipe.getRecipeTags().stream().map(RecipeTag::getName).collect(Collectors.toSet());

      // Add only new tags that don't already exist
      var newRecipeTags =
          request.getTags().stream()
              .filter(tagDto -> !existingTagNames.contains(tagDto.getName()))
              .map(
                  tagDto -> {
                    // Find existing tag by name or create new one
                    return recipeTagRepository
                        .findByNameIgnoreCase(tagDto.getName())
                        .orElseGet(
                            () ->
                                recipeTagRepository.save(
                                    RecipeTag.builder().name(tagDto.getName()).build()));
                  })
              .collect(Collectors.toList());
      recipe.getRecipeTags().addAll(newRecipeTags);
    }

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
      final SearchRecipesRequest searchRequest, final Pageable pageable) {

    // Preprocess tags to ensure case-insensitive matching by converting to
    // lowercase
    List<String> tagsToSearch = searchRequest.getTags();
    if (searchRequest.getTags() != null && !searchRequest.getTags().isEmpty()) {
      // Find actual tag names that match case-insensitively
      var matchingTagNames =
          searchRequest.getTags().stream()
              .map(tagName -> recipeTagRepository.findByNameIgnoreCase(tagName))
              .filter(Optional::isPresent)
              .map(Optional::get)
              .map(RecipeTag::getName)
              .collect(Collectors.toList());

      // Update the search request with the actual tag names from the database
      tagsToSearch = matchingTagNames.isEmpty() ? null : matchingTagNames;
    }

    // Preprocess ingredients to ensure case-insensitive matching
    List<String> ingredientsToSearch = null;
    if (searchRequest.getIngredients() != null && !searchRequest.getIngredients().isEmpty()) {
      List<String> lowercaseIngredients =
          searchRequest.getIngredients().stream()
              .map(String::toLowerCase)
              .collect(Collectors.toList());
      ingredientsToSearch = lowercaseIngredients.isEmpty() ? null : lowercaseIngredients;
    }

    // Create a new search request with the processed ingredients and tags
    SearchRecipesRequest processedSearchRequest =
        SearchRecipesRequest.builder()
            .recipeNameQuery(searchRequest.getRecipeNameQuery())
            .difficulty(searchRequest.getDifficulty())
            .maxCookingTime(searchRequest.getMaxCookingTime())
            .maxPreparationTime(searchRequest.getMaxPreparationTime())
            .minServings(searchRequest.getMinServings())
            .maxServings(searchRequest.getMaxServings())
            .ingredients(searchRequest.getIngredients())
            .tags(searchRequest.getTags())
            .ingredientMatchMode(searchRequest.getIngredientMatchMode())
            .build();

    // Perform search using repository
    Page<Recipe> recipePage =
        recipeRepository.searchRecipes(
            processedSearchRequest.getRecipeNameQuery(),
            processedSearchRequest.getDifficulty() != null
                ? processedSearchRequest.getDifficulty().name()
                : null,
            processedSearchRequest.getMaxCookingTime(),
            processedSearchRequest.getMaxPreparationTime(),
            processedSearchRequest.getMinServings(),
            processedSearchRequest.getMaxServings(),
            ingredientsToSearch != null
                ? ingredientsToSearch.toArray(new String[0])
                : new String[0],
            tagsToSearch != null ? tagsToSearch.toArray(new String[0]) : new String[0],
            pageable);

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

  /**
   * Resolves an ingredient by ID or name, creating it if necessary.
   *
   * @param ingredientReq the ingredient request containing ID or name
   * @return the resolved ingredient
   * @throws IllegalArgumentException if ingredient has neither valid ID nor name
   */
  private Ingredient resolveIngredient(final RecipeIngredientDto ingredientReq) {
    Ingredient ingredient = null;

    // Try to find by ID first
    if (ingredientReq.getIngredientId() != null) {
      Optional<Ingredient> found = ingredientRepository.findById(ingredientReq.getIngredientId());
      ingredient = found.orElse(null);
    }

    // If not found by ID, try to find or create by name
    if (ingredient == null && ingredientReq.getIngredientName() != null) {
      ingredient =
          ingredientRepository
              .findByNameIgnoreCase(ingredientReq.getIngredientName())
              .orElseGet(
                  () ->
                      ingredientRepository.save(
                          Ingredient.builder().name(ingredientReq.getIngredientName()).build()));
    }

    // Validate that we have a valid ingredient
    if (ingredient == null) {
      throw new IllegalArgumentException("Ingredient must have either a valid ID or name");
    }

    return ingredient;
  }
}
