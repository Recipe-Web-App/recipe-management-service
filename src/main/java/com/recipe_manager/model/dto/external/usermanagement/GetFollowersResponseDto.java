package com.recipe_manager.model.dto.external.usermanagement;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for getting a user's followers from the user-management service.
 *
 * <p>Contains the list of followers and pagination information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetFollowersResponseDto {

  /** Total number of followers. */
  @JsonProperty("totalCount")
  private Integer totalCount;

  /**
   * List of users who are followers.
   *
   * <p>Null when count_only=true in the request.
   */
  @JsonProperty("followedUsers")
  private List<UserDto> followedUsers;

  /**
   * Number of results returned.
   *
   * <p>Null when count_only=true in the request.
   */
  @JsonProperty("limit")
  private Integer limit;

  /**
   * Number of results skipped.
   *
   * <p>Null when count_only=true in the request.
   */
  @JsonProperty("offset")
  private Integer offset;
}
