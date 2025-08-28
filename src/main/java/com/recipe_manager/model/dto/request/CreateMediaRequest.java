package com.recipe_manager.model.dto.request;

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
 * will be forwarded to the media-manager service. User ID is extracted from security context.
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
  /** Maximum allowed length for filenames. */
  public static final int MAX_FILENAME_LENGTH = 255;

  /** Maximum allowed length for content hashes. */
  public static final int MAX_CONTENT_HASH_LENGTH = 64;

  /** The original filename when uploaded. */
  @NotBlank
  @Size(max = MAX_FILENAME_LENGTH)
  private String originalFilename;

  /** The media type (MIME type). */
  @NotNull private MediaType mediaType;

  /** The file size in bytes. */
  @NotNull @Positive private Long fileSize;

  /** Optional content hash for integrity checking. */
  @Size(max = MAX_CONTENT_HASH_LENGTH)
  private String contentHash;
}
