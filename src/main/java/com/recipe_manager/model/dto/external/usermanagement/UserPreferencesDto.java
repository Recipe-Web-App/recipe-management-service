package com.recipe_manager.model.dto.external.usermanagement;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a user's preferences response from the user-management service.
 *
 * <p>Contains various preference categories. For minimal implementation, only privacy preferences
 * are used by this service for access control.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesDto {

  /** User ID associated with these preferences. */
  @JsonProperty("userId")
  private UUID userId;

  /**
   * Privacy preferences (profile visibility, recipe visibility, etc.).
   *
   * <p>Critical for enforcing access control on user-specific data like favorites.
   */
  @JsonProperty("privacy")
  private PrivacyPreferencesDto privacy;
}
