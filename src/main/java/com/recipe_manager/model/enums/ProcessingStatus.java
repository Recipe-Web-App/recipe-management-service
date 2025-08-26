package com.recipe_manager.model.enums;

/**
 * Enum representing the processing status of media content. Maps to the processing_status_enum in
 * the database.
 */
public enum ProcessingStatus {
  /** Media is pending processing. */
  PENDING,
  /** Media is currently being processed. */
  PROCESSING,
  /** Media processing completed successfully. */
  COMPLETE,
  /** Media processing failed. */
  FAILED
}
