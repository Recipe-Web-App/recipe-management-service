package com.recipe_manager.model.dto.external.mediamanager.media;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Wrapper DTO for media type information. Contains one of Image, Video, or Audio metadata depending
 * on the media file type.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class MediaTypeDto {

  /** Image metadata if this is an image file. */
  @JsonProperty("Image")
  private ImageMediaTypeDto image;

  /** Video metadata if this is a video file. */
  @JsonProperty("Video")
  private VideoMediaTypeDto video;

  /** Audio metadata if this is an audio file. */
  @JsonProperty("Audio")
  private AudioMediaTypeDto audio;
}
