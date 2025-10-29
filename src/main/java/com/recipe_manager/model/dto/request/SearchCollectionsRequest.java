package com.recipe_manager.model.dto.request;

import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for searching collections with advanced filters. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class SearchCollectionsRequest {
  /** Text search query for collection name or description. */
  private String query;

  /** Filter by visibility types. */
  private List<CollectionVisibility> visibility;

  /** Filter by collaboration modes. */
  private List<CollaborationMode> collaborationMode;

  /** Filter by collection owner. */
  private UUID ownerUserId;

  /** Minimum number of recipes in collection. */
  @Min(value = 0, message = "Minimum recipe count must be non-negative")
  private Integer minRecipeCount;

  /** Maximum number of recipes in collection. */
  @Min(value = 0, message = "Maximum recipe count must be non-negative")
  private Integer maxRecipeCount;
}
