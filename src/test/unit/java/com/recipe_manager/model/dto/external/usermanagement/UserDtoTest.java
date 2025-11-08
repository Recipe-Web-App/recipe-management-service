package com.recipe_manager.model.dto.external.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class UserDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    UUID userId = UUID.randomUUID();
    String username = "testuser";
    String email = "test@example.com";
    String fullName = "Test User";
    Boolean isActive = true;

    UserDto dto = UserDto.builder()
        .userId(userId)
        .username(username)
        .email(email)
        .fullName(fullName)
        .isActive(isActive)
        .build();

    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getUsername()).isEqualTo(username);
    assertThat(dto.getEmail()).isEqualTo(email);
    assertThat(dto.getFullName()).isEqualTo(fullName);
    assertThat(dto.getIsActive()).isEqualTo(isActive);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO correctly")
  void shouldSerializeAndDeserializeDto() throws Exception {
    UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    String username = "johndoe";
    String email = "john@example.com";
    String fullName = "John Doe";
    Boolean isActive = true;

    UserDto original = UserDto.builder()
        .userId(userId)
        .username(username)
        .email(email)
        .fullName(fullName)
        .isActive(isActive)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"userId\"");
    assertThat(json).contains("\"username\"");
    assertThat(json).contains("\"email\"");
    assertThat(json).contains("\"fullName\"");
    assertThat(json).contains("\"isActive\"");
    assertThat(json).contains("550e8400-e29b-41d4-a716-446655440001");
    assertThat(json).contains("johndoe");
    assertThat(json).contains("john@example.com");

    UserDto deserialized = objectMapper.readValue(json, UserDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null email field")
  void shouldHandleNullEmail() throws Exception {
    UUID userId = UUID.randomUUID();

    UserDto dto = UserDto.builder()
        .userId(userId)
        .username("testuser")
        .email(null)
        .fullName("Test User")
        .isActive(true)
        .build();

    String json = objectMapper.writeValueAsString(dto);
    UserDto deserialized = objectMapper.readValue(json, UserDto.class);

    assertThat(deserialized.getEmail()).isNull();
    assertThat(deserialized.getUserId()).isEqualTo(userId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null fullName field")
  void shouldHandleNullFullName() throws Exception {
    UUID userId = UUID.randomUUID();

    UserDto dto = UserDto.builder()
        .userId(userId)
        .username("testuser")
        .email("test@example.com")
        .fullName(null)
        .isActive(false)
        .build();

    String json = objectMapper.writeValueAsString(dto);
    UserDto deserialized = objectMapper.readValue(json, UserDto.class);

    assertThat(deserialized.getFullName()).isNull();
    assertThat(deserialized.getIsActive()).isFalse();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    UUID userId = UUID.randomUUID();

    UserDto dto1 = UserDto.builder()
        .userId(userId)
        .username("testuser")
        .email("test@example.com")
        .fullName("Test User")
        .isActive(true)
        .build();

    UserDto dto2 = UserDto.builder()
        .userId(userId)
        .username("testuser")
        .email("test@example.com")
        .fullName("Test User")
        .isActive(true)
        .build();

    UserDto differentDto = UserDto.builder()
        .userId(UUID.randomUUID())
        .username("different")
        .email("different@example.com")
        .fullName("Different User")
        .isActive(false)
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
    UUID userId = UUID.randomUUID();

    UserDto dto = UserDto.builder()
        .userId(userId)
        .username("testuser")
        .email("test@example.com")
        .fullName("Test User")
        .isActive(true)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("UserDto");
    assertThat(toString).contains("testuser");
    assertThat(toString).contains("test@example.com");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all-args constructor")
  void shouldSupportAllArgsConstructor() {
    UUID userId = UUID.randomUUID();

    UserDto dto = new UserDto(userId, "testuser", "test@example.com", "Test User", true);

    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getUsername()).isEqualTo("testuser");
    assertThat(dto.getEmail()).isEqualTo("test@example.com");
    assertThat(dto.getFullName()).isEqualTo("Test User");
    assertThat(dto.getIsActive()).isTrue();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support no-args constructor and setters")
  void shouldSupportNoArgsConstructorAndSetters() {
    UUID userId = UUID.randomUUID();

    UserDto dto = new UserDto();
    dto.setUserId(userId);
    dto.setUsername("testuser");
    dto.setEmail("test@example.com");
    dto.setFullName("Test User");
    dto.setIsActive(true);

    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getUsername()).isEqualTo("testuser");
    assertThat(dto.getEmail()).isEqualTo("test@example.com");
    assertThat(dto.getFullName()).isEqualTo("Test User");
    assertThat(dto.getIsActive()).isTrue();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle all null fields")
  void shouldHandleAllNullFields() throws Exception {
    UserDto dto = UserDto.builder().build();

    String json = objectMapper.writeValueAsString(dto);
    UserDto deserialized = objectMapper.readValue(json, UserDto.class);

    assertThat(deserialized.getUserId()).isNull();
    assertThat(deserialized.getUsername()).isNull();
    assertThat(deserialized.getEmail()).isNull();
    assertThat(deserialized.getFullName()).isNull();
    assertThat(deserialized.getIsActive()).isNull();
  }
}
