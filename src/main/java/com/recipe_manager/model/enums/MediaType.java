package com.recipe_manager.model.enums;

/**
 * Enum representing media MIME types supported by the system. Maps to the media_type_enum in the
 * database.
 */
public enum MediaType {
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
  /** MP4 video format. */
  VIDEO_MP4("video/mp4"),
  /** WebM video format. */
  VIDEO_WEBM("video/webm"),
  /** OGG video format. */
  VIDEO_OGG("video/ogg"),
  /** QuickTime video format. */
  VIDEO_QUICKTIME("video/quicktime");

  /** The MIME type string for this media type. */
  private final String mimeType;

  MediaType(final String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * Gets the MIME type string for this media type.
   *
   * @return the MIME type string
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * Finds a MediaType by its MIME type string.
   *
   * @param mimeType the MIME type string to search for
   * @return the corresponding MediaType, or null if not found
   */
  public static MediaType fromMimeType(final String mimeType) {
    if (mimeType == null) {
      return null;
    }
    for (MediaType mediaType : values()) {
      if (mediaType.mimeType.equals(mimeType)) {
        return mediaType;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return mimeType;
  }
}
