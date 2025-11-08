package com.recipe_manager.client.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;

import com.recipe_manager.model.dto.external.usermanagement.GetFollowersResponseDto;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class UserManagementFallbackTest {

  private UserManagementFallback fallback;

  @BeforeEach
  void setUp() {
    fallback = new UserManagementFallback();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return fallback response for getFollowers with empty list")
  void shouldReturnFallbackForGetFollowers() {
    UUID userId = UUID.randomUUID();
    Integer limit = 20;
    Integer offset = 0;
    Boolean countOnly = false;

    GetFollowersResponseDto response = fallback.getFollowers(userId, limit, offset, countOnly);

    assertThat(response).isNotNull();
    assertThat(response.getTotalCount()).isEqualTo(0);
    assertThat(response.getFollowedUsers()).isEmpty();
    assertThat(response.getLimit()).isEqualTo(limit);
    assertThat(response.getOffset()).isEqualTo(offset);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return fallback response with null parameters")
  void shouldReturnFallbackWithNullParameters() {
    UUID userId = UUID.randomUUID();

    GetFollowersResponseDto response = fallback.getFollowers(userId, null, null, null);

    assertThat(response).isNotNull();
    assertThat(response.getTotalCount()).isEqualTo(0);
    assertThat(response.getFollowedUsers()).isEmpty();
    assertThat(response.getLimit()).isNull();
    assertThat(response.getOffset()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return fallback response when countOnly is true")
  void shouldReturnFallbackWhenCountOnly() {
    UUID userId = UUID.randomUUID();

    GetFollowersResponseDto response = fallback.getFollowers(userId, 20, 0, true);

    assertThat(response).isNotNull();
    assertThat(response.getTotalCount()).isEqualTo(0);
    assertThat(response.getFollowedUsers()).isEmpty();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return fallback response with custom pagination")
  void shouldReturnFallbackWithCustomPagination() {
    UUID userId = UUID.randomUUID();
    Integer limit = 50;
    Integer offset = 100;

    GetFollowersResponseDto response = fallback.getFollowers(userId, limit, offset, false);

    assertThat(response).isNotNull();
    assertThat(response.getTotalCount()).isEqualTo(0);
    assertThat(response.getFollowedUsers()).isEmpty();
    assertThat(response.getLimit()).isEqualTo(limit);
    assertThat(response.getOffset()).isEqualTo(offset);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle multiple fallback calls")
  void shouldHandleMultipleFallbackCalls() {
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();

    GetFollowersResponseDto response1 = fallback.getFollowers(userId1, 20, 0, false);
    GetFollowersResponseDto response2 = fallback.getFollowers(userId2, 10, 5, false);

    assertThat(response1.getTotalCount()).isEqualTo(0);
    assertThat(response2.getTotalCount()).isEqualTo(0);
    assertThat(response1.getFollowedUsers()).isEmpty();
    assertThat(response2.getFollowedUsers()).isEmpty();
  }
}
