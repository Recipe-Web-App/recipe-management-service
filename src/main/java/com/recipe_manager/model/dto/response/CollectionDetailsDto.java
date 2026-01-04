package com.recipe_manager.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.collection.CollectionCollaboratorDto;
import com.recipe_manager.model.dto.collection.CollectionRecipeDto;
import com.recipe_manager.model.dto.collection.CollectionTagDto;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for collection details endpoint matching OpenAPI specification. Contains full
 * collection metadata and list of recipes with their display order.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class CollectionDetailsDto {
  /** The unique ID of the collection. */
  private Long collectionId;

  /** The user ID of the collection owner. */
  private UUID userId;

  /** The collection name. */
  private String name;

  /** The optional description. */
  private String description;

  /** The visibility level. */
  private CollectionVisibility visibility;

  /** The collaboration mode. */
  private CollaborationMode collaborationMode;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /** List of recipes in this collection (ordered by displayOrder). */
  private List<CollectionRecipeDto> recipes;

  /** List of collaborators for this collection. */
  private List<CollectionCollaboratorDto> collaborators;

  /** List of tags for this collection. */
  private List<CollectionTagDto> tags;
}
