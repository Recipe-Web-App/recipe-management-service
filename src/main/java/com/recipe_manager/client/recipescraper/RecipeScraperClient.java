package com.recipe_manager.client.recipescraper;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.recipe_manager.client.common.FeignClientConfig;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;

/**
 * Feign client for recipe scraper service. Provides declarative HTTP client interface for
 * retrieving pricing and shopping information from the external recipe scraper service.
 */
@FeignClient(
    name = "recipe-scraper",
    url = "${external.services.recipe-scraper.base-url}",
    configuration = FeignClientConfig.class,
    fallback = com.recipe_manager.service.external.RecipeScraperFallback.class)
public interface RecipeScraperClient {

  /**
   * Retrieves shopping information including pricing for a specific recipe.
   *
   * @param recipeId the recipe ID to get shopping info for
   * @return shopping information with pricing details
   */
  @GetMapping("${external.services.recipe-scraper.shopping-info-path}")
  RecipeScraperShoppingDto getShoppingInfo(@PathVariable("recipeId") Long recipeId);
}
