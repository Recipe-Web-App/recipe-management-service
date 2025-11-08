package com.recipe_manager.model.dto.external.notificationservice.response;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response DTO for batch notification operations. Returned when notifications are queued for async
 * processing (HTTP 202 Accepted). Contains a list of created notifications mapped to their
 * recipients.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class BatchNotificationResponseDto {

  /**
   * List of created notifications with their IDs and recipient mappings. Each notification
   * represents a queued email for a specific recipient.
   */
  @JsonProperty("notifications")
  private List<NotificationDto> notifications;

  /**
   * Number of notifications successfully queued for processing. Should match the size of the
   * notifications list.
   */
  @JsonProperty("queued_count")
  private Integer queuedCount;

  /** Success message indicating notifications were queued. */
  @JsonProperty("message")
  private String message;

  /**
   * Nested DTO representing a single notification in the batch. Maps a notification ID to its
   * recipient.
   */
  @Data
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @EqualsAndHashCode
  @ToString
  public static final class NotificationDto {

    /** ID of the created notification record. Used to track notification status. */
    @JsonProperty("notification_id")
    private Long notificationId;

    /** UUID of the recipient user for this notification. */
    @JsonProperty("recipient_id")
    private UUID recipientId;
  }
}
