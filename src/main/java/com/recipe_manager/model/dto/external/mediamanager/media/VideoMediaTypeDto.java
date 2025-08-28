package com.recipe_manager.model.dto.external.mediamanager.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe_manager.model.enums.MediaFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** DTO representing video-specific metadata including format, dimensions, and duration. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class VideoMediaTypeDto {

  /** Video format (MP4, WebM, MOV, AVI). */
  @JsonProperty("format")
  private MediaFormat format;

  /** Video width in pixels. */
  @JsonProperty("width")
  private Integer width;

  /** Video height in pixels. */
  @JsonProperty("height")
  private Integer height;

  /** Video duration in seconds, nullable if not available. */
  @JsonProperty("duration_seconds")
  private Integer durationSeconds;
}
