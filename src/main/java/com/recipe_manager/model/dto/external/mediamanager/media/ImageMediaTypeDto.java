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

/** DTO representing image-specific metadata including format, width, and height dimensions. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class ImageMediaTypeDto {

  /** Image format (JPEG, PNG, WebP, AVIF, GIF). */
  @JsonProperty("format")
  private MediaFormat format;

  /** Image width in pixels. */
  @JsonProperty("width")
  private Integer width;

  /** Image height in pixels. */
  @JsonProperty("height")
  private Integer height;
}
