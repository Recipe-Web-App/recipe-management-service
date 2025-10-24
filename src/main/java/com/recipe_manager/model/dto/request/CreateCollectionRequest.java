package com.recipe_manager.model.dto.request;

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
}
