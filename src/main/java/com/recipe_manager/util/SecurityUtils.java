package com.recipe_manager.util;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Utility class for security and authentication context operations. */
public final class SecurityUtils {

  private SecurityUtils() {
    // Utility class - prevent instantiation
  }

  /**
   * Returns the UUID of the currently authenticated user from the security context.
   *
   * @return the user UUID
   * @throws IllegalStateException if the authentication is missing or the user ID is not a valid
   *     UUID
   */
  public static UUID getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication.getName() == null) {
      throw new IllegalStateException("No authenticated user found in security context");
    }
    try {
      return UUID.fromString(authentication.getName());
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("Authenticated user ID is not a valid UUID", e);
    }
  }
}
