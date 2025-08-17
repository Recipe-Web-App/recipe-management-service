package com.recipe_manager.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.request.AddIngredientCommentRequest;
import com.recipe_manager.model.dto.request.DeleteIngredientCommentRequest;
import com.recipe_manager.model.dto.request.EditIngredientCommentRequest;
import com.recipe_manager.model.dto.response.IngredientCommentResponse;
import com.recipe_manager.model.dto.response.RecipeIngredientsResponse;
import com.recipe_manager.model.dto.response.ShoppingListResponse;
import com.recipe_manager.model.dto.shopping.ShoppingListItemDto;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.ingredient.IngredientComment;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.mapper.IngredientCommentMapper;
import com.recipe_manager.model.mapper.RecipeIngredientMapper;
import com.recipe_manager.model.mapper.ShoppingListMapper;
import com.recipe_manager.repository.ingredient.IngredientCommentRepository;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeIngredientRepository;
import com.recipe_manager.service.external.RecipeScraperService;
import com.recipe_manager.util.SecurityUtils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Service for ingredient-related operations.
 *
 * <p>All methods are placeholders and should be implemented.
 */
@Service
public final class IngredientService {

  /** Logger for service operations. */
  private static final Logger LOGGER = LoggerFactory.getLogger(IngredientService.class);

  /** Repository for recipe ingredient data access operations. */
  private final RecipeIngredientRepository recipeIngredientRepository;

  /** Repository for ingredient data access operations. */
  private final IngredientRepository ingredientRepository;

  /** Repository for ingredient comment data access operations. */
  private final IngredientCommentRepository ingredientCommentRepository;

  /** Mapper for converting between RecipeIngredient entities and DTOs. */
  private final RecipeIngredientMapper recipeIngredientMapper;

  /** Mapper for converting between IngredientComment entities and DTOs. */
  private final IngredientCommentMapper ingredientCommentMapper;

  /** Mapper for converting RecipeIngredient entities to shopping list items. */
  private final ShoppingListMapper shoppingListMapper;

  /** Service for retrieving external pricing information. */
  private final RecipeScraperService recipeScraperService;

  /** Metrics registry for observability. */
  @Autowired private MeterRegistry meterRegistry;

  /** Counter for tracking shopping list generations. */
  private Counter shoppingListGenerationsCounter;

  /** Counter for tracking shopping lists with pricing data. */
  private Counter shoppingListsWithPricingCounter;

  public IngredientService(
      final RecipeIngredientRepository recipeIngredientRepository,
      final IngredientRepository ingredientRepository,
      final IngredientCommentRepository ingredientCommentRepository,
      final RecipeIngredientMapper recipeIngredientMapper,
      final IngredientCommentMapper ingredientCommentMapper,
      final ShoppingListMapper shoppingListMapper,
      final RecipeScraperService recipeScraperService) {
    this.recipeIngredientRepository = recipeIngredientRepository;
    this.ingredientRepository = ingredientRepository;
    this.ingredientCommentRepository = ingredientCommentRepository;
    this.recipeIngredientMapper = recipeIngredientMapper;
    this.ingredientCommentMapper = ingredientCommentMapper;
    this.shoppingListMapper = shoppingListMapper;
    this.recipeScraperService = recipeScraperService;
  }

  /** Initializes metrics for monitoring service operations. */
  @jakarta.annotation.PostConstruct
  public void initMetrics() {
    if (meterRegistry != null) {
      shoppingListGenerationsCounter =
          Counter.builder("shopping.list.generations")
              .description("Total number of shopping list generations")
              .register(meterRegistry);

      shoppingListsWithPricingCounter =
          Counter.builder("shopping.list.with.pricing")
              .description("Total number of shopping lists generated with pricing data")
              .register(meterRegistry);
    }
  }

  /**
   * Get ingredients for a recipe.
   *
   * @param recipeId the recipe ID
   * @return response with recipe ingredients
   */
  public ResponseEntity<RecipeIngredientsResponse> getIngredients(final String recipeId) {
    Long id;
    try {
      id = Long.parseLong(recipeId);
    } catch (NumberFormatException e) {
      throw new BusinessException("Invalid recipe ID: " + recipeId);
    }
    final List<RecipeIngredient> ingredients = recipeIngredientRepository.findByRecipeRecipeId(id);
    final List<RecipeIngredientDto> ingredientDtos = recipeIngredientMapper.toDtoList(ingredients);

    // Fetch and populate comments for each ingredient
    ingredientDtos.forEach(
        dto -> {
          final List<IngredientComment> comments =
              ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(
                  dto.getIngredientId());
          dto.setComments(ingredientCommentMapper.toDtoList(comments));
        });

    final RecipeIngredientsResponse response =
        RecipeIngredientsResponse.builder()
            .recipeId(id)
            .ingredients(ingredientDtos)
            .totalCount(ingredientDtos.size())
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Scale ingredients for a recipe.
   *
   * @param recipeId the recipe ID
   * @param quantity the scale quantity
   * @return response with scaled recipe ingredients
   */
  public ResponseEntity<RecipeIngredientsResponse> scaleIngredients(
      final String recipeId, final float quantity) {
    Long id;
    try {
      id = Long.parseLong(recipeId);
    } catch (NumberFormatException e) {
      throw new BusinessException("Invalid recipe ID: " + recipeId);
    }
    final List<RecipeIngredient> ingredients = recipeIngredientRepository.findByRecipeRecipeId(id);
    final List<RecipeIngredientDto> scaledIngredientDtos =
        recipeIngredientMapper.toDtoListWithScale(ingredients, quantity);

    // Fetch and populate comments for each ingredient
    scaledIngredientDtos.forEach(
        dto -> {
          final List<IngredientComment> comments =
              ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(
                  dto.getIngredientId());
          dto.setComments(ingredientCommentMapper.toDtoList(comments));
        });

    final RecipeIngredientsResponse response =
        RecipeIngredientsResponse.builder()
            .recipeId(id)
            .ingredients(scaledIngredientDtos)
            .totalCount(scaledIngredientDtos.size())
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Generate a shopping list for a recipe with pricing information.
   *
   * @param recipeId the recipe ID
   * @return response with shopping list including pricing data
   */
  public ResponseEntity<ShoppingListResponse> generateShoppingList(final String recipeId) {
    Long id;
    try {
      id = Long.parseLong(recipeId);
    } catch (NumberFormatException e) {
      throw new BusinessException("Invalid recipe ID: " + recipeId);
    }

    if (shoppingListGenerationsCounter != null) {
      shoppingListGenerationsCounter.increment();
    }
    LOGGER.info("Generating shopping list for recipe {}", id);

    // Get local ingredient data
    final List<RecipeIngredient> ingredients = recipeIngredientRepository.findByRecipeRecipeId(id);
    final List<ShoppingListItemDto> shoppingListItems =
        shoppingListMapper.toAggregatedShoppingListItems(ingredients);

    // Get external pricing data
    RecipeScraperShoppingDto pricingData = null;
    boolean hasPricingData = false;
    try {
      LOGGER.debug("Fetching pricing data for recipe {}", id);
      pricingData = recipeScraperService.getShoppingInfo(id).get();
      hasPricingData = pricingData != null && !pricingData.getIngredients().isEmpty();
      if (hasPricingData) {
        if (shoppingListsWithPricingCounter != null) {
          shoppingListsWithPricingCounter.increment();
        }
        LOGGER.debug(
            "Successfully retrieved pricing data for recipe {} with {} ingredients",
            id,
            pricingData != null ? pricingData.getIngredients().size() : 0);
      }
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.warn("Failed to retrieve pricing data for recipe {}: {}", id, e.getMessage());
      // Continue without pricing data - graceful degradation
    }

    // Merge pricing data with shopping list items
    final List<ShoppingListItemDto> enrichedShoppingListItems =
        shoppingListMapper.mergeWithPricingData(shoppingListItems, pricingData);

    // Calculate total estimated cost
    final BigDecimal totalEstimatedCost =
        enrichedShoppingListItems.stream()
            .map(ShoppingListItemDto::getEstimatedPrice)
            .filter(price -> price != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    final ShoppingListResponse response =
        ShoppingListResponse.builder()
            .recipeId(id)
            .items(enrichedShoppingListItems)
            .totalCount(enrichedShoppingListItems.size())
            .totalEstimatedCost(totalEstimatedCost)
            .build();

    LOGGER.info(
        "Generated shopping list for recipe {} with {} items, total cost: ${}, pricing data: {}",
        id,
        enrichedShoppingListItems.size(),
        totalEstimatedCost,
        hasPricingData ? "available" : "unavailable");

    return ResponseEntity.ok(response);
  }

  /**
   * Add a comment to an ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @param request the add comment request
   * @return response with updated comments
   */
  public ResponseEntity<IngredientCommentResponse> addComment(
      final String recipeId, final String ingredientId, final AddIngredientCommentRequest request) {
    final Long recipeIdLong = validateAndParseId(recipeId, "recipe");
    final Long ingredientIdLong = validateAndParseId(ingredientId, "ingredient");

    // Verify the recipe ingredient exists
    final RecipeIngredient recipeIngredient =
        validateRecipeIngredient(recipeIdLong, ingredientIdLong, recipeId, ingredientId);
    final Ingredient ingredient = recipeIngredient.getIngredient();

    // Create and save the new comment
    final IngredientComment comment =
        IngredientComment.builder()
            .ingredient(ingredient)
            .recipeId(recipeIdLong)
            .userId(SecurityUtils.getCurrentUserId())
            .commentText(request.getComment())
            .isPublic(true)
            .build();

    ingredientCommentRepository.save(comment);

    // Get all comments for this ingredient
    final List<IngredientComment> comments =
        ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(
            ingredientIdLong);

    LOGGER.info(
        "Added comment to ingredient {} in recipe {}: {}",
        ingredientId,
        recipeId,
        request.getComment());

    return ResponseEntity.ok(
        IngredientCommentResponse.builder()
            .recipeId(recipeIdLong)
            .ingredientId(ingredientIdLong)
            .comments(ingredientCommentMapper.toDtoList(comments))
            .build());
  }

  /**
   * Validates and parses an ID string.
   *
   * @param idString the ID string to parse
   * @param idType the type of ID (for error messages)
   * @return the parsed Long ID
   */
  private Long validateAndParseId(final String idString, final String idType) {
    try {
      return Long.parseLong(idString);
    } catch (NumberFormatException e) {
      throw new BusinessException("Invalid " + idType + " ID: " + idString);
    }
  }

  /**
   * Validates that a recipe ingredient exists.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @param recipeIdString the original recipe ID string for error messages
   * @param ingredientIdString the original ingredient ID string for error messages
   * @return the RecipeIngredient entity
   */
  private RecipeIngredient validateRecipeIngredient(
      final Long recipeId,
      final Long ingredientId,
      final String recipeIdString,
      final String ingredientIdString) {
    return recipeIngredientRepository
        .findByRecipeRecipeIdAndIngredientIngredientId(recipeId, ingredientId)
        .orElseThrow(
            () ->
                new BusinessException(
                    String.format(
                        "Recipe ingredient not found for recipe %s and ingredient %s",
                        recipeIdString, ingredientIdString)));
  }

  /**
   * Edit a comment on an ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @param request the edit comment request
   * @return response with updated comments
   */
  public ResponseEntity<IngredientCommentResponse> editComment(
      final String recipeId,
      final String ingredientId,
      final EditIngredientCommentRequest request) {

    final Long recipeIdLong = validateAndParseId(recipeId, "recipe");
    final Long ingredientIdLong = validateAndParseId(ingredientId, "ingredient");

    // Verify the recipe ingredient exists
    validateRecipeIngredient(recipeIdLong, ingredientIdLong, recipeId, ingredientId);

    // Find and update the specific comment
    final IngredientComment comment =
        ingredientCommentRepository
            .findByCommentIdAndIngredientIngredientId(request.getCommentId(), ingredientIdLong)
            .orElseThrow(
                () ->
                    new BusinessException("Comment not found with ID: " + request.getCommentId()));

    final String oldComment = comment.getCommentText();
    comment.setCommentText(request.getComment());
    ingredientCommentRepository.save(comment);

    // Get all comments for this ingredient
    final List<IngredientComment> comments =
        ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(
            ingredientIdLong);

    LOGGER.info(
        "Edited comment {} for ingredient {} in recipe {}: '{}' -> '{}'",
        request.getCommentId(),
        ingredientId,
        recipeId,
        oldComment,
        request.getComment());

    return ResponseEntity.ok(
        IngredientCommentResponse.builder()
            .recipeId(recipeIdLong)
            .ingredientId(ingredientIdLong)
            .comments(ingredientCommentMapper.toDtoList(comments))
            .build());
  }

  /**
   * Delete a comment from an ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @param request the delete comment request
   * @return response with updated comments
   */
  public ResponseEntity<IngredientCommentResponse> deleteComment(
      final String recipeId,
      final String ingredientId,
      final DeleteIngredientCommentRequest request) {

    final Long recipeIdLong = validateAndParseId(recipeId, "recipe");
    final Long ingredientIdLong = validateAndParseId(ingredientId, "ingredient");

    // Verify the recipe ingredient exists
    validateRecipeIngredient(recipeIdLong, ingredientIdLong, recipeId, ingredientId);

    // Find and delete the specific comment
    final IngredientComment comment =
        ingredientCommentRepository
            .findByCommentIdAndIngredientIngredientId(request.getCommentId(), ingredientIdLong)
            .orElseThrow(
                () ->
                    new BusinessException("Comment not found with ID: " + request.getCommentId()));

    final String deletedComment = comment.getCommentText();
    ingredientCommentRepository.delete(comment);

    // Get remaining comments for this ingredient
    final List<IngredientComment> comments =
        ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(
            ingredientIdLong);

    LOGGER.info(
        "Deleted comment {} for ingredient {} in recipe {}: '{}'",
        request.getCommentId(),
        ingredientId,
        recipeId,
        deletedComment);

    return ResponseEntity.ok(
        IngredientCommentResponse.builder()
            .recipeId(recipeIdLong)
            .ingredientId(ingredientIdLong)
            .comments(ingredientCommentMapper.toDtoList(comments))
            .build());
  }

  /**
   * Add media to an ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @return placeholder response
   */
  public ResponseEntity<String> addMedia(final String recipeId, final String ingredientId) {
    return ResponseEntity.ok("Add Media Ref to Ingredient - placeholder");
  }

  /**
   * Update media on an ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @return placeholder response
   */
  public ResponseEntity<String> updateMedia(final String recipeId, final String ingredientId) {
    return ResponseEntity.ok("Update Media Ref on Ingredient - placeholder");
  }

  /**
   * Delete media from an ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @return placeholder response
   */
  public ResponseEntity<String> deleteMedia(final String recipeId, final String ingredientId) {
    return ResponseEntity.ok("Delete Media Ref from Ingredient - placeholder");
  }
}
