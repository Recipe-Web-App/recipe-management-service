package com.recipe_manager.model.dto.external.usermanagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe_manager.model.enums.ThemeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing user display preferences from the user-management service.
 *
 * <p>Contains settings that control the visual appearance and localization of the application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayPreferencesDto {

  /**
   * UI theme preference.
   *
   * <p>Possible values:
   *
   * <ul>
   *   <li>LIGHT: Light theme with bright background
   *   <li>DARK: Dark theme with dark background
   *   <li>AUTO: Automatically switch based on system settings
   * </ul>
   */
  @JsonProperty("theme")
  private ThemeEnum theme;

  /**
   * Preferred language code (ISO 639-1).
   *
   * <p>Examples: "en", "es", "fr", "de", etc.
   */
  @JsonProperty("language")
  private String language;

  /**
   * Preferred timezone (IANA timezone identifier).
   *
   * <p>Examples: "UTC", "America/New_York", "Europe/London", etc.
   */
  @JsonProperty("timezone")
  private String timezone;
}
