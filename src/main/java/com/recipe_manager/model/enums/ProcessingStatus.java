package com.recipe_manager.model.enums;

/**
 * Enum representing the processing status of media content. Maps to the processing_status_enum in
 * the database.
 */
public enum ProcessingStatus {
  /** Media upload initiated but not started. */
  INITIATED,
  /** Media upload in progress. */
  UPLOADING,
  /** Media upload is processing. */
  PROCESSING,
  /** Media upload completed successfully. */
  COMPLETE,
  /** Media upload failed. */
  FAILED,
  /** Media upload session expired. */
  EXPIRED
}
