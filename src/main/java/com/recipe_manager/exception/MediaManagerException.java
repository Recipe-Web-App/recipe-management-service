package com.recipe_manager.exception;

import com.recipe_manager.model.enums.ExternalServiceName;

/**
 * Specific exception for media manager service failures. Provides additional context for media
 * manager-specific errors.
 */
public class MediaManagerException extends ExternalServiceException {

  /** Media ID associated with the error, if available. */
  private final Long mediaId;

  public MediaManagerException(final String message) {
    super(ExternalServiceName.MEDIA_SERVICE, message);
    this.mediaId = null;
  }

  public MediaManagerException(final String message, final Throwable cause) {
    super(ExternalServiceName.MEDIA_SERVICE, message, cause);
    this.mediaId = null;
  }

  public MediaManagerException(final Long mediaId, final String message) {
    super(
        ExternalServiceName.MEDIA_SERVICE,
        String.format("Media manager error for media %d: %s", mediaId, message));
    this.mediaId = mediaId;
  }

  public MediaManagerException(final Long mediaId, final String message, final Throwable cause) {
    super(
        ExternalServiceName.MEDIA_SERVICE,
        String.format("Media manager error for media %d: %s", mediaId, message),
        cause);
    this.mediaId = mediaId;
  }

  public MediaManagerException(final Long mediaId, final int statusCode, final String message) {
    super(
        ExternalServiceName.MEDIA_SERVICE,
        statusCode,
        String.format("Media manager error for media %d: %s", mediaId, message));
    this.mediaId = mediaId;
  }

  /**
   * Gets the media ID associated with this error, if available.
   *
   * @return media ID, or null if not available
   */
  public Long getMediaId() {
    return mediaId;
  }
}
