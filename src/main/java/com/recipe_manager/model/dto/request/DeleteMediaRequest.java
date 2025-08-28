package com.recipe_manager.model.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Request DTO for deleting media. Contains the data needed for media deletion which will be
 * forwarded to the media-manager service and processed locally.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class DeleteMediaRequest {
  /** The unique ID of the media to delete. */
  @NotNull private Long mediaId;

  /** The user ID for authorization (must own the media). */
  @NotNull private UUID userId;
}
