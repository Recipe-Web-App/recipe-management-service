package com.recipe_manager.model.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for adding a collaborator to a collection. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class AddCollaboratorRequest {
  /** The user ID to add as a collaborator. */
  @NotNull(message = "User ID is required")
  private UUID userId;
}
