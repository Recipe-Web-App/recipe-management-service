package com.recipe_manager.client.usermanagement;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.recipe_manager.client.common.FeignClientConfig;
import com.recipe_manager.model.dto.external.usermanagement.GetFollowersResponseDto;

/**
 * Feign client for user-management service. Provides declarative HTTP client interface for
 * interacting with the external user-management service to fetch user follower information.
 *
 * <p>This client is used to retrieve the list of followers for a given user, which is needed for
 * sending notifications to followers when a recipe is published.
 */
@FeignClient(
    name = "user-management-service",
    url = "${external.services.user-management.base-url}",
    configuration = FeignClientConfig.class,
    fallback = UserManagementFallback.class)
public interface UserManagementClient {

  /**
   * Get followers for a specific user. Retrieves the list of users following the specified user.
   *
   * <p>This endpoint supports pagination and can return either the full list of followers or just
   * the count.
   *
   * <p>Requires OAuth2 Bearer token authentication.
   *
   * @param userId the ID of the user whose followers to retrieve
   * @param limit number of results to return (1-100, default: 20)
   * @param offset number of results to skip (min: 0, default: 0)
   * @param countOnly return only the count of results (default: false)
   * @return response containing follower list and pagination information
   */
  @GetMapping("/user-management/users/{user_id}/followers")
  GetFollowersResponseDto getFollowers(
      @PathVariable("user_id") UUID userId,
      @RequestParam(value = "limit", required = false) Integer limit,
      @RequestParam(value = "offset", required = false) Integer offset,
      @RequestParam(value = "count_only", required = false) Boolean countOnly);
}
