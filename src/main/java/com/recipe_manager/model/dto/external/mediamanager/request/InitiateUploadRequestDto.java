package com.recipe_manager.model.dto.external.mediamanager.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
 * Request DTO for initiating a presigned upload session with the media management service. Creates
 * a secure, time-limited upload URL for client-side file uploads.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class InitiateUploadRequestDto {

  /** Original filename as uploaded, validated for security. */
  @JsonProperty("filename")
  @NotBlank(message = "Filename is required")
  @Size(min = 1, max = 255, message = "Filename must be between 1 and 255 characters")
  private String filename;

  /** MIME content type of the file to be uploaded. */
  @JsonProperty("content_type")
  @NotBlank(message = "Content type is required")
  @Pattern(
      regexp = "^[a-zA-Z0-9][a-zA-Z0-9!#$&\\-\\^_]*\\/[a-zA-Z0-9][a-zA-Z0-9!#$&\\-\\^_.]*$",
      message = "Content type must be a valid MIME type format")
  private String contentType;

  /** File size in bytes, with a maximum of 50MB by default. */
  @JsonProperty("file_size")
  @NotNull(message = "File size is required")
  @Min(value = 1, message = "File size must be at least 1 byte")
  @Max(value = 52428800, message = "File size cannot exceed 50MB (52428800 bytes)")
  private Long fileSize;
}
