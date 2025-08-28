package com.recipe_manager.model.dto.request;

import java.util.UUID;

import com.recipe_manager.model.enums.MediaType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Request DTO for creating new media. Contains the data needed to initiate media creation which
 * will be forwarded to the media-manager service.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class CreateMediaRequest {
  /** The user ID of the media owner. */
  @NotNull private UUID userId;

  /** The original filename when uploaded. */
  @NotBlank
  @Size(max = 255)
  private String originalFilename;

  /** The media type (MIME type). */
  @NotNull private MediaType mediaType;

  /** The file size in bytes. */
  @NotNull @Positive private Long fileSize;

  /** Optional content hash for integrity checking. */
  @Size(max = 64)
  private String contentHash;
}
