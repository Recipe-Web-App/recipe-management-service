package com.recipe_manager.client.notificationservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.recipe_manager.client.common.FeignClientConfig;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeCommentedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeLikedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipePublishedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.response.BatchNotificationResponseDto;

/**
 * Feign client for notification service. Provides declarative HTTP client interface for interacting
 * with the external notification service to send recipe-related email notifications.
 *
 * <p>All endpoints return HTTP 202 Accepted as notifications are processed asynchronously via a
 * queue. The notification service fetches entity details from downstream services using provided
 * IDs.
 */
@FeignClient(
    name = "notification-service",
    url = "${external.services.notification-service.base-url}",
    configuration = FeignClientConfig.class,
    fallback =
        com.recipe_manager.service.external.notificationservice.NotificationServiceFallback.class)
public interface NotificationServiceClient {

  /**
   * Notify followers when a recipe is published. Sends email notifications to all followers of the
   * recipe author.
   *
   * <p>The notification service fetches recipe details (name, author) from
   * recipe-management-service and constructs URLs using FRONTEND_BASE_URL configuration.
   *
   * <p>Requires <strong>notification:admin</strong> scope OR <strong>notification:user</strong>
   * scope with valid follower relationships.
   *
   * @param request contains recipient IDs (followers) and recipe ID
   * @return batch response with queued notification IDs mapped to recipients
   */
  @PostMapping("/notifications/recipe-published")
  BatchNotificationResponseDto notifyRecipePublished(
      @RequestBody RecipePublishedRequestDto request);

  /**
   * Notify recipe author when someone likes their recipe. Sends email notification to the recipe
   * author with liker information.
   *
   * <p>The notification service fetches recipe details from recipe-management-service and liker
   * details from user-management-service.
   *
   * <p>Requires <strong>notification:admin</strong> scope OR <strong>notification:user</strong>
   * scope with valid follower relationship.
   *
   * @param request contains recipient IDs (typically recipe author), recipe ID, and liker ID
   * @return batch response with queued notification IDs mapped to recipients
   */
  @PostMapping("/notifications/recipe-liked")
  BatchNotificationResponseDto notifyRecipeLiked(@RequestBody RecipeLikedRequestDto request);

  /**
   * Notify recipe author when someone comments on their recipe. Sends email notification to the
   * recipe author with comment preview.
   *
   * <p>The notification service fetches complete comment details (including recipe_id, commenter
   * info, comment text) from recipe-management-service.
   *
   * <p>Requires <strong>notification:admin</strong> scope OR <strong>notification:user</strong>
   * scope with valid follower relationship.
   *
   * @param request contains recipient IDs (typically recipe author) and comment ID
   * @return batch response with queued notification IDs mapped to recipients
   */
  @PostMapping("/notifications/recipe-commented")
  BatchNotificationResponseDto notifyRecipeCommented(
      @RequestBody RecipeCommentedRequestDto request);
}
