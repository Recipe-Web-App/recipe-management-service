package com.recipe_manager.service.external.notificationservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.recipe_manager.model.dto.external.notificationservice.request.RecipeCollectedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeCommentedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeLikedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipePublishedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeRatedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.response.BatchNotificationResponseDto;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class NotificationServiceFallbackTest {

  private NotificationServiceFallback fallback;

  @BeforeEach
  void setUp() {
    fallback = new NotificationServiceFallback();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return fallback response for recipe published notification")
  void shouldReturnFallbackForRecipePublished() {
    Long recipeId = 123L;
    UUID recipientId = UUID.randomUUID();
    RecipePublishedRequestDto request = RecipePublishedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(recipeId)
        .build();

    BatchNotificationResponseDto response = fallback.notifyRecipePublished(request);

    assertThat(response).isNotNull();
    assertThat(response.getNotifications()).isEmpty();
    assertThat(response.getQueuedCount()).isEqualTo(0);
    assertThat(response.getMessage())
        .contains("Notification service unavailable")
        .contains("recipe published");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return fallback response for recipe liked notification")
  void shouldReturnFallbackForRecipeLiked() {
    Long recipeId = 123L;
    UUID likerId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    RecipeLikedRequestDto request = RecipeLikedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(recipeId)
        .likerId(likerId)
        .build();

    BatchNotificationResponseDto response = fallback.notifyRecipeLiked(request);

    assertThat(response).isNotNull();
    assertThat(response.getNotifications()).isEmpty();
    assertThat(response.getQueuedCount()).isEqualTo(0);
    assertThat(response.getMessage())
        .contains("Notification service unavailable")
        .contains("recipe liked");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return fallback response for recipe commented notification")
  void shouldReturnFallbackForRecipeCommented() {
    Long commentId = 789L;
    UUID recipientId = UUID.randomUUID();
    RecipeCommentedRequestDto request = RecipeCommentedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .commentId(commentId)
        .build();

    BatchNotificationResponseDto response = fallback.notifyRecipeCommented(request);

    assertThat(response).isNotNull();
    assertThat(response.getNotifications()).isEmpty();
    assertThat(response.getQueuedCount()).isEqualTo(0);
    assertThat(response.getMessage())
        .contains("Notification service unavailable")
        .contains("recipe commented");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return fallback response for recipe collected notification")
  void shouldReturnFallbackForRecipeCollected() {
    Long recipeId = 123L;
    Long collectionId = 456L;
    UUID collectorId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    RecipeCollectedRequestDto request = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(recipeId)
        .collectorId(collectorId)
        .collectionId(collectionId)
        .build();

    BatchNotificationResponseDto response = fallback.notifyRecipeCollected(request);

    assertThat(response).isNotNull();
    assertThat(response.getNotifications()).isEmpty();
    assertThat(response.getQueuedCount()).isEqualTo(0);
    assertThat(response.getMessage())
        .contains("Notification service unavailable")
        .contains("recipe collected");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return fallback response for recipe rated notification")
  void shouldReturnFallbackForRecipeRated() {
    Long recipeId = 123L;
    UUID raterId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    RecipeRatedRequestDto request = RecipeRatedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(recipeId)
        .raterId(raterId)
        .build();

    BatchNotificationResponseDto response = fallback.notifyRecipeRated(request);

    assertThat(response).isNotNull();
    assertThat(response.getNotifications()).isEmpty();
    assertThat(response.getQueuedCount()).isEqualTo(0);
    assertThat(response.getMessage())
        .contains("Notification service unavailable")
        .contains("recipe rated");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle multiple recipients in fallback response")
  void shouldHandleMultipleRecipientsInFallback() {
    List<UUID> recipientIds = List.of(
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID()
    );
    RecipePublishedRequestDto request = RecipePublishedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(999L)
        .build();

    BatchNotificationResponseDto response = fallback.notifyRecipePublished(request);

    assertThat(response).isNotNull();
    assertThat(response.getNotifications()).isEmpty();
    assertThat(response.getQueuedCount()).isEqualTo(0);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return consistent fallback structure for all notification types")
  void shouldReturnConsistentFallbackStructure() {
    UUID recipientId = UUID.randomUUID();

    RecipePublishedRequestDto publishedRequest = RecipePublishedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(999L)
        .build();

    RecipeLikedRequestDto likedRequest = RecipeLikedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(999L)
        .likerId(UUID.randomUUID())
        .build();

    RecipeCommentedRequestDto commentedRequest = RecipeCommentedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .commentId(888L)
        .build();

    RecipeCollectedRequestDto collectedRequest = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(999L)
        .collectorId(UUID.randomUUID())
        .collectionId(777L)
        .build();

    RecipeRatedRequestDto ratedRequest = RecipeRatedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(999L)
        .raterId(UUID.randomUUID())
        .build();

    BatchNotificationResponseDto publishedResponse = fallback.notifyRecipePublished(publishedRequest);
    BatchNotificationResponseDto likedResponse = fallback.notifyRecipeLiked(likedRequest);
    BatchNotificationResponseDto commentedResponse = fallback.notifyRecipeCommented(commentedRequest);
    BatchNotificationResponseDto collectedResponse = fallback.notifyRecipeCollected(collectedRequest);
    BatchNotificationResponseDto ratedResponse = fallback.notifyRecipeRated(ratedRequest);

    // All responses should have the same structure
    assertThat(publishedResponse.getNotifications()).isEmpty();
    assertThat(likedResponse.getNotifications()).isEmpty();
    assertThat(commentedResponse.getNotifications()).isEmpty();
    assertThat(collectedResponse.getNotifications()).isEmpty();
    assertThat(ratedResponse.getNotifications()).isEmpty();

    assertThat(publishedResponse.getQueuedCount()).isEqualTo(0);
    assertThat(likedResponse.getQueuedCount()).isEqualTo(0);
    assertThat(commentedResponse.getQueuedCount()).isEqualTo(0);
    assertThat(collectedResponse.getQueuedCount()).isEqualTo(0);
    assertThat(ratedResponse.getQueuedCount()).isEqualTo(0);

    // All messages should indicate service unavailable
    assertThat(publishedResponse.getMessage()).contains("Notification service unavailable");
    assertThat(likedResponse.getMessage()).contains("Notification service unavailable");
    assertThat(commentedResponse.getMessage()).contains("Notification service unavailable");
    assertThat(collectedResponse.getMessage()).contains("Notification service unavailable");
    assertThat(ratedResponse.getMessage()).contains("Notification service unavailable");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle empty recipient list gracefully")
  void shouldHandleEmptyRecipientList() {
    RecipePublishedRequestDto request = RecipePublishedRequestDto.builder()
        .recipientIds(List.of())
        .recipeId(999L)
        .build();

    BatchNotificationResponseDto response = fallback.notifyRecipePublished(request);

    assertThat(response).isNotNull();
    assertThat(response.getNotifications()).isEmpty();
    assertThat(response.getQueuedCount()).isEqualTo(0);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return non-null response for all fallback methods")
  void shouldReturnNonNullResponse() {
    RecipePublishedRequestDto publishedRequest = RecipePublishedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(999L)
        .build();

    RecipeLikedRequestDto likedRequest = RecipeLikedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(999L)
        .likerId(UUID.randomUUID())
        .build();

    RecipeCommentedRequestDto commentedRequest = RecipeCommentedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .commentId(888L)
        .build();

    RecipeCollectedRequestDto collectedRequest = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(999L)
        .collectorId(UUID.randomUUID())
        .collectionId(777L)
        .build();

    RecipeRatedRequestDto ratedRequest = RecipeRatedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(999L)
        .raterId(UUID.randomUUID())
        .build();

    assertThat(fallback.notifyRecipePublished(publishedRequest)).isNotNull();
    assertThat(fallback.notifyRecipeLiked(likedRequest)).isNotNull();
    assertThat(fallback.notifyRecipeCommented(commentedRequest)).isNotNull();
    assertThat(fallback.notifyRecipeCollected(collectedRequest)).isNotNull();
    assertThat(fallback.notifyRecipeRated(ratedRequest)).isNotNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have distinct messages for each notification type")
  void shouldHaveDistinctMessagesForEachType() {
    UUID recipientId = UUID.randomUUID();

    RecipePublishedRequestDto publishedRequest = RecipePublishedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(999L)
        .build();

    RecipeLikedRequestDto likedRequest = RecipeLikedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(999L)
        .likerId(UUID.randomUUID())
        .build();

    RecipeCommentedRequestDto commentedRequest = RecipeCommentedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .commentId(888L)
        .build();

    RecipeCollectedRequestDto collectedRequest = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(999L)
        .collectorId(UUID.randomUUID())
        .collectionId(777L)
        .build();

    RecipeRatedRequestDto ratedRequest = RecipeRatedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(999L)
        .raterId(UUID.randomUUID())
        .build();

    String publishedMessage = fallback.notifyRecipePublished(publishedRequest).getMessage();
    String likedMessage = fallback.notifyRecipeLiked(likedRequest).getMessage();
    String commentedMessage = fallback.notifyRecipeCommented(commentedRequest).getMessage();
    String collectedMessage = fallback.notifyRecipeCollected(collectedRequest).getMessage();
    String ratedMessage = fallback.notifyRecipeRated(ratedRequest).getMessage();

    // Messages should be distinct to identify the notification type
    assertThat(publishedMessage).isNotEqualTo(likedMessage);
    assertThat(likedMessage).isNotEqualTo(commentedMessage);
    assertThat(publishedMessage).isNotEqualTo(commentedMessage);
    assertThat(collectedMessage).isNotEqualTo(publishedMessage);
    assertThat(collectedMessage).isNotEqualTo(likedMessage);
    assertThat(collectedMessage).isNotEqualTo(commentedMessage);
    assertThat(ratedMessage).isNotEqualTo(publishedMessage);
    assertThat(ratedMessage).isNotEqualTo(likedMessage);
    assertThat(ratedMessage).isNotEqualTo(commentedMessage);
    assertThat(ratedMessage).isNotEqualTo(collectedMessage);
  }
}
