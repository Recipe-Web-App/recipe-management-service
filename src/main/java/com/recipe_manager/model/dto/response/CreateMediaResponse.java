package com.recipe_manager.model.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
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
 * Response DTO for media creation operations. Contains the media ID and upload URL returned from
 * the media-manager service.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateMediaResponse {
  /** The unique ID of the created media record. */
  @NotNull private Long mediaId;

  /** The upload URL provided by the media-manager service. */
  @NotBlank private String uploadUrl;

  /** When the upload URL expires. */
  private LocalDateTime expiresAt;

  /** Optional content hash for integrity verification. */
  private String contentHash;
}
