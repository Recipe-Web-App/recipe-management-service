package com.recipe_manager.model.dto.external.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class GetFollowersResponseDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    UserDto user1 = UserDto.builder()
        .userId(UUID.randomUUID())
        .username("user1")
        .isActive(true)
        .build();

    UserDto user2 = UserDto.builder()
        .userId(UUID.randomUUID())
        .username("user2")
        .isActive(true)
        .build();

    List<UserDto> followedUsers = List.of(user1, user2);

    GetFollowersResponseDto dto = GetFollowersResponseDto.builder()
        .totalCount(2)
        .followedUsers(followedUsers)
        .limit(20)
        .offset(0)
        .build();

    assertThat(dto.getTotalCount()).isEqualTo(2);
    assertThat(dto.getFollowedUsers()).hasSize(2);
    assertThat(dto.getLimit()).isEqualTo(20);
    assertThat(dto.getOffset()).isEqualTo(0);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO correctly")
  void shouldSerializeAndDeserializeDto() throws Exception {
    UserDto user = UserDto.builder()
        .userId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))
        .username("follower1")
        .email("follower@example.com")
        .fullName("Follower One")
        .isActive(true)
        .build();

    GetFollowersResponseDto original = GetFollowersResponseDto.builder()
        .totalCount(1)
        .followedUsers(List.of(user))
        .limit(20)
        .offset(0)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"totalCount\"");
    assertThat(json).contains("\"followedUsers\"");
    assertThat(json).contains("\"limit\"");
    assertThat(json).contains("\"offset\"");
    assertThat(json).contains("follower1");

    GetFollowersResponseDto deserialized = objectMapper.readValue(json, GetFollowersResponseDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle empty followers list")
  void shouldHandleEmptyFollowersList() {
    GetFollowersResponseDto dto = GetFollowersResponseDto.builder()
        .totalCount(0)
        .followedUsers(new ArrayList<>())
        .limit(20)
        .offset(0)
        .build();

    assertThat(dto.getTotalCount()).isEqualTo(0);
    assertThat(dto.getFollowedUsers()).isEmpty();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null followedUsers for count-only response")
  void shouldHandleNullFollowedUsersForCountOnly() throws Exception {
    GetFollowersResponseDto dto = GetFollowersResponseDto.builder()
        .totalCount(100)
        .followedUsers(null)
        .limit(null)
        .offset(null)
        .build();

    String json = objectMapper.writeValueAsString(dto);
    GetFollowersResponseDto deserialized = objectMapper.readValue(json, GetFollowersResponseDto.class);

    assertThat(deserialized.getTotalCount()).isEqualTo(100);
    assertThat(deserialized.getFollowedUsers()).isNull();
    assertThat(deserialized.getLimit()).isNull();
    assertThat(deserialized.getOffset()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle multiple followers with pagination")
  void shouldHandleMultipleFollowersWithPagination() {
    List<UserDto> followers = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      followers.add(UserDto.builder()
          .userId(UUID.randomUUID())
          .username("user" + i)
          .isActive(true)
          .build());
    }

    GetFollowersResponseDto dto = GetFollowersResponseDto.builder()
        .totalCount(50)
        .followedUsers(followers)
        .limit(10)
        .offset(20)
        .build();

    assertThat(dto.getTotalCount()).isEqualTo(50);
    assertThat(dto.getFollowedUsers()).hasSize(10);
    assertThat(dto.getLimit()).isEqualTo(10);
    assertThat(dto.getOffset()).isEqualTo(20);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    UserDto user = UserDto.builder()
        .userId(UUID.randomUUID())
        .username("testuser")
        .isActive(true)
        .build();

    GetFollowersResponseDto dto1 = GetFollowersResponseDto.builder()
        .totalCount(1)
        .followedUsers(List.of(user))
        .limit(20)
        .offset(0)
        .build();

    GetFollowersResponseDto dto2 = GetFollowersResponseDto.builder()
        .totalCount(1)
        .followedUsers(List.of(user))
        .limit(20)
        .offset(0)
        .build();

    GetFollowersResponseDto differentDto = GetFollowersResponseDto.builder()
        .totalCount(0)
        .followedUsers(new ArrayList<>())
        .limit(20)
        .offset(0)
        .build();

    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2)
        .isNotEqualTo(differentDto)
        .doesNotHaveSameHashCodeAs(differentDto);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support toString method")
  void shouldSupportToString() {
    GetFollowersResponseDto dto = GetFollowersResponseDto.builder()
        .totalCount(5)
        .followedUsers(new ArrayList<>())
        .limit(20)
        .offset(0)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("GetFollowersResponseDto");
    assertThat(toString).contains("5");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all-args constructor")
  void shouldSupportAllArgsConstructor() {
    List<UserDto> followers = List.of(
        UserDto.builder().userId(UUID.randomUUID()).username("user1").isActive(true).build()
    );

    GetFollowersResponseDto dto = new GetFollowersResponseDto(10, followers, 20, 0);

    assertThat(dto.getTotalCount()).isEqualTo(10);
    assertThat(dto.getFollowedUsers()).hasSize(1);
    assertThat(dto.getLimit()).isEqualTo(20);
    assertThat(dto.getOffset()).isEqualTo(0);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support no-args constructor and setters")
  void shouldSupportNoArgsConstructorAndSetters() {
    List<UserDto> followers = List.of(
        UserDto.builder().userId(UUID.randomUUID()).username("user1").isActive(true).build()
    );

    GetFollowersResponseDto dto = new GetFollowersResponseDto();
    dto.setTotalCount(15);
    dto.setFollowedUsers(followers);
    dto.setLimit(25);
    dto.setOffset(5);

    assertThat(dto.getTotalCount()).isEqualTo(15);
    assertThat(dto.getFollowedUsers()).hasSize(1);
    assertThat(dto.getLimit()).isEqualTo(25);
    assertThat(dto.getOffset()).isEqualTo(5);
  }
}
