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
 * Request DTO for notifying the recipe author when their recipe is added to a collection. The
 * notification service will fetch recipe details from recipe-management-service, collection details
 * from recipe-management-service, and collector details from user-management-service.
 *
 * <p>Privacy considerations are handled by the notification service - it validates that the
 * collector either has a public profile or follows the recipe author before sending the
 * notification.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class RecipeCollectedRequestDto {

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
   * ID of the recipe that was added to the collection. The notification service will fetch recipe
   * details from the recipe-management-service.
   */
  @JsonProperty("recipe_id")
  @NotNull(message = "Recipe ID is required")
  private Long recipeId;

  /**
   * UUID of the user who added the recipe to their collection. The notification service will fetch
   * collector details (name, username) from the user-management-service.
   */
  @JsonProperty("collector_id")
  @NotNull(message = "Collector ID is required")
  private UUID collectorId;

  /**
   * ID of the collection that the recipe was added to. The notification service will fetch
   * collection details from the recipe-management-service and include links to the collection and
   * collector profile in the notification.
   */
  @JsonProperty("collection_id")
  @NotNull(message = "Collection ID is required")
  private Long collectionId;
}
