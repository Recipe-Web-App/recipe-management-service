package com.recipe_manager.model.dto.external.usermanagement;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing user notification preferences from the user-management service.
 *
 * <p>Contains boolean flags for various types of notifications that the user can enable or disable.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesDto {

  /** Whether to send email notifications. */
  @JsonProperty("email_notifications")
  private Boolean emailNotifications;

  /** Whether to send push notifications. */
  @JsonProperty("push_notifications")
  private Boolean pushNotifications;

  /** Whether to send notifications when someone follows the user. */
  @JsonProperty("follow_notifications")
  private Boolean followNotifications;

  /** Whether to send notifications when someone likes the user's content. */
  @JsonProperty("like_notifications")
  private Boolean likeNotifications;

  /** Whether to send notifications when someone comments on the user's content. */
  @JsonProperty("comment_notifications")
  private Boolean commentNotifications;

  /** Whether to send notifications related to recipes. */
  @JsonProperty("recipe_notifications")
  private Boolean recipeNotifications;

  /** Whether to send system-level notifications. */
  @JsonProperty("system_notifications")
  private Boolean systemNotifications;
}
