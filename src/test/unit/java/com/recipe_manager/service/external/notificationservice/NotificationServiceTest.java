package com.recipe_manager.service.external.notificationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.recipe_manager.client.notificationservice.NotificationServiceClient;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeCommentedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipePublishedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.response.BatchNotificationResponseDto;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock private NotificationServiceClient notificationServiceClient;

  @InjectMocks private NotificationService notificationService;

  private BatchNotificationResponseDto mockResponse;

  @BeforeEach
  void setUp() {
    mockResponse =
        BatchNotificationResponseDto.builder()
            .notifications(Collections.emptyList())
            .queuedCount(1)
            .message("Notification queued successfully")
            .build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should successfully notify recipe published with followers")
  void shouldNotifyRecipePublishedWithFollowers() throws Exception {
    UUID followerId1 = UUID.randomUUID();
    UUID followerId2 = UUID.randomUUID();
    List<UUID> followerIds = List.of(followerId1, followerId2);
    Long recipeId = 123L;

    when(notificationServiceClient.notifyRecipePublished(any(RecipePublishedRequestDto.class)))
        .thenReturn(mockResponse);

    CompletableFuture<Void> future =
        notificationService.notifyRecipePublishedAsync(followerIds, recipeId);

    future.get(); // Wait for async operation

    verify(notificationServiceClient).notifyRecipePublished(any(RecipePublishedRequestDto.class));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not call notification service when no followers exist")
  void shouldNotCallServiceWhenNoFollowers() throws Exception {
    Long recipeId = 123L;

    CompletableFuture<Void> future =
        notificationService.notifyRecipePublishedAsync(Collections.emptyList(), recipeId);

    future.get(); // Wait for async operation

    verify(notificationServiceClient, never())
        .notifyRecipePublished(any(RecipePublishedRequestDto.class));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not call notification service when followers list is null")
  void shouldNotCallServiceWhenFollowersIsNull() throws Exception {
    Long recipeId = 123L;

    CompletableFuture<Void> future = notificationService.notifyRecipePublishedAsync(null, recipeId);

    future.get(); // Wait for async operation

    verify(notificationServiceClient, never())
        .notifyRecipePublished(any(RecipePublishedRequestDto.class));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle notification service failure gracefully for recipe published")
  void shouldHandleRecipePublishedFailureGracefully() throws Exception {
    UUID followerId = UUID.randomUUID();
    Long recipeId = 123L;

    when(notificationServiceClient.notifyRecipePublished(any(RecipePublishedRequestDto.class)))
        .thenThrow(new RuntimeException("Service unavailable"));

    CompletableFuture<Void> future =
        notificationService.notifyRecipePublishedAsync(List.of(followerId), recipeId);

    // Should complete without throwing exception
    future.get();
    assertThat(future).isCompleted();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should successfully notify recipe commented")
  void shouldNotifyRecipeCommented() throws Exception {
    UUID recipeAuthorId = UUID.randomUUID();
    UUID commenterId = UUID.randomUUID();
    Long commentId = 456L;

    when(notificationServiceClient.notifyRecipeCommented(any(RecipeCommentedRequestDto.class)))
        .thenReturn(mockResponse);

    CompletableFuture<Void> future =
        notificationService.notifyRecipeCommentedAsync(recipeAuthorId, commentId, commenterId);

    future.get(); // Wait for async operation

    verify(notificationServiceClient).notifyRecipeCommented(any(RecipeCommentedRequestDto.class));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should filter out self-notifications when author comments on own recipe")
  void shouldFilterOutSelfNotifications() throws Exception {
    UUID userId = UUID.randomUUID();
    Long commentId = 456L;

    CompletableFuture<Void> future =
        notificationService.notifyRecipeCommentedAsync(
            userId, // recipe author
            commentId,
            userId); // same user commenting

    future.get(); // Wait for async operation

    verify(notificationServiceClient, never())
        .notifyRecipeCommented(any(RecipeCommentedRequestDto.class));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle notification service failure gracefully for recipe commented")
  void shouldHandleRecipeCommentedFailureGracefully() throws Exception {
    UUID recipeAuthorId = UUID.randomUUID();
    UUID commenterId = UUID.randomUUID();
    Long commentId = 456L;

    when(notificationServiceClient.notifyRecipeCommented(any(RecipeCommentedRequestDto.class)))
        .thenThrow(new RuntimeException("Service unavailable"));

    CompletableFuture<Void> future =
        notificationService.notifyRecipeCommentedAsync(recipeAuthorId, commentId, commenterId);

    // Should complete without throwing exception
    future.get();
    assertThat(future).isCompleted();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should complete async operation and return void future")
  void shouldCompleteAsyncOperation() throws Exception {
    UUID followerId = UUID.randomUUID();
    Long recipeId = 123L;

    when(notificationServiceClient.notifyRecipePublished(any(RecipePublishedRequestDto.class)))
        .thenReturn(mockResponse);

    CompletableFuture<Void> future =
        notificationService.notifyRecipePublishedAsync(List.of(followerId), recipeId);

    assertThat(future).isNotNull();
    future.get(); // Wait for completion
    assertThat(future).isCompleted();
    assertThat(future).isDone();
  }
}
