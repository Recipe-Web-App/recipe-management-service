package com.recipe_manager.model.dto.external.usermanagement;

import java.time.OffsetDateTime;

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
  @JsonProperty("profileVisibility")
  private ProfileVisibilityEnum profileVisibility;

  /** Visibility level for user's recipes. */
  @JsonProperty("recipeVisibility")
  private ProfileVisibilityEnum recipeVisibility;

  /** Visibility level for user's activity. */
  @JsonProperty("activityVisibility")
  private ProfileVisibilityEnum activityVisibility;

  /** Visibility level for user's contact information. */
  @JsonProperty("contactInfoVisibility")
  private ProfileVisibilityEnum contactInfoVisibility;

  /** Whether to allow data sharing with partners. */
  @JsonProperty("dataSharing")
  private Boolean dataSharing;

  /** Whether to allow analytics tracking. */
  @JsonProperty("analyticsTracking")
  private Boolean analyticsTracking;

  /** Last update timestamp. */
  @JsonProperty("updatedAt")
  private OffsetDateTime updatedAt;
}
