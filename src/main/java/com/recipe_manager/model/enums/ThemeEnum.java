package com.recipe_manager.model.enums;

/**
 * Enum representing the user's preferred theme for the application UI.
 *
 * <p>Maps to the theme_enum in the database and the user-management-service API.
 *
 * <p>Used in display preferences to control the visual appearance of the application.
 */
public enum ThemeEnum {
  /** Light theme with bright background and dark text. */
  LIGHT,
  /** Dark theme with dark background and light text. */
  DARK,
  /** Automatically switch between light and dark based on system settings. */
  AUTO
}
