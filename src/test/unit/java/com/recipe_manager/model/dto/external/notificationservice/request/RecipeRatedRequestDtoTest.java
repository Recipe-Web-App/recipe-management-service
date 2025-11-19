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
class RecipeRatedRequestDtoTest {

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
    Long recipeId = 123L;
    UUID raterId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(recipientId);

    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .raterId(raterId)
        .build();

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto.getRaterId()).isEqualTo(raterId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO with snake_case")
  void shouldSerializeAndDeserializeWithSnakeCase() throws Exception {
    Long recipeId = 99999L;
    UUID raterId = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    UUID recipientId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    List<UUID> recipientIds = List.of(recipientId);

    RecipeRatedRequestDto original = RecipeRatedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .raterId(raterId)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"recipient_ids\"");
    assertThat(json).contains("\"recipe_id\"");
    assertThat(json).contains("\"rater_id\"");
    assertThat(json).contains("99999");
    assertThat(json).contains("550e8400-e29b-41d4-a716-446655440001");
    assertThat(json).contains("550e8400-e29b-41d4-a716-446655440002");

    RecipeRatedRequestDto deserialized = objectMapper.readValue(json, RecipeRatedRequestDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate valid DTO")
  void shouldValidateValidDto() {
    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(456L)
        .raterId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeRatedRequestDto>> violations = validator.validate(dto);
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

    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder()
        .recipientIds(maxRecipients)
        .recipeId(456L)
        .raterId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeRatedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null recipient IDs")
  void shouldRejectNullRecipientIds() {
    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder()
        .recipeId(456L)
        .raterId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeRatedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Recipient IDs are required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject empty recipient IDs list")
  void shouldRejectEmptyRecipientIds() {
    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder()
        .recipientIds(List.of())
        .recipeId(456L)
        .raterId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeRatedRequestDto>> violations = validator.validate(dto);
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

    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder()
        .recipientIds(tooManyRecipients)
        .recipeId(456L)
        .raterId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeRatedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Recipient IDs must contain between 1 and 100 items");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null recipe ID")
  void shouldRejectNullRecipeId() {
    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .raterId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeRatedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Recipe ID is required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null rater ID")
  void shouldRejectNullRaterId() {
    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(456L)
        .build();

    Set<ConstraintViolation<RecipeRatedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Rater ID is required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject all null fields")
  void shouldRejectAllNullFields() {
    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder().build();

    Set<ConstraintViolation<RecipeRatedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(3);

    Set<String> messages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(java.util.stream.Collectors.toSet());

    assertThat(messages).containsExactlyInAnyOrder(
        "Recipient IDs are required",
        "Recipe ID is required",
        "Rater ID is required"
    );
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    Long recipeId = 123L;
    UUID raterId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(recipientId);

    RecipeRatedRequestDto dto1 = RecipeRatedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .raterId(raterId)
        .build();

    RecipeRatedRequestDto dto2 = RecipeRatedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .raterId(raterId)
        .build();

    RecipeRatedRequestDto differentDto = RecipeRatedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(789L)
        .raterId(UUID.randomUUID())
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
    Long recipeId = 123L;
    UUID raterId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();

    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(recipeId)
        .raterId(raterId)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("RecipeRatedRequestDto");
    assertThat(toString).contains(recipeId.toString());
    assertThat(toString).contains(raterId.toString());
    assertThat(toString).contains(recipientId.toString());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all-args constructor")
  void shouldSupportAllArgsConstructor() {
    Long recipeId = 123L;
    UUID raterId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(UUID.randomUUID());

    RecipeRatedRequestDto dto = new RecipeRatedRequestDto(recipientIds, recipeId, raterId);

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto.getRaterId()).isEqualTo(raterId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support no-args constructor and setters")
  void shouldSupportNoArgsConstructorAndSetters() {
    Long recipeId = 123L;
    UUID raterId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(UUID.randomUUID());

    RecipeRatedRequestDto dto = new RecipeRatedRequestDto();
    dto.setRecipientIds(recipientIds);
    dto.setRecipeId(recipeId);
    dto.setRaterId(raterId);

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto.getRaterId()).isEqualTo(raterId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate DTO with single recipient")
  void shouldValidateDtoWithSingleRecipient() {
    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(456L)
        .raterId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeRatedRequestDto>> violations = validator.validate(dto);
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

    RecipeRatedRequestDto dto = RecipeRatedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(456L)
        .raterId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeRatedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }
}
