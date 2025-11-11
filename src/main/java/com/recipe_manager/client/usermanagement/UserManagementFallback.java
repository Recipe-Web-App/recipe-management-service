package com.recipe_manager.client.usermanagement;

import java.util.ArrayList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.recipe_manager.model.dto.external.usermanagement.DisplayPreferencesDto;
import com.recipe_manager.model.dto.external.usermanagement.GetFollowersResponseDto;
import com.recipe_manager.model.dto.external.usermanagement.NotificationPreferencesDto;
import com.recipe_manager.model.dto.external.usermanagement.PrivacyPreferencesDto;
import com.recipe_manager.model.dto.external.usermanagement.UserPreferenceResponseDto;
import com.recipe_manager.model.dto.external.usermanagement.UserPreferencesDto;
import com.recipe_manager.model.enums.ProfileVisibilityEnum;
import com.recipe_manager.model.enums.ThemeEnum;

/**
 * Fallback implementation for user-management service. Provides graceful degradation when the
 * user-management service is unavailable. Returns fallback responses instead of failing requests.
 *
 * <p>Fallback strategies:
 *
 * <ul>
 *   <li>Followers: Return empty follower list (non-critical data)
 *   <li>User Preferences: Return strictest privacy defaults (PRIVATE profile) to protect user
 *       privacy when service is down
 * </ul>
 */
@Component
public final class UserManagementFallback implements UserManagementClient {

  /** Logger for fallback operations. */
  private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementFallback.class);

  /**
   * Fallback for getting followers. Logs the failure and returns an empty follower list.
   *
   * @param userId the ID of the user whose followers were requested
   * @param limit pagination limit (ignored in fallback)
   * @param offset pagination offset (ignored in fallback)
   * @param countOnly whether to return count only (ignored in fallback)
   * @return fallback response with empty follower list and totalCount=0
   */
  @Override
  public GetFollowersResponseDto getFollowers(
      final UUID userId, final Integer limit, final Integer offset, final Boolean countOnly) {
    LOGGER.warn(
        "User management service unavailable for getting followers. "
            + "User ID: {}, Limit: {}, Offset: {}, Count Only: {}. "
            + "Returning empty follower list.",
        userId,
        limit,
        offset,
        countOnly);

    // Return empty follower list since service is unavailable
    // This prevents notification failures but means no followers will be notified
    return GetFollowersResponseDto.builder()
        .totalCount(0)
        .followedUsers(new ArrayList<>())
        .limit(limit)
        .offset(offset)
        .build();
  }

  /**
   * Fallback for getting user preferences. Logs the failure and returns strictest privacy defaults
   * to protect user privacy when the service is unavailable.
   *
   * <p>Privacy-first approach: Returns PRIVATE profile visibility by default, ensuring user data
   * (like favorites) is not accidentally exposed when the user-management service is down.
   *
   * @return fallback response with PRIVATE profile visibility and all notifications disabled
   */
  @Override
  public UserPreferenceResponseDto getUserPreferences() {
    LOGGER.warn(
        "User management service unavailable for getting user preferences. "
            + "Returning strictest privacy defaults (PRIVATE profile) to protect user privacy.");

    // Return strictest defaults to protect user privacy (fail-secure approach)
    return UserPreferenceResponseDto.builder()
        .preferences(
            UserPreferencesDto.builder()
                .privacyPreferences(
                    PrivacyPreferencesDto.builder()
                        .profileVisibility(ProfileVisibilityEnum.PRIVATE) // STRICTEST setting
                        .showEmail(false)
                        .showFullName(false)
                        .allowFollows(false)
                        .allowMessages(false)
                        .build())
                .notificationPreferences(
                    NotificationPreferencesDto.builder()
                        .emailNotifications(false)
                        .pushNotifications(false)
                        .followNotifications(false)
                        .likeNotifications(false)
                        .commentNotifications(false)
                        .recipeNotifications(false)
                        .systemNotifications(false)
                        .build())
                .displayPreferences(
                    DisplayPreferencesDto.builder()
                        .theme(ThemeEnum.AUTO)
                        .language("en")
                        .timezone("UTC")
                        .build())
                .build())
        .build();
  }
}
