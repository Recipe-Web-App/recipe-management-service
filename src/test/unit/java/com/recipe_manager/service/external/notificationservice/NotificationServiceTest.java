package com.recipe_manager.service.external.notificationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import com.recipe_manager.client.usermanagement.UserManagementClient;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeCollectedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipeCommentedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.request.RecipePublishedRequestDto;
import com.recipe_manager.model.dto.external.notificationservice.response.BatchNotificationResponseDto;
import com.recipe_manager.model.dto.external.usermanagement.GetFollowersResponseDto;
import com.recipe_manager.model.dto.external.usermanagement.UserDto;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock private NotificationServiceClient notificationServiceClient;

  @Mock private UserManagementClient userManagementClient;

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
    UUID authorId = UUID.randomUUID();
    UUID followerId1 = UUID.randomUUID();
    UUID followerId2 = UUID.randomUUID();
    Long recipeId = 123L;

    List<UserDto> followers = List.of(
        UserDto.builder().userId(followerId1).username("follower1").isActive(true).build(),
        UserDto.builder().userId(followerId2).username("follower2").isActive(true).build()
    );

    GetFollowersResponseDto followersResponse = GetFollowersResponseDto.builder()
        .totalCount(2)
        .followedUsers(followers)
        .limit(20)
        .offset(0)
        .build();

    when(userManagementClient.getFollowers(eq(authorId), isNull(), isNull(), eq(false)))
        .thenReturn(followersResponse);
    when(notificationServiceClient.notifyRecipePublished(any(RecipePublishedRequestDto.class)))
        .thenReturn(mockResponse);

    CompletableFuture<Void> future =
        notificationService.notifyRecipePublishedAsync(authorId, recipeId);

    future.get(); // Wait for async operation

    verify(userManagementClient).getFollowers(eq(authorId), isNull(), isNull(), eq(false));
    verify(notificationServiceClient).notifyRecipePublished(any(RecipePublishedRequestDto.class));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not call notification service when no followers exist")
  void shouldNotCallServiceWhenNoFollowers() throws Exception {
    UUID authorId = UUID.randomUUID();
    Long recipeId = 123L;

    GetFollowersResponseDto followersResponse = GetFollowersResponseDto.builder()
        .totalCount(0)
        .followedUsers(new ArrayList<>())
        .limit(20)
        .offset(0)
        .build();

    when(userManagementClient.getFollowers(eq(authorId), isNull(), isNull(), eq(false)))
        .thenReturn(followersResponse);

    CompletableFuture<Void> future =
        notificationService.notifyRecipePublishedAsync(authorId, recipeId);

    future.get(); // Wait for async operation

    verify(userManagementClient).getFollowers(eq(authorId), isNull(), isNull(), eq(false));
    verify(notificationServiceClient, never())
        .notifyRecipePublished(any(RecipePublishedRequestDto.class));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not call notification service when followers list is null")
  void shouldNotCallServiceWhenFollowersIsNull() throws Exception {
    UUID authorId = UUID.randomUUID();
    Long recipeId = 123L;

    GetFollowersResponseDto followersResponse = GetFollowersResponseDto.builder()
        .totalCount(0)
        .followedUsers(null)
        .limit(null)
        .offset(null)
        .build();

    when(userManagementClient.getFollowers(eq(authorId), isNull(), isNull(), eq(false)))
        .thenReturn(followersResponse);

    CompletableFuture<Void> future = notificationService.notifyRecipePublishedAsync(authorId, recipeId);

    future.get(); // Wait for async operation

    verify(userManagementClient).getFollowers(eq(authorId), isNull(), isNull(), eq(false));
    verify(notificationServiceClient, never())
        .notifyRecipePublished(any(RecipePublishedRequestDto.class));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle notification service failure gracefully for recipe published")
  void shouldHandleRecipePublishedFailureGracefully() throws Exception {
    UUID authorId = UUID.randomUUID();
    UUID followerId = UUID.randomUUID();
    Long recipeId = 123L;

    List<UserDto> followers = List.of(
        UserDto.builder().userId(followerId).username("follower1").isActive(true).build()
    );

    GetFollowersResponseDto followersResponse = GetFollowersResponseDto.builder()
        .totalCount(1)
        .followedUsers(followers)
        .limit(20)
        .offset(0)
        .build();

    when(userManagementClient.getFollowers(eq(authorId), isNull(), isNull(), eq(false)))
        .thenReturn(followersResponse);
    when(notificationServiceClient.notifyRecipePublished(any(RecipePublishedRequestDto.class)))
        .thenThrow(new RuntimeException("Service unavailable"));

    CompletableFuture<Void> future =
        notificationService.notifyRecipePublishedAsync(authorId, recipeId);

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
  @DisplayName("Should successfully notify recipe collected")
  void shouldNotifyRecipeCollected() throws Exception {
    UUID recipeAuthorId = UUID.randomUUID();
    UUID collectorId = UUID.randomUUID();
    Long recipeId = 123L;
    Long collectionId = 456L;

    when(notificationServiceClient.notifyRecipeCollected(any(RecipeCollectedRequestDto.class)))
        .thenReturn(mockResponse);

    CompletableFuture<Void> future =
        notificationService.notifyRecipeCollectedAsync(
            recipeAuthorId, recipeId, collectionId, collectorId);

    future.get(); // Wait for async operation

    verify(notificationServiceClient).notifyRecipeCollected(any(RecipeCollectedRequestDto.class));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should filter out self-notifications when user adds own recipe to collection")
  void shouldFilterOutSelfNotificationsForRecipeCollected() throws Exception {
    UUID userId = UUID.randomUUID();
    Long recipeId = 123L;
    Long collectionId = 456L;

    CompletableFuture<Void> future =
        notificationService.notifyRecipeCollectedAsync(
            userId, // recipe author
            recipeId,
            collectionId,
            userId); // same user collecting

    future.get(); // Wait for async operation

    verify(notificationServiceClient, never())
        .notifyRecipeCollected(any(RecipeCollectedRequestDto.class));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle notification service failure gracefully for recipe collected")
  void shouldHandleRecipeCollectedFailureGracefully() throws Exception {
    UUID recipeAuthorId = UUID.randomUUID();
    UUID collectorId = UUID.randomUUID();
    Long recipeId = 123L;
    Long collectionId = 456L;

    when(notificationServiceClient.notifyRecipeCollected(any(RecipeCollectedRequestDto.class)))
        .thenThrow(new RuntimeException("Service unavailable"));

    CompletableFuture<Void> future =
        notificationService.notifyRecipeCollectedAsync(
            recipeAuthorId, recipeId, collectionId, collectorId);

    // Should complete without throwing exception
    future.get();
    assertThat(future).isCompleted();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should complete async operation and return void future")
  void shouldCompleteAsyncOperation() throws Exception {
    UUID authorId = UUID.randomUUID();
    UUID followerId = UUID.randomUUID();
    Long recipeId = 123L;

    List<UserDto> followers = List.of(
        UserDto.builder().userId(followerId).username("follower1").isActive(true).build()
    );

    GetFollowersResponseDto followersResponse = GetFollowersResponseDto.builder()
        .totalCount(1)
        .followedUsers(followers)
        .limit(20)
        .offset(0)
        .build();

    when(userManagementClient.getFollowers(eq(authorId), isNull(), isNull(), eq(false)))
        .thenReturn(followersResponse);
    when(notificationServiceClient.notifyRecipePublished(any(RecipePublishedRequestDto.class)))
        .thenReturn(mockResponse);

    CompletableFuture<Void> future =
        notificationService.notifyRecipePublishedAsync(authorId, recipeId);

    assertThat(future).isNotNull();
    future.get(); // Wait for completion
    assertThat(future).isCompleted();
    assertThat(future).isDone();
  }
}
