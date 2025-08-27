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

/** DTO representing audio-specific metadata including format, duration, and bitrate. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class AudioMediaTypeDto {

  /** Audio format (MP3, WAV, FLAC, OGG). */
  @JsonProperty("format")
  private MediaFormat format;

  /** Audio duration in seconds, nullable if not available. */
  @JsonProperty("duration_seconds")
  private Integer durationSeconds;

  /** Audio bitrate in bits per second, nullable if not available. */
  @JsonProperty("bitrate")
  private Integer bitrate;
}
