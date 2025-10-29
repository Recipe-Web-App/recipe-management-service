package com.recipe_manager.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response DTO for collection summary. Lightweight version without nested data. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class CollectionSummaryResponse {
  /** The unique ID of the collection. */
  private Long collectionId;

  /** The name of the collection. */
  private String name;

  /** The optional description of the collection. */
  private String description;

  /** The visibility level of the collection. */
  private CollectionVisibility visibility;

  /** The collaboration mode. */
  private CollaborationMode collaborationMode;

  /** The user ID of the collection owner. */
  private UUID ownerId;

  /** The username of the collection owner (optional). */
  private String ownerUsername;

  /** Count of recipes in the collection. */
  private Integer recipeCount;

  /** Count of collaborators. */
  private Integer collaboratorCount;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;
}
