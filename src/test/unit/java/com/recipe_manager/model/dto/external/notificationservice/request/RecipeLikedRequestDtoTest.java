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
class RecipeLikedRequestDtoTest {

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
    UUID recipeId = UUID.randomUUID();
    UUID likerId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(recipientId);

    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .likerId(likerId)
        .build();

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto.getLikerId()).isEqualTo(likerId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO with snake_case")
  void shouldSerializeAndDeserializeWithSnakeCase() throws Exception {
    UUID recipeId = UUID.fromString("660e8400-e29b-41d4-a716-446655440099");
    UUID likerId = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    UUID recipientId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    List<UUID> recipientIds = List.of(recipientId);

    RecipeLikedRequestDto original = RecipeLikedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .likerId(likerId)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"recipient_ids\"");
    assertThat(json).contains("\"recipe_id\"");
    assertThat(json).contains("\"liker_id\"");
    assertThat(json).contains("660e8400-e29b-41d4-a716-446655440099");
    assertThat(json).contains("550e8400-e29b-41d4-a716-446655440001");
    assertThat(json).contains("550e8400-e29b-41d4-a716-446655440002");

    RecipeLikedRequestDto deserialized = objectMapper.readValue(json, RecipeLikedRequestDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate valid DTO")
  void shouldValidateValidDto() {
    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(UUID.randomUUID())
        .likerId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeLikedRequestDto>> violations = validator.validate(dto);
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

    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder()
        .recipientIds(maxRecipients)
        .recipeId(UUID.randomUUID())
        .likerId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeLikedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null recipient IDs")
  void shouldRejectNullRecipientIds() {
    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder()
        .recipeId(UUID.randomUUID())
        .likerId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeLikedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Recipient IDs are required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject empty recipient IDs list")
  void shouldRejectEmptyRecipientIds() {
    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder()
        .recipientIds(List.of())
        .recipeId(UUID.randomUUID())
        .likerId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeLikedRequestDto>> violations = validator.validate(dto);
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

    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder()
        .recipientIds(tooManyRecipients)
        .recipeId(UUID.randomUUID())
        .likerId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeLikedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Recipient IDs must contain between 1 and 100 items");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null recipe ID")
  void shouldRejectNullRecipeId() {
    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .likerId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeLikedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Recipe ID is required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null liker ID")
  void shouldRejectNullLikerId() {
    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeLikedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Liker ID is required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject all null fields")
  void shouldRejectAllNullFields() {
    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder().build();

    Set<ConstraintViolation<RecipeLikedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(3);

    Set<String> messages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(java.util.stream.Collectors.toSet());

    assertThat(messages).containsExactlyInAnyOrder(
        "Recipient IDs are required",
        "Recipe ID is required",
        "Liker ID is required"
    );
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    UUID recipeId = UUID.randomUUID();
    UUID likerId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(recipientId);

    RecipeLikedRequestDto dto1 = RecipeLikedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .likerId(likerId)
        .build();

    RecipeLikedRequestDto dto2 = RecipeLikedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .likerId(likerId)
        .build();

    RecipeLikedRequestDto differentDto = RecipeLikedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(UUID.randomUUID())
        .likerId(UUID.randomUUID())
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
    UUID recipeId = UUID.randomUUID();
    UUID likerId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();

    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(recipeId)
        .likerId(likerId)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("RecipeLikedRequestDto");
    assertThat(toString).contains(recipeId.toString());
    assertThat(toString).contains(likerId.toString());
    assertThat(toString).contains(recipientId.toString());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all-args constructor")
  void shouldSupportAllArgsConstructor() {
    UUID recipeId = UUID.randomUUID();
    UUID likerId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(UUID.randomUUID());

    RecipeLikedRequestDto dto = new RecipeLikedRequestDto(recipientIds, recipeId, likerId);

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto.getLikerId()).isEqualTo(likerId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support no-args constructor and setters")
  void shouldSupportNoArgsConstructorAndSetters() {
    UUID recipeId = UUID.randomUUID();
    UUID likerId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(UUID.randomUUID());

    RecipeLikedRequestDto dto = new RecipeLikedRequestDto();
    dto.setRecipientIds(recipientIds);
    dto.setRecipeId(recipeId);
    dto.setLikerId(likerId);

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto.getLikerId()).isEqualTo(likerId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate DTO with single recipient")
  void shouldValidateDtoWithSingleRecipient() {
    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(UUID.randomUUID())
        .likerId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeLikedRequestDto>> violations = validator.validate(dto);
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

    RecipeLikedRequestDto dto = RecipeLikedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(UUID.randomUUID())
        .likerId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeLikedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }
}
