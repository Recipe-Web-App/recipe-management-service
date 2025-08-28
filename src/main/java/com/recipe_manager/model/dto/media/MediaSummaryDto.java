package com.recipe_manager.model.dto.media;

import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

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
 * Lightweight Data Transfer Object for Media entity. Contains basic media information for listings
 * and summary displays.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class MediaSummaryDto {
  /** The unique ID of the media. */
  private Long mediaId;

  /** The original filename when uploaded. */
  private String originalFilename;

  /** The media type (MIME type). */
  @NotNull private MediaType mediaType;

  /** The file size in bytes. */
  private Long fileSize;

  /** The processing status of the media. */
  @NotNull private ProcessingStatus processingStatus;
}
