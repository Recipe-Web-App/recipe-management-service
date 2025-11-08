package com.recipe_manager.service.external.notificationservice;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.recipe_manager.client.notificationservice.NotificationServiceClient;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeCommentedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipePublishedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.response.BatchNotificationResponseDto;

/**
 * Service wrapper for notification service client. Provides async fire-and-forget notification
 * calls with automatic error handling and self-notification filtering.
 *
 * <p>All notification operations are asynchronous to prevent blocking the main request flow.
 * Notification failures are logged but do not fail the originating operation.
 */
@Service
public class NotificationService {

  /** Logger for notification operations. */
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

  /** Feign client for notification service. */
  private final NotificationServiceClient notificationServiceClient;

  /**
   * Constructor for NotificationService.
   *
   * @param notificationServiceClient feign client for notification service
   */
  public NotificationService(final NotificationServiceClient notificationServiceClient) {
    this.notificationServiceClient = notificationServiceClient;
  }

  /**
   * Asynchronously notify followers when a recipe is published. Fire-and-forget operation that logs
   * errors but does not fail the main request.
   *
   * <p>TODO: Fetch followers from user-management-service once the follower system is implemented.
   * Currently sends notifications to an empty list as a placeholder.
   *
   * @param followerIds list of follower user IDs to notify
   * @param recipeId ID of the published recipe
   * @return completable future that completes when notification is queued (or fails)
   */
  public CompletableFuture<Void> notifyRecipePublishedAsync(
      final List<UUID> followerIds, final Long recipeId) {
    return CompletableFuture.runAsync(
        () -> {
          try {
            if (followerIds == null || followerIds.isEmpty()) {
              LOGGER.info(
                  "No followers to notify for recipe published. Recipe ID: {}. "
                      + "TODO: Integrate with user-management-service to fetch followers.",
                  recipeId);
              return;
            }

            RecipePublishedRequestDto request =
                RecipePublishedRequestDto.builder()
                    .recipientIds(followerIds)
                    .recipeId(recipeId)
                    .build();

            BatchNotificationResponseDto response =
                notificationServiceClient.notifyRecipePublished(request);

            LOGGER.info(
                "Recipe published notification queued successfully. Recipe ID: {}, "
                    + "Recipients: {}, Queued: {}",
                recipeId,
                followerIds.size(),
                response.getQueuedCount());

          } catch (Exception e) {
            // Log error but don't fail the recipe creation operation
            LOGGER.error(
                "Failed to send recipe published notification. Recipe ID: {}, Error: {}",
                recipeId,
                e.getMessage(),
                e);
          }
        });
  }

  /**
   * Asynchronously notify recipe author when someone comments on their recipe. Fire-and-forget
   * operation that logs errors but does not fail the main request.
   *
   * <p>Automatically filters out self-notifications (when commenter is the recipe author).
   *
   * @param recipeAuthorId UUID of the recipe author to notify
   * @param commentId ID of the comment
   * @param commenterId UUID of the user who made the comment
   * @return completable future that completes when notification is queued (or fails)
   */
  public CompletableFuture<Void> notifyRecipeCommentedAsync(
      final UUID recipeAuthorId, final Long commentId, final UUID commenterId) {
    return CompletableFuture.runAsync(
        () -> {
          try {
            // Filter out self-notifications
            if (recipeAuthorId.equals(commenterId)) {
              LOGGER.debug(
                  "Skipping self-notification for recipe comment. " + "Comment ID: {}, User ID: {}",
                  commentId,
                  commenterId);
              return;
            }

            RecipeCommentedRequestDto request =
                RecipeCommentedRequestDto.builder()
                    .recipientIds(Collections.singletonList(recipeAuthorId))
                    .commentId(commentId)
                    .build();

            BatchNotificationResponseDto response =
                notificationServiceClient.notifyRecipeCommented(request);

            LOGGER.info(
                "Recipe commented notification queued successfully. Comment ID: {}, "
                    + "Recipient: {}, Queued: {}",
                commentId,
                recipeAuthorId,
                response.getQueuedCount());

          } catch (Exception e) {
            // Log error but don't fail the comment creation operation
            LOGGER.error(
                "Failed to send recipe commented notification. Comment ID: {}, Error: {}",
                commentId,
                e.getMessage(),
                e);
          }
        });
  }
}
