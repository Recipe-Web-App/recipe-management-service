package com.recipe_manager.model.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.recipe_manager.model.dto.recipe.RecipeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for recipe search results.
 *
 * <p>Contains paginated search results along with metadata about the search operation. This
 * provides a structured response that includes both the recipe data and pagination information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchRecipesResponse {

  /** List of recipes matching the search criteria. */
  private List<RecipeDto> recipes;

  /** Current page number (0-based). */
  private int page;

  /** Number of items per page. */
  private int size;

  /** Total number of recipes matching the search criteria. */
  private long totalElements;

  /** Total number of pages available. */
  private int totalPages;

  /** Whether this is the first page. */
  private boolean first;

  /** Whether this is the last page. */
  private boolean last;

  /** Number of recipes in the current page. */
  private int numberOfElements;

  /** Whether the current page is empty. */
  private boolean empty;
}
