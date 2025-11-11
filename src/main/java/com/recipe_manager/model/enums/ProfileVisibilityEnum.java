package com.recipe_manager.model.enums;

/**
 * Enum representing the visibility level of a user's profile and associated data (such as
 * favorites). Controls who can view a user's profile information and favorites list.
 *
 * <p>Maps to the profile_visibility_enum in the database and the user-management-service API.
 *
 * <p>Used to enforce privacy controls when accessing user-specific data like favorite recipes.
 */
public enum ProfileVisibilityEnum {
  /** Anyone can view the user's profile and favorites. */
  PUBLIC,
  /** Only users who follow this user can view their profile and favorites. */
  FRIENDS_ONLY,
  /** Only the user themselves can view their profile and favorites. */
  PRIVATE
}
