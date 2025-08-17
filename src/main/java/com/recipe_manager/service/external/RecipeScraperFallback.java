package com.recipe_manager.service.external;

import java.math.BigDecimal;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.recipe_manager.client.recipescraper.RecipeScraperClient;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;

/**
 * Fallback implementation for recipe scraper service. Provides graceful degradation when the recipe
 * scraper service is unavailable, returning empty pricing data instead of failing the request. This
 * implementation is co-located with RecipeScraperService for better code organization and
 * maintenance of related functionality.
 */
@Component
public final class RecipeScraperFallback implements RecipeScraperClient {

  /** Logger for fallback operations. */
  private static final Logger LOGGER = LoggerFactory.getLogger(RecipeScraperFallback.class);

  /**
   * Provides fallback shopping information when the recipe scraper service is unavailable.
   *
   * @param recipeId the recipe ID to get shopping info for
   * @return shopping data with empty pricing information
   */
  @Override
  public RecipeScraperShoppingDto getShoppingInfo(final Long recipeId) {
    LOGGER.warn(
        "Recipe scraper service unavailable for recipe {}, using fallback with no pricing data",
        recipeId);

    return RecipeScraperShoppingDto.builder()
        .recipeId(recipeId)
        .ingredients(Collections.emptyMap())
        .totalEstimatedCost(BigDecimal.ZERO)
        .build();
  }
}
