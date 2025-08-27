package com.recipe_manager.model.enums;

/**
 * Enum representing specific media formats supported by the media management service. Used to
 * identify the exact format of uploaded media files.
 */
public enum MediaFormat {
  // Image formats
  /** JPEG image format. */
  JPEG,
  /** PNG image format. */
  PNG,
  /** WebP image format. */
  WEBP,
  /** AVIF image format. */
  AVIF,
  /** GIF image format. */
  GIF,

  // Video formats
  /** MP4 video format. */
  MP4,
  /** WebM video format. */
  WEBM,
  /** MOV video format. */
  MOV,
  /** AVI video format. */
  AVI,

  // Audio formats
  /** MP3 audio format. */
  MP3,
  /** WAV audio format. */
  WAV,
  /** FLAC audio format. */
  FLAC,
  /** OGG audio format. */
  OGG
}
