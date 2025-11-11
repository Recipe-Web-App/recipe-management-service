package com.recipe_manager.model.dto.external.usermanagement;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for getting user preferences from the user-management service.
 *
 * <p>Contains the complete set of user preferences including notification, privacy, and display
 * settings.
 *
 * <p>Privacy preferences are particularly important for enforcing access control on user-specific
 * data like favorite recipes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceResponseDto {

  /**
   * User preferences object containing notification, privacy, and display settings.
   *
   * <p>The privacy_preferences.profile_visibility field determines who can access the user's
   * favorite recipes.
   */
  @JsonProperty("preferences")
  private UserPreferencesDto preferences;
}
