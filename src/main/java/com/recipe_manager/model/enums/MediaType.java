package com.recipe_manager.model.enums;

/** Enum representing the media MIME types. Maps to the media_type_enum in the database. */
public enum MediaType {
  // Image Formats
  /** JPEG image format. */
  IMAGE_JPEG("image/jpeg"),
  /** PNG image format. */
  IMAGE_PNG("image/png"),
  /** GIF image format. */
  IMAGE_GIF("image/gif"),
  /** WebP image format. */
  IMAGE_WEBP("image/webp"),
  /** AVIF image format. */
  IMAGE_AVIF("image/avif"),
  /** SVG image format. */
  IMAGE_SVG_XML("image/svg+xml"),
  /** HEIC image format. */
  IMAGE_HEIC("image/heic"),
  /** TIFF image format. */
  IMAGE_TIFF("image/tiff"),

  // Video Formats
  /** MP4 video format. */
  VIDEO_MP4("video/mp4"),
  /** WebM video format. */
  VIDEO_WEBM("video/webm"),
  /** OGG video format. */
  VIDEO_OGG("video/ogg"),
  /** QuickTime video format. */
  VIDEO_QUICKTIME("video/quicktime");

  /** The MIME type string. */
  private final String mimeType;

  MediaType(final String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * Gets the MIME type as a string.
   *
   * @return MIME type
   */
  public String getMimeType() {
    return mimeType;
  }

  @Override
  public String toString() {
    return mimeType;
  }
}
