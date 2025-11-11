package com.recipe_manager.model.dto.external.usermanagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe_manager.model.enums.ProfileVisibilityEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing user privacy preferences from the user-management service.
 *
 * <p>Contains settings that control who can view the user's profile and data, including their
 * favorite recipes.
 *
 * <p>The profileVisibility field is critical for enforcing access control on user-specific data
 * like favorites.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyPreferencesDto {

  /**
   * Profile visibility level controlling who can view the user's profile and favorites.
   *
   * <p>Possible values:
   *
   * <ul>
   *   <li>PUBLIC: Anyone can view
   *   <li>FRIENDS_ONLY: Only followers can view
   *   <li>PRIVATE: Only the user themselves can view
   * </ul>
   */
  @JsonProperty("profile_visibility")
  private ProfileVisibilityEnum profileVisibility;

  /** Whether to show the user's email address on their profile. */
  @JsonProperty("show_email")
  private Boolean showEmail;

  /** Whether to show the user's full name on their profile. */
  @JsonProperty("show_full_name")
  private Boolean showFullName;

  /** Whether to allow other users to follow this user. */
  @JsonProperty("allow_follows")
  private Boolean allowFollows;

  /** Whether to allow other users to send messages to this user. */
  @JsonProperty("allow_messages")
  private Boolean allowMessages;
}
