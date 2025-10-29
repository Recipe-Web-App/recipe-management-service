package com.recipe_manager.model.dto.collection;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for CollectionCollaborator entity. Used for transferring collaborator data
 * between layers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionCollaboratorDto {
  /** The collection ID. */
  private Long collectionId;

  /** The collaborator user ID. */
  private UUID userId;

  /** The collaborator username. */
  private String username;

  /** The user ID who granted access. */
  private UUID grantedBy;

  /** The username of the user who granted access. */
  private String grantedByUsername;

  /** The timestamp when access was granted. */
  private LocalDateTime grantedAt;
}
