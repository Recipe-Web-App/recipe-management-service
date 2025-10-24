package com.recipe_manager.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for collection data matching OpenAPI specification. Used for API responses to
 * external clients.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class CollectionDto {
  /** The unique ID of the collection. */
  private Long collectionId;

  /** The user ID of the collection owner. */
  private UUID userId;

  /** The name of the collection. */
  private String name;

  /** The optional description of the collection. */
  private String description;

  /** The visibility level of the collection. */
  private CollectionVisibility visibility;

  /** The collaboration mode. */
  private CollaborationMode collaborationMode;

  /** Count of recipes in the collection. */
  private Integer recipeCount;

  /** Count of collaborators. */
  private Integer collaboratorCount;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;
}
