package com.recipe_manager.model.dto.request;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating collection metadata.
 *
 * <p>All fields are optional to support partial updates. Only provided fields will be updated.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class UpdateCollectionRequest {

  /** Maximum length for collection name. */
  private static final int MAX_NAME_LENGTH = 255;

  /** Maximum length for collection description. */
  private static final int MAX_DESCRIPTION_LENGTH = 2000;

  /** Collection name. Must be between 1 and 255 characters if provided. */
  @Size(
      min = 1,
      max = MAX_NAME_LENGTH,
      message = "Collection name must be between 1 and 255 characters")
  private String name;

  /** Collection description. Maximum 2000 characters. Can be set to null to clear description. */
  @Size(
      max = MAX_DESCRIPTION_LENGTH,
      message = "Collection description must not exceed 2000 characters")
  private String description;

  /** Collection visibility setting. Controls who can view the collection. */
  private CollectionVisibility visibility;

  /** Collaboration mode. Controls who can edit the collection. */
  private CollaborationMode collaborationMode;
}
