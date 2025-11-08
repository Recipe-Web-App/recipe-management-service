package com.recipe_manager.model.dto.external.usermanagement;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a user from the user-management service.
 *
 * <p>Contains minimal user information needed for notification purposes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  /** Unique identifier for the user. */
  @JsonProperty("userId")
  private UUID userId;

  /** Username for the user account. */
  @JsonProperty("username")
  private String username;

  /** Email address for the user account (nullable). */
  @JsonProperty("email")
  private String email;

  /** Full name of the user (nullable). */
  @JsonProperty("fullName")
  private String fullName;

  /** Whether the user account is active. */
  @JsonProperty("isActive")
  private Boolean isActive;
}
