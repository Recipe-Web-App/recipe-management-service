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
class RecipeCollectedRequestDtoTest {

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
    Long collectionId = 456L;
    UUID collectorId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(recipientId);

    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .collectorId(collectorId)
        .collectionId(collectionId)
        .build();

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto.getCollectorId()).isEqualTo(collectorId);
    assertThat(dto.getCollectionId()).isEqualTo(collectionId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO with snake_case")
  void shouldSerializeAndDeserializeWithSnakeCase() throws Exception {
    Long recipeId = 99999L;
    Long collectionId = 88888L;
    UUID collectorId = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    UUID recipientId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    List<UUID> recipientIds = List.of(recipientId);

    RecipeCollectedRequestDto original = RecipeCollectedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .collectorId(collectorId)
        .collectionId(collectionId)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"recipient_ids\"");
    assertThat(json).contains("\"recipe_id\"");
    assertThat(json).contains("\"collector_id\"");
    assertThat(json).contains("\"collection_id\"");
    assertThat(json).contains("99999");
    assertThat(json).contains("88888");
    assertThat(json).contains("550e8400-e29b-41d4-a716-446655440001");
    assertThat(json).contains("550e8400-e29b-41d4-a716-446655440002");

    RecipeCollectedRequestDto deserialized = objectMapper.readValue(json, RecipeCollectedRequestDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate valid DTO")
  void shouldValidateValidDto() {
    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(456L)
        .collectorId(UUID.randomUUID())
        .collectionId(789L)
        .build();

    Set<ConstraintViolation<RecipeCollectedRequestDto>> violations = validator.validate(dto);
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

    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipientIds(maxRecipients)
        .recipeId(456L)
        .collectorId(UUID.randomUUID())
        .collectionId(789L)
        .build();

    Set<ConstraintViolation<RecipeCollectedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null recipient IDs")
  void shouldRejectNullRecipientIds() {
    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipeId(456L)
        .collectorId(UUID.randomUUID())
        .collectionId(789L)
        .build();

    Set<ConstraintViolation<RecipeCollectedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Recipient IDs are required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject empty recipient IDs list")
  void shouldRejectEmptyRecipientIds() {
    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of())
        .recipeId(456L)
        .collectorId(UUID.randomUUID())
        .collectionId(789L)
        .build();

    Set<ConstraintViolation<RecipeCollectedRequestDto>> violations = validator.validate(dto);
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

    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipientIds(tooManyRecipients)
        .recipeId(456L)
        .collectorId(UUID.randomUUID())
        .collectionId(789L)
        .build();

    Set<ConstraintViolation<RecipeCollectedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Recipient IDs must contain between 1 and 100 items");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null recipe ID")
  void shouldRejectNullRecipeId() {
    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .collectorId(UUID.randomUUID())
        .collectionId(789L)
        .build();

    Set<ConstraintViolation<RecipeCollectedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Recipe ID is required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null collector ID")
  void shouldRejectNullCollectorId() {
    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(456L)
        .collectionId(789L)
        .build();

    Set<ConstraintViolation<RecipeCollectedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Collector ID is required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null collection ID")
  void shouldRejectNullCollectionId() {
    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(456L)
        .collectorId(UUID.randomUUID())
        .build();

    Set<ConstraintViolation<RecipeCollectedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Collection ID is required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject all null fields")
  void shouldRejectAllNullFields() {
    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder().build();

    Set<ConstraintViolation<RecipeCollectedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(4);

    Set<String> messages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(java.util.stream.Collectors.toSet());

    assertThat(messages).containsExactlyInAnyOrder(
        "Recipient IDs are required",
        "Recipe ID is required",
        "Collector ID is required",
        "Collection ID is required"
    );
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    Long recipeId = 123L;
    Long collectionId = 456L;
    UUID collectorId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(recipientId);

    RecipeCollectedRequestDto dto1 = RecipeCollectedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .collectorId(collectorId)
        .collectionId(collectionId)
        .build();

    RecipeCollectedRequestDto dto2 = RecipeCollectedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(recipeId)
        .collectorId(collectorId)
        .collectionId(collectionId)
        .build();

    RecipeCollectedRequestDto differentDto = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(789L)
        .collectorId(UUID.randomUUID())
        .collectionId(999L)
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
    Long collectionId = 456L;
    UUID collectorId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();

    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of(recipientId))
        .recipeId(recipeId)
        .collectorId(collectorId)
        .collectionId(collectionId)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("RecipeCollectedRequestDto");
    assertThat(toString).contains(recipeId.toString());
    assertThat(toString).contains(collectionId.toString());
    assertThat(toString).contains(collectorId.toString());
    assertThat(toString).contains(recipientId.toString());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all-args constructor")
  void shouldSupportAllArgsConstructor() {
    Long recipeId = 123L;
    Long collectionId = 456L;
    UUID collectorId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(UUID.randomUUID());

    RecipeCollectedRequestDto dto = new RecipeCollectedRequestDto(
        recipientIds, recipeId, collectorId, collectionId);

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto.getCollectorId()).isEqualTo(collectorId);
    assertThat(dto.getCollectionId()).isEqualTo(collectionId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support no-args constructor and setters")
  void shouldSupportNoArgsConstructorAndSetters() {
    Long recipeId = 123L;
    Long collectionId = 456L;
    UUID collectorId = UUID.randomUUID();
    List<UUID> recipientIds = List.of(UUID.randomUUID());

    RecipeCollectedRequestDto dto = new RecipeCollectedRequestDto();
    dto.setRecipientIds(recipientIds);
    dto.setRecipeId(recipeId);
    dto.setCollectorId(collectorId);
    dto.setCollectionId(collectionId);

    assertThat(dto.getRecipientIds()).isEqualTo(recipientIds);
    assertThat(dto.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto.getCollectorId()).isEqualTo(collectorId);
    assertThat(dto.getCollectionId()).isEqualTo(collectionId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate DTO with single recipient")
  void shouldValidateDtoWithSingleRecipient() {
    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipientIds(List.of(UUID.randomUUID()))
        .recipeId(456L)
        .collectorId(UUID.randomUUID())
        .collectionId(789L)
        .build();

    Set<ConstraintViolation<RecipeCollectedRequestDto>> violations = validator.validate(dto);
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

    RecipeCollectedRequestDto dto = RecipeCollectedRequestDto.builder()
        .recipientIds(recipientIds)
        .recipeId(456L)
        .collectorId(UUID.randomUUID())
        .collectionId(789L)
        .build();

    Set<ConstraintViolation<RecipeCollectedRequestDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }
}
