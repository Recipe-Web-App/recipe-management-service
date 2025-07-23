package com.recipe_manager.service;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.request.CreateRecipeRequest;
import com.recipe_manager.model.dto.request.UpdateRecipeRequest;
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

  private final RecipeRepository recipeRepository;
  private final IngredientRepository ingredientRepository;
  private final RecipeMapper recipeMapper;

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
      throw new ResourceNotFoundException("Invalid recipe ID: " + recipeId);
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
   * @return placeholder response
   */
  public ResponseEntity<String> deleteRecipe(final String recipeId) {
    return ResponseEntity.ok("Delete Recipe - placeholder");
  }

  /**
   * Get a recipe by ID.
   *
   * @param recipeId the recipe ID
   * @return placeholder response
   */
  public ResponseEntity<String> getRecipe(final String recipeId) {
    return ResponseEntity.ok("Get Full Recipe - placeholder");
  }

  /**
   * Search for recipes.
   *
   * @return placeholder response
   */
  public ResponseEntity<String> searchRecipes() {
    return ResponseEntity.ok("Search Recipes - placeholder");
  }
}
