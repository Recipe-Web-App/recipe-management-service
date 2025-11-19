package com.recipe_manager.service.external.notificationservice;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.recipe_manager.client.notificationservice.NotificationServiceClient;
import com.recipe_manager.client.usermanagement.UserManagementClient;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeCollectedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeCommentedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipePublishedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.response.BatchNotificationResponseDto;
import com.recipe_manager.model.dto.external.usermanagement.GetFollowersResponseDto;
import com.recipe_manager.model.dto.external.usermanagement.UserDto;

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

  /** Feign client for user-management service. */
  private final UserManagementClient userManagementClient;

  /**
   * Constructor for NotificationService.
   *
   * @param notificationServiceClient feign client for notification service
   * @param userManagementClient feign client for user-management service
   */
  public NotificationService(
      final NotificationServiceClient notificationServiceClient,
      final UserManagementClient userManagementClient) {
    this.notificationServiceClient = notificationServiceClient;
    this.userManagementClient = userManagementClient;
  }

  /**
   * Asynchronously notify followers when a recipe is published. Fire-and-forget operation that logs
   * errors but does not fail the main request.
   *
   * <p>Fetches the author's followers from user-management-service and sends notifications to them.
   *
   * @param authorUserId UUID of the recipe author whose followers should be notified
   * @param recipeId ID of the published recipe
   * @return completable future that completes when notification is queued (or fails)
   */
  public CompletableFuture<Void> notifyRecipePublishedAsync(
      final UUID authorUserId, final Long recipeId) {
    return CompletableFuture.runAsync(
        () -> {
          try {
            // Fetch followers from user-management-service
            GetFollowersResponseDto followersResponse =
                userManagementClient.getFollowers(authorUserId, null, null, false);

            List<UserDto> followers = followersResponse.getFollowedUsers();
            if (followers == null || followers.isEmpty()) {
              LOGGER.info(
                  "No followers to notify for recipe published. Recipe ID: {}, Author ID: {}",
                  recipeId,
                  authorUserId);
              return;
            }

            // Extract follower user IDs
            List<UUID> followerIds =
                followers.stream().map(UserDto::getUserId).collect(Collectors.toList());

            RecipePublishedRequestDto request =
                RecipePublishedRequestDto.builder()
                    .recipientIds(followerIds)
                    .recipeId(recipeId)
                    .build();

            BatchNotificationResponseDto response =
                notificationServiceClient.notifyRecipePublished(request);

            LOGGER.info(
                "Recipe published notification queued successfully. Recipe ID: {}, "
                    + "Author ID: {}, Recipients: {}, Queued: {}",
                recipeId,
                authorUserId,
                followerIds.size(),
                response.getQueuedCount());

          } catch (Exception e) {
            // Log error but don't fail the recipe creation operation
            LOGGER.error(
                "Failed to send recipe published notification. Recipe ID: {}, Author ID: {}, "
                    + "Error: {}",
                recipeId,
                authorUserId,
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

  /**
   * Asynchronously notify recipe author when their recipe is added to a collection. Fire-and-forget
   * operation that logs errors but does not fail the main request.
   *
   * <p>Automatically filters out self-notifications (when collector is the recipe author).
   *
   * <p>Privacy considerations are handled by the notification service - it validates that the
   * collector either has a public profile or follows the recipe author before sending the
   * notification.
   *
   * @param recipeAuthorId UUID of the recipe author to notify
   * @param recipeId ID of the recipe that was added to the collection
   * @param collectionId ID of the collection the recipe was added to
   * @param collectorId UUID of the user who added the recipe to their collection
   * @return completable future that completes when notification is queued (or fails)
   */
  public CompletableFuture<Void> notifyRecipeCollectedAsync(
      final UUID recipeAuthorId,
      final Long recipeId,
      final Long collectionId,
      final UUID collectorId) {
    return CompletableFuture.runAsync(
        () -> {
          try {
            // Filter out self-notifications (user adding their own recipe to collection)
            if (recipeAuthorId.equals(collectorId)) {
              LOGGER.debug(
                  "Skipping self-notification for recipe collected. "
                      + "Recipe ID: {}, Collection ID: {}, User ID: {}",
                  recipeId,
                  collectionId,
                  collectorId);
              return;
            }

            RecipeCollectedRequestDto request =
                RecipeCollectedRequestDto.builder()
                    .recipientIds(Collections.singletonList(recipeAuthorId))
                    .recipeId(recipeId)
                    .collectorId(collectorId)
                    .collectionId(collectionId)
                    .build();

            BatchNotificationResponseDto response =
                notificationServiceClient.notifyRecipeCollected(request);

            LOGGER.info(
                "Recipe collected notification queued successfully. Recipe ID: {}, "
                    + "Collection ID: {}, Collector ID: {}, Recipient: {}, Queued: {}",
                recipeId,
                collectionId,
                collectorId,
                recipeAuthorId,
                response.getQueuedCount());

          } catch (Exception e) {
            // Log error but don't fail the collection operation
            LOGGER.error(
                "Failed to send recipe collected notification. Recipe ID: {}, "
                    + "Collection ID: {}, Collector ID: {}, Error: {}",
                recipeId,
                collectionId,
                collectorId,
                e.getMessage(),
                e);
          }
        });
  }
}
