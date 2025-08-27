package com.recipe_manager.model.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.recipe_manager.exception.RevisionSerializationException;
import com.recipe_manager.model.dto.revision.AbstractRevision;
import com.recipe_manager.model.dto.revision.IngredientAddRevision;
import com.recipe_manager.model.dto.revision.IngredientDeleteRevision;
import com.recipe_manager.model.dto.revision.IngredientUpdateRevision;
import com.recipe_manager.model.dto.revision.StepAddRevision;
import com.recipe_manager.model.dto.revision.StepDeleteRevision;
import com.recipe_manager.model.dto.revision.StepUpdateRevision;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RevisionDataConverter class.
 */
@Tag("unit")
class RevisionDataConverterTest {

  private RevisionDataConverter converter;

  @BeforeEach
  void setUp() {
    converter = new RevisionDataConverter();
  }

  @Test
  void testConvertToDatabaseColumn_NullRevision_ReturnsNull() {
    String result = converter.convertToDatabaseColumn(null);
    assertNull(result);
  }

  @Test
  void testConvertToEntityAttribute_NullData_ReturnsNull() {
    AbstractRevision result = converter.convertToEntityAttribute(null);
    assertNull(result);
  }

  @Test
  void testConvertToEntityAttribute_EmptyData_ReturnsNull() {
    AbstractRevision result = converter.convertToEntityAttribute("");
    assertNull(result);
  }

  @Test
  void testConvertToEntityAttribute_WhitespaceData_ReturnsNull() {
    AbstractRevision result = converter.convertToEntityAttribute("   ");
    assertNull(result);
  }

  @Test
  void testConvertToDatabaseColumn_IngredientAddRevision_ReturnsJsonString() {
    IngredientAddRevision revision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .changeComment("Added ingredient")
        .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0))
        .ingredientId(1L)
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .description("Sea salt")
        .build();

    String result = converter.convertToDatabaseColumn(revision);

    assertNotNull(result);
    assertJson(result);
  }

  @Test
  void testConvertToDatabaseColumn_IngredientDeleteRevision_ReturnsJsonString() {
    IngredientDeleteRevision revision = IngredientDeleteRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.DELETE)
        .changeComment("Removed ingredient")
        .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0))
        .ingredientId(2L)
        .ingredientName("Pepper")
        .build();

    String result = converter.convertToDatabaseColumn(revision);

    assertNotNull(result);
    assertJson(result);
  }

  @Test
  void testConvertToDatabaseColumn_IngredientUpdateRevision_ReturnsJsonString() {
    IngredientUpdateRevision revision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .changeComment("Updated ingredient")
        .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0))
        .ingredientId(3L)
        .ingredientName("Sugar")
        .changedField(com.recipe_manager.model.enums.IngredientField.QUANTITY)
        .previousValue(new BigDecimal("1.0"))
        .newValue(new BigDecimal("2.0"))
        .build();

    String result = converter.convertToDatabaseColumn(revision);

    assertNotNull(result);
    assertJson(result);
  }

  @Test
  void testConvertToDatabaseColumn_StepAddRevision_ReturnsJsonString() {
    StepAddRevision revision = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .changeComment("Added step")
        .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0))
        .stepId(1L)
        .stepNumber(1)
        .instruction("Mix ingredients")
        .timerSeconds(5)
        .build();

    String result = converter.convertToDatabaseColumn(revision);

    assertNotNull(result);
    assertJson(result);
  }

  @Test
  void testConvertToDatabaseColumn_StepDeleteRevision_ReturnsJsonString() {
    StepDeleteRevision revision = StepDeleteRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.DELETE)
        .changeComment("Removed step")
        .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0))
        .stepId(2L)
        .stepNumber(2)
        .build();

    String result = converter.convertToDatabaseColumn(revision);

    assertNotNull(result);
    assertJson(result);
  }

  @Test
  void testConvertToDatabaseColumn_StepUpdateRevision_ReturnsJsonString() {
    StepUpdateRevision revision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .changeComment("Updated step")
        .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0))
        .stepId(3L)
        .stepNumber(3)
        .changedField(com.recipe_manager.model.enums.StepField.INSTRUCTION)
        .previousValue("Old instruction")
        .newValue("New instruction")
        .build();

    String result = converter.convertToDatabaseColumn(revision);

    assertNotNull(result);
    assertJson(result);
  }

  @Test
  void testConvertToDatabaseColumn_GeneratesValidJson() {
    IngredientAddRevision original = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .changeComment("Added ingredient")
        .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0))
        .ingredientId(1L)
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .description("Sea salt")
        .build();

    String json = converter.convertToDatabaseColumn(original);

    assertNotNull(json);
    assertJson(json);
    // Verify the JSON contains the expected data
    assertTrue(json.contains("Salt"));
    assertTrue(json.contains("INGREDIENT"));
    assertTrue(json.contains("1.5"));
    assertTrue(json.contains("Sea salt"));
  }

  @Test
  void testConvertToEntityAttribute_InvalidJson_ThrowsRevisionSerializationException() {
    String invalidJson = "invalid json";

    RevisionSerializationException exception = assertThrows(RevisionSerializationException.class,
        () -> converter.convertToEntityAttribute(invalidJson));

    assertEquals("Failed to convert JSON to revision", exception.getMessage());
  }

  private void assertJson(String json) {
    assertNotNull(json);
    // Basic JSON validation - should start with { and end with }
    assertEquals('{', json.charAt(0));
    assertEquals('}', json.charAt(json.length() - 1));
  }
}
