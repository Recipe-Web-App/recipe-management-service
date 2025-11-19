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
 * Request DTO for recipe rated notification.
 *
 * <p>This DTO is used to notify the recipe author when someone rates their recipe. The notification
 * service will fetch recipe details and rating information from recipe-management-service, and
 * rater details from user-management-service.
 *
 * <p>Privacy validation is handled by the notification service, which checks follower relationships
 * as specified in the notification-service API specification.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class RecipeRatedRequestDto {

  /** Minimum number of recipients allowed. */
  public static final int MIN_RECIPIENTS = 1;

  /** Maximum number of recipients allowed. */
  public static final int MAX_RECIPIENTS = 100;

  /**
   * List of recipient user IDs (typically the recipe author).
   *
   * <p>Must contain between 1 and 100 UUIDs.
   */
  @JsonProperty("recipient_ids")
  @NotNull(message = "Recipient IDs are required")
  @Size(
      min = MIN_RECIPIENTS,
      max = MAX_RECIPIENTS,
      message = "Recipient IDs must contain between 1 and 100 items")
  private List<UUID> recipientIds;

  /**
   * ID of the rated recipe.
   *
   * <p>The notification service will fetch recipe details and rating information from
   * recipe-management-service.
   */
  @JsonProperty("recipe_id")
  @NotNull(message = "Recipe ID is required")
  private Long recipeId;

  /**
   * UUID of the user who rated the recipe.
   *
   * <p>The notification service will fetch rater details from user-management-service.
   */
  @JsonProperty("rater_id")
  @NotNull(message = "Rater ID is required")
  private UUID raterId;
}
