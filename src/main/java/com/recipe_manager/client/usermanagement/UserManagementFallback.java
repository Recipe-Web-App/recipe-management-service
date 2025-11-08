package com.recipe_manager.client.usermanagement;

import java.util.ArrayList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.recipe_manager.model.dto.external.usermanagement.GetFollowersResponseDto;

/**
 * Fallback implementation for user-management service. Provides graceful degradation when the
 * user-management service is unavailable. Returns fallback responses with empty follower lists
 * instead of failing requests.
 *
 * <p>Since follower information is not critical to core recipe management functionality, the
 * fallback strategy is to log the failure and return an empty follower list. This allows
 * notifications to continue (albeit with no recipients) when follower data cannot be fetched.
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
}
