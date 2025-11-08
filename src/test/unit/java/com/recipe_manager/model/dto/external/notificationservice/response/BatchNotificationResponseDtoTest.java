package com.recipe_manager.model.dto.external.notificationservice.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.dto.external.notificationservice.response.BatchNotificationResponseDto.NotificationDto;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class BatchNotificationResponseDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    Long notificationId = 1001L;
    UUID recipientId = UUID.randomUUID();

    NotificationDto notification = NotificationDto.builder()
        .notificationId(notificationId)
        .recipientId(recipientId)
        .build();

    List<NotificationDto> notifications = List.of(notification);

    BatchNotificationResponseDto dto = BatchNotificationResponseDto.builder()
        .notifications(notifications)
        .queuedCount(1)
        .message("Notifications queued successfully")
        .build();

    assertThat(dto.getNotifications()).isEqualTo(notifications);
    assertThat(dto.getQueuedCount()).isEqualTo(1);
    assertThat(dto.getMessage()).isEqualTo("Notifications queued successfully");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO with snake_case")
  void shouldSerializeAndDeserializeWithSnakeCase() throws Exception {
    Long notificationId1 = 2001L;
    UUID recipientId1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    Long notificationId2 = 2002L;
    UUID recipientId2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    NotificationDto notification1 = NotificationDto.builder()
        .notificationId(notificationId1)
        .recipientId(recipientId1)
        .build();

    NotificationDto notification2 = NotificationDto.builder()
        .notificationId(notificationId2)
        .recipientId(recipientId2)
        .build();

    BatchNotificationResponseDto original = BatchNotificationResponseDto.builder()
        .notifications(List.of(notification1, notification2))
        .queuedCount(2)
        .message("Notifications queued successfully")
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"notifications\"");
    assertThat(json).contains("\"queued_count\"");
    assertThat(json).contains("\"message\"");
    assertThat(json).contains("\"notification_id\"");
    assertThat(json).contains("\"recipient_id\"");
    assertThat(json).contains("2001");
    assertThat(json).contains("550e8400-e29b-41d4-a716-446655440001");

    BatchNotificationResponseDto deserialized = objectMapper.readValue(json, BatchNotificationResponseDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle empty notifications list")
  void shouldHandleEmptyNotificationsList() {
    BatchNotificationResponseDto dto = BatchNotificationResponseDto.builder()
        .notifications(List.of())
        .queuedCount(0)
        .message("No notifications queued")
        .build();

    assertThat(dto.getNotifications()).isEmpty();
    assertThat(dto.getQueuedCount()).isEqualTo(0);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle multiple notifications")
  void shouldHandleMultipleNotifications() {
    List<NotificationDto> notifications = List.of(
        NotificationDto.builder()
            .notificationId(5001L)
            .recipientId(UUID.randomUUID())
            .build(),
        NotificationDto.builder()
            .notificationId(5001L)
            .recipientId(UUID.randomUUID())
            .build(),
        NotificationDto.builder()
            .notificationId(5001L)
            .recipientId(UUID.randomUUID())
            .build()
    );

    BatchNotificationResponseDto dto = BatchNotificationResponseDto.builder()
        .notifications(notifications)
        .queuedCount(3)
        .message("Notifications queued successfully")
        .build();

    assertThat(dto.getNotifications()).hasSize(3);
    assertThat(dto.getQueuedCount()).isEqualTo(3);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    Long notificationId = 1001L;
    UUID recipientId = UUID.randomUUID();

    NotificationDto notification = NotificationDto.builder()
        .notificationId(notificationId)
        .recipientId(recipientId)
        .build();

    BatchNotificationResponseDto dto1 = BatchNotificationResponseDto.builder()
        .notifications(List.of(notification))
        .queuedCount(1)
        .message("Success")
        .build();

    BatchNotificationResponseDto dto2 = BatchNotificationResponseDto.builder()
        .notifications(List.of(notification))
        .queuedCount(1)
        .message("Success")
        .build();

    BatchNotificationResponseDto differentDto = BatchNotificationResponseDto.builder()
        .notifications(List.of())
        .queuedCount(0)
        .message("Different")
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
    Long notificationId = 1001L;
    UUID recipientId = UUID.randomUUID();

    NotificationDto notification = NotificationDto.builder()
        .notificationId(notificationId)
        .recipientId(recipientId)
        .build();

    BatchNotificationResponseDto dto = BatchNotificationResponseDto.builder()
        .notifications(List.of(notification))
        .queuedCount(1)
        .message("Queued")
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("BatchNotificationResponseDto");
    assertThat(toString).contains("Queued");
    assertThat(toString).contains("1");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all-args constructor")
  void shouldSupportAllArgsConstructor() {
    NotificationDto notification = new NotificationDto(3001L, UUID.randomUUID());
    List<NotificationDto> notifications = List.of(notification);

    BatchNotificationResponseDto dto = new BatchNotificationResponseDto(notifications, 1, "Success");

    assertThat(dto.getNotifications()).isEqualTo(notifications);
    assertThat(dto.getQueuedCount()).isEqualTo(1);
    assertThat(dto.getMessage()).isEqualTo("Success");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support no-args constructor and setters")
  void shouldSupportNoArgsConstructorAndSetters() {
    NotificationDto notification = new NotificationDto();
    Long notificationId = 1001L;
    UUID recipientId = UUID.randomUUID();

    notification.setNotificationId(notificationId);
    notification.setRecipientId(recipientId);

    BatchNotificationResponseDto dto = new BatchNotificationResponseDto();
    dto.setNotifications(List.of(notification));
    dto.setQueuedCount(1);
    dto.setMessage("Test message");

    assertThat(dto.getNotifications()).hasSize(1);
    assertThat(dto.getQueuedCount()).isEqualTo(1);
    assertThat(dto.getMessage()).isEqualTo("Test message");
    assertThat(notification.getNotificationId()).isEqualTo(notificationId);
    assertThat(notification.getRecipientId()).isEqualTo(recipientId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("NotificationDto should properly implement equals and hashCode")
  void notificationDtoShouldImplementEqualsAndHashCode() {
    Long notificationId = 1001L;
    UUID recipientId = UUID.randomUUID();

    NotificationDto dto1 = NotificationDto.builder()
        .notificationId(notificationId)
        .recipientId(recipientId)
        .build();

    NotificationDto dto2 = NotificationDto.builder()
        .notificationId(notificationId)
        .recipientId(recipientId)
        .build();

    NotificationDto differentDto = NotificationDto.builder()
        .notificationId(9999L)
        .recipientId(UUID.randomUUID())
        .build();

    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2)
        .isNotEqualTo(differentDto)
        .doesNotHaveSameHashCodeAs(differentDto);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("NotificationDto should support toString method")
  void notificationDtoShouldSupportToString() {
    Long notificationId = 1001L;
    UUID recipientId = UUID.randomUUID();

    NotificationDto dto = NotificationDto.builder()
        .notificationId(notificationId)
        .recipientId(recipientId)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("NotificationDto");
    assertThat(toString).contains(notificationId.toString());
    assertThat(toString).contains(recipientId.toString());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null fields gracefully")
  void shouldHandleNullFieldsGracefully() {
    BatchNotificationResponseDto dto = BatchNotificationResponseDto.builder().build();

    assertThat(dto.getNotifications()).isNull();
    assertThat(dto.getQueuedCount()).isNull();
    assertThat(dto.getMessage()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize example from OpenAPI spec")
  void shouldSerializeExampleFromOpenApiSpec() throws Exception {
    Long notificationId1 = 2001L;
    UUID recipientId1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    Long notificationId2 = 2002L;
    UUID recipientId2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    BatchNotificationResponseDto dto = BatchNotificationResponseDto.builder()
        .notifications(List.of(
            NotificationDto.builder()
                .notificationId(notificationId1)
                .recipientId(recipientId1)
                .build(),
            NotificationDto.builder()
                .notificationId(notificationId2)
                .recipientId(recipientId2)
                .build()
        ))
        .queuedCount(2)
        .message("Notifications queued successfully")
        .build();

    String json = objectMapper.writeValueAsString(dto);

    assertThat(json).contains("2001");
    assertThat(json).contains("550e8400-e29b-41d4-a716-446655440001");
    assertThat(json).contains("2002");
    assertThat(json).contains("550e8400-e29b-41d4-a716-446655440002");
    assertThat(json).contains("Notifications queued successfully");
  }
}
