package com.recipe_manager.service.external.notificationservice;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.recipe_manager.client.notificationservice.NotificationServiceClient;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeCommentedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeLikedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipePublishedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.response.BatchNotificationResponseDto;

/**
 * Fallback implementation for notification service. Provides graceful degradation when the
 * notification service is unavailable. Returns fallback responses indicating the service is
 * unavailable instead of failing requests.
 *
 * <p>Since notifications are not critical to core recipe management functionality, the fallback
 * strategy is to log the failure and return a response indicating notifications were not queued.
 * This allows the application to continue functioning even when notifications cannot be sent.
 */
@Component
public final class NotificationServiceFallback implements NotificationServiceClient {

  /** Logger for fallback operations. */
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceFallback.class);

  /**
   * Fallback for recipe published notifications. Logs the failure and returns a response indicating
   * the service is unavailable.
   *
   * @param request the original notification request
   * @return fallback response with empty notifications list
   */
  @Override
  public BatchNotificationResponseDto notifyRecipePublished(
      final RecipePublishedRequestDto request) {
    LOGGER.warn(
        "Notification service unavailable for recipe published notification. "
            + "Recipe ID: {}, Recipients: {}",
        request.getRecipeId(),
        request.getRecipientIds().size());

    return createFallbackResponse(
        "Notification service unavailable - recipe published notifications not queued");
  }

  /**
   * Fallback for recipe liked notifications. Logs the failure and returns a response indicating the
   * service is unavailable.
   *
   * @param request the original notification request
   * @return fallback response with empty notifications list
   */
  @Override
  public BatchNotificationResponseDto notifyRecipeLiked(final RecipeLikedRequestDto request) {
    LOGGER.warn(
        "Notification service unavailable for recipe liked notification. "
            + "Recipe ID: {}, Liker ID: {}, Recipients: {}",
        request.getRecipeId(),
        request.getLikerId(),
        request.getRecipientIds().size());

    return createFallbackResponse(
        "Notification service unavailable - recipe liked notifications not queued");
  }

  /**
   * Fallback for recipe commented notifications. Logs the failure and returns a response indicating
   * the service is unavailable.
   *
   * @param request the original notification request
   * @return fallback response with empty notifications list
   */
  @Override
  public BatchNotificationResponseDto notifyRecipeCommented(
      final RecipeCommentedRequestDto request) {
    LOGGER.warn(
        "Notification service unavailable for recipe commented notification. "
            + "Comment ID: {}, Recipients: {}",
        request.getCommentId(),
        request.getRecipientIds().size());

    return createFallbackResponse(
        "Notification service unavailable - recipe commented notifications not queued");
  }

  /**
   * Creates a fallback response with empty notifications and a message explaining the service is
   * unavailable.
   *
   * @param message explanation message
   * @return fallback batch notification response
   */
  private BatchNotificationResponseDto createFallbackResponse(final String message) {
    // Return empty notifications list since service is unavailable
    // In a real scenario, these notifications would be queued for retry
    List<BatchNotificationResponseDto.NotificationDto> notifications = new ArrayList<>();

    return BatchNotificationResponseDto.builder()
        .notifications(notifications)
        .queuedCount(0)
        .message(message)
        .build();
  }
}
