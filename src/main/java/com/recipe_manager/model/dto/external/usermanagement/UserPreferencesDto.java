package com.recipe_manager.model.dto.external.usermanagement;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a user's complete set of preferences from the user-management service.
 *
 * <p>Contains notification, privacy, and display preferences.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesDto {

  /** Notification preferences (email, push, various notification types). */
  @JsonProperty("notification_preferences")
  private NotificationPreferencesDto notificationPreferences;

  /**
   * Privacy preferences (profile visibility, email/name visibility, follow/message permissions).
   *
   * <p>Critical for enforcing access control on user-specific data like favorites.
   */
  @JsonProperty("privacy_preferences")
  private PrivacyPreferencesDto privacyPreferences;

  /** Display preferences (theme, language, timezone). */
  @JsonProperty("display_preferences")
  private DisplayPreferencesDto displayPreferences;
}
