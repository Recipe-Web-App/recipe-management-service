package com.recipe_manager.model.dto.request;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for creating a new recipe collection. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class CreateCollectionRequest {
  /** Max name length as defined in DB schema. */
  private static final int MAX_NAME_LENGTH = 255;

  /** The name of the collection. */
  @NotBlank(message = "Collection name is required")
  @Size(max = MAX_NAME_LENGTH, message = "Collection name must not exceed 255 characters")
  private String name;

  /** The optional description of the collection. */
  private String description;

  /** The visibility level of the collection. */
  @NotNull(message = "Visibility is required")
  private CollectionVisibility visibility;

  /** The collaboration mode. */
  @NotNull(message = "Collaboration mode is required")
  private CollaborationMode collaborationMode;

  /** Optional list of recipe IDs to add to the collection during creation. */
  @Builder.Default private List<Long> recipeIds = new ArrayList<>();

  /**
   * Optional list of user IDs to add as collaborators during creation. Only applicable when
   * collaborationMode is SPECIFIC_USERS.
   */
  @Builder.Default private List<UUID> collaboratorIds = new ArrayList<>();

  /** Optional list of tag names to add to the collection during creation. */
  @Builder.Default private List<String> tags = new ArrayList<>();
}
