package com.recipe_manager.model.dto.external.notificationservice.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class RecipeCommentedRequestDtoTest {

  private ObjectMapper objectMapper;
  private Validator validator;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    UUID commentId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(recipientId);

    RecipeCommentedRequestDto dto = RecipeCommentedRequestDto.builder()
        .recipientIds(recipientIds)
        .commentId(commentId)
        .build();

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getCommentId()).isEqualTo(commentId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO with snake_case")
  void shouldSerializeAndDeserializeWithSnakeCase() throws Exception {
    UUID commentId = UUID.fromString("880e8400-e29b-41d4-a716-446655440123");
    UUID recipientId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    List<UUID> recipientIds = List.of(recipientId);

    RecipeCommentedRequestDto original = RecipeCommentedRequestDto.builder()
        .recipientIds(recipientIds)
        .commentId(commentId)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"recipient_ids\"");
    assertThat(json).contains("\"comment_id\"");
    assertThat(json).contains("880e8400-e29b-41d4-a716-446655440123");
    assertThat(json).contains("550e8400-e29b-41d4-a716-446655440001");

    RecipeCommentedRequestDto deserialized = objectMapper.readValue(json, RecipeCommentedRequestDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate valid DTO")
  void shouldValidateValidDto() {
    RecipeCommentedRequestDto dto = RecipeCommentedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .commentId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeCommentedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate DTO with maximum allowed recipients")
  void shouldValidateDtoWithMaxRecipients() {
    List<UUID> maxRecipients = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      maxRecipients.add(UUID.randomUUID());
    }

    RecipeCommentedRequestDto dto = RecipeCommentedRequestDto.builder()
        .recipientIds(maxRecipients)
        .commentId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeCommentedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null recipient IDs")
  void shouldRejectNullRecipientIds() {
    RecipeCommentedRequestDto dto = RecipeCommentedRequestDto.builder()
        .commentId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeCommentedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Recipient IDs are required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject empty recipient IDs list")
  void shouldRejectEmptyRecipientIds() {
    RecipeCommentedRequestDto dto = RecipeCommentedRequestDto.builder()
        .recipientIds(List.of())
        .commentId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeCommentedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Recipient IDs must contain between 1 and 100 items");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject recipient IDs exceeding maximum")
  void shouldRejectRecipientIdsExceedingMax() {
    List<UUID> tooManyRecipients = new ArrayList<>();
    for (int i = 0; i < 101; i++) {
      tooManyRecipients.add(UUID.randomUUID());
    }

    RecipeCommentedRequestDto dto = RecipeCommentedRequestDto.builder()
        .recipientIds(tooManyRecipients)
        .commentId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeCommentedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Recipient IDs must contain between 1 and 100 items");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null comment ID")
  void shouldRejectNullCommentId() {
    RecipeCommentedRequestDto dto = RecipeCommentedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .build();

    Set<ConstraintViolation<RecipeCommentedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Comment ID is required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject all null fields")
  void shouldRejectAllNullFields() {
    RecipeCommentedRequestDto dto = RecipeCommentedRequestDto.builder().build();

    Set<ConstraintViolation<RecipeCommentedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(2);

    Set<String> messages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(java.util.stream.Collectors.toSet());

    assertThat(messages).containsExactlyInAnyOrder(
        "Recipient IDs are required",
        "Comment ID is required"
    );
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    UUID commentId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(recipientId);

    RecipeCommentedRequestDto dto1 = RecipeCommentedRequestDto.builder()
        .recipientIds(recipientIds)
        .commentId(commentId)
        .build();

    RecipeCommentedRequestDto dto2 = RecipeCommentedRequestDto.builder()
        .recipientIds(recipientIds)
        .commentId(commentId)
        .build();

    RecipeCommentedRequestDto differentDto = RecipeCommentedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .commentId(UUID.randomUUID())
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
    UUID commentId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();

    RecipeCommentedRequestDto dto = RecipeCommentedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .commentId(commentId)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("RecipeCommentedRequestDto");
    assertThat(toString).contains(commentId.toString());
    assertThat(toString).contains(recipientId.toString());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all-args constructor")
  void shouldSupportAllArgsConstructor() {
    UUID commentId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(UUID.randomUUID());

    RecipeCommentedRequestDto dto = new RecipeCommentedRequestDto(recipientIds, commentId);

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getCommentId()).isEqualTo(commentId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support no-args constructor and setters")
  void shouldSupportNoArgsConstructorAndSetters() {
    UUID commentId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(UUID.randomUUID());

    RecipeCommentedRequestDto dto = new RecipeCommentedRequestDto();
    dto.setRecipientIds(recipientIds);
    dto.setCommentId(commentId);

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getCommentId()).isEqualTo(commentId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate DTO with single recipient")
  void shouldValidateDtoWithSingleRecipient() {
    RecipeCommentedRequestDto dto = RecipeCommentedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .commentId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeCommentedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate DTO with multiple recipients")
  void shouldValidateDtoWithMultipleRecipients() {
    List<UUID> recipientIds = List.of(
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID()
    );

    RecipeCommentedRequestDto dto = RecipeCommentedRequestDto.builder()
        .recipientIds(recipientIds)
        .commentId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeCommentedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }
}
