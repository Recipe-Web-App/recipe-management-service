package com.recipe_manager.model.dto.collection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for RecipeCollection entity. Used for transferring collection data between
 * layers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class RecipeCollectionDto {
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

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /** The list of items in this collection. */
  @Valid @Default private List<RecipeCollectionItemDto> items = new ArrayList<>();

  /** The list of collaborators. */
  @Valid @Default private List<CollectionCollaboratorDto> collaborators = new ArrayList<>();
}
