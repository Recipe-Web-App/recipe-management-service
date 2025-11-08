package com.recipe_manager.model.dto.external.notificationservice.request;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Request DTO for notifying the recipe author when someone comments on their recipe. The
 * notification service will fetch complete comment details (including recipe_id, commenter info,
 * comment text) from the recipe-management-service using the provided comment ID.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class RecipeCommentedRequestDto {

  /** Minimum number of recipients allowed per request. */
  public static final int MIN_RECIPIENTS = 1;

  /** Maximum number of recipients allowed per request (notification service limit). */
  public static final int MAX_RECIPIENTS = 100;

  /**
   * List of recipient user IDs (typically the recipe author). The notification service will send
   * email notifications to all specified recipients.
   */
  @JsonProperty("recipient_ids")
  @NotNull(message = "Recipient IDs are required")
  @Size(
      min = MIN_RECIPIENTS,
      max = MAX_RECIPIENTS,
      message = "Recipient IDs must contain between 1 and 100 items")
  private List<UUID> recipientIds;

  /**
   * ID of the comment. The notification service will fetch complete comment details (including
   * recipe_id and commenter info) from the recipe-management-service.
   */
  @JsonProperty("comment_id")
  @NotNull(message = "Comment ID is required")
  private Long commentId;
}
