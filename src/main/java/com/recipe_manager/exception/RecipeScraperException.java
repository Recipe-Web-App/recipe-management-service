package com.recipe_manager.exception;

import com.recipe_manager.model.enums.ExternalServiceName;

/**
 * Specific exception for recipe scraper service failures. Provides additional context for recipe
 * scraper-specific errors.
 */
public class RecipeScraperException extends ExternalServiceException {

  /** Recipe ID associated with the error, if available. */
  private final Long recipeId;

  public RecipeScraperException(final String message) {
    super(ExternalServiceName.RECIPE_SCRAPER, message);
    this.recipeId = null;
  }

  public RecipeScraperException(final String message, final Throwable cause) {
    super(ExternalServiceName.RECIPE_SCRAPER, message, cause);
    this.recipeId = null;
  }

  public RecipeScraperException(final Long recipeId, final String message) {
    super(
        ExternalServiceName.RECIPE_SCRAPER,
        String.format("Recipe scraper error for recipe %d: %s", recipeId, message));
    this.recipeId = recipeId;
  }

  public RecipeScraperException(final Long recipeId, final String message, final Throwable cause) {
    super(
        ExternalServiceName.RECIPE_SCRAPER,
        String.format("Recipe scraper error for recipe %d: %s", recipeId, message),
        cause);
    this.recipeId = recipeId;
  }

  public RecipeScraperException(final Long recipeId, final int statusCode, final String message) {
    super(
        ExternalServiceName.RECIPE_SCRAPER,
        statusCode,
        String.format("Recipe scraper error for recipe %d: %s", recipeId, message));
    this.recipeId = recipeId;
  }

  /**
   * Gets the recipe ID associated with this error, if available.
   *
   * @return recipe ID, or null if not available
   */
  public Long getRecipeId() {
    return recipeId;
  }
}
