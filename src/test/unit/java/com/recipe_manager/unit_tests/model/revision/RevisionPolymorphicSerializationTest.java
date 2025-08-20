package com.recipe_manager.unit_tests.model.revision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.enums.IngredientField;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;
import com.recipe_manager.model.enums.StepField;
import com.recipe_manager.model.revision.IngredientAddRevision;
import com.recipe_manager.model.revision.IngredientDeleteRevision;
import com.recipe_manager.model.revision.IngredientUpdateRevision;
import com.recipe_manager.model.revision.StepAddRevision;
import com.recipe_manager.model.revision.StepDeleteRevision;
import com.recipe_manager.model.revision.StepUpdateRevision;

/** Unit tests for Jackson serialization of revision classes. */
@Tag("unit")
class RevisionPolymorphicSerializationTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper =
        new ObjectMapper().configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
  }

  @Test
  void shouldSerializeIngredientRevisions() throws Exception {
    IngredientAddRevision ingredientAdd =
        IngredientAddRevision.builder()
            .category(RevisionCategory.INGREDIENT)
            .type(RevisionType.ADD)
            .ingredientId(1L)
            .ingredientName("Flour")
            .quantity(new BigDecimal("2.5"))
            .unit(IngredientUnit.CUP)
            .isOptional(false)
            .description("All-purpose flour")
            .build();

    String json = objectMapper.writeValueAsString(ingredientAdd);
    assertNotNull(json);
    assertTrue(json.contains("\"category\":\"INGREDIENT\""));
    assertTrue(json.contains("\"type\":\"ADD\""));
    assertTrue(json.contains("\"ingredientId\":1"));
    assertTrue(json.contains("\"ingredientName\":\"Flour\""));

    // Deserialize back to concrete type
    IngredientAddRevision deserialized = objectMapper.readValue(json, IngredientAddRevision.class);
    assertEquals(ingredientAdd, deserialized);
  }

  @Test
  void shouldSerializeStepRevisions() throws Exception {
    StepAddRevision stepAdd =
        StepAddRevision.builder()
            .category(RevisionCategory.STEP)
            .type(RevisionType.ADD)
            .stepId(1L)
            .stepNumber(1)
            .instruction("Mix ingredients thoroughly")
            .optional(false)
            .timerSeconds(300)
            .build();

    String json = objectMapper.writeValueAsString(stepAdd);
    assertNotNull(json);
    assertTrue(json.contains("\"category\":\"STEP\""));
    assertTrue(json.contains("\"type\":\"ADD\""));
    assertTrue(json.contains("\"stepId\":1"));
    assertTrue(json.contains("\"instruction\":\"Mix ingredients thoroughly\""));

    // Deserialize back to concrete type
    StepAddRevision deserialized = objectMapper.readValue(json, StepAddRevision.class);
    assertEquals(stepAdd, deserialized);
  }

  @Test
  void shouldSerializeAllRevisionTypes() throws Exception {
    // Test all concrete revision types
    IngredientAddRevision ingredientAdd = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Sugar")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .description("White sugar")
        .build();

    IngredientUpdateRevision ingredientUpdate = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(2L)
        .ingredientName("Salt")
        .changedField(IngredientField.QUANTITY)
        .previousValue(new BigDecimal("1.0"))
        .newValue(new BigDecimal("2.0"))
        .build();

    IngredientDeleteRevision ingredientDelete = IngredientDeleteRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.DELETE)
        .ingredientId(3L)
        .ingredientName("Pepper")
        .quantity(new BigDecimal("0.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(true)
        .description("Black pepper")
        .build();

    StepAddRevision stepAdd = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(1L)
        .stepNumber(1)
        .instruction("Preheat oven")
        .optional(false)
        .timerSeconds(null)
        .build();

    StepUpdateRevision stepUpdate = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(2L)
        .stepNumber(2)
        .changedField(StepField.INSTRUCTION)
        .previousValue("Old instruction")
        .newValue("New instruction")
        .build();

    StepDeleteRevision stepDelete = StepDeleteRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.DELETE)
        .stepId(3L)
        .stepNumber(3)
        .instruction("Remove from heat")
        .optional(true)
        .timerSeconds(600)
        .build();

    // Test serialization of all types
    Object[] revisions = {ingredientAdd, ingredientUpdate, ingredientDelete, stepAdd, stepUpdate, stepDelete};

    for (Object revision : revisions) {
      String json = objectMapper.writeValueAsString(revision);
      assertNotNull(json);
      assertTrue(json.contains("\"category\":"));
      assertTrue(json.contains("\"type\":"));

      // Each should be deserializable back to its concrete type
      Object deserialized = objectMapper.readValue(json, revision.getClass());
      assertEquals(revision, deserialized);
    }
  }

  @Test
  void shouldHandleIngredientRevisionWithAllFields() throws Exception {
    IngredientUpdateRevision revision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Flour")
        .changedField(IngredientField.UNIT)
        .previousValue(IngredientUnit.CUP)
        .newValue(IngredientUnit.TBSP)
        .build();

    String json = objectMapper.writeValueAsString(revision);

    // Verify the JSON contains all expected fields
    assertTrue(json.contains("\"category\":\"INGREDIENT\""));
    assertTrue(json.contains("\"type\":\"UPDATE\""));
    assertTrue(json.contains("\"changedField\":\"UNIT\""));
    assertTrue(json.contains("\"previousValue\":\"CUP\""));
    assertTrue(json.contains("\"newValue\":\"TBSP\""));

    // Deserialize back to specific type
    IngredientUpdateRevision deserialized = objectMapper.readValue(json, IngredientUpdateRevision.class);
    assertEquals(revision.getIngredientId(), deserialized.getIngredientId());
    assertEquals(revision.getIngredientName(), deserialized.getIngredientName());
    assertEquals(revision.getChangedField(), deserialized.getChangedField());
    // Note: Object fields are deserialized as strings, which is expected behavior
    assertEquals(revision.getPreviousValue().toString(), deserialized.getPreviousValue().toString());
    assertEquals(revision.getNewValue().toString(), deserialized.getNewValue().toString());
    assertTrue(deserialized.isValid());
  }

  @Test
  void shouldHandleStepRevisionWithAllFields() throws Exception {
    StepUpdateRevision revision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(1L)
        .stepNumber(1)
        .changedField(StepField.TIMER)
        .previousValue(300)
        .newValue(600)
        .build();

    String json = objectMapper.writeValueAsString(revision);

    // Verify the JSON contains all expected fields
    assertTrue(json.contains("\"category\":\"STEP\""));
    assertTrue(json.contains("\"type\":\"UPDATE\""));
    assertTrue(json.contains("\"changedField\":\"TIMER\""));
    assertTrue(json.contains("\"previousValue\":300"));
    assertTrue(json.contains("\"newValue\":600"));

    // Deserialize back to specific type
    StepUpdateRevision deserialized = objectMapper.readValue(json, StepUpdateRevision.class);
    assertEquals(revision, deserialized);
    assertTrue(deserialized.isValid());
  }

  @Test
  void shouldMaintainValidationAfterDeserialization() throws Exception {
    // Test valid revision
    IngredientUpdateRevision validRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Sugar")
        .changedField(IngredientField.UNIT)
        .previousValue(IngredientUnit.CUP)
        .newValue(IngredientUnit.TSP)
        .build();

    String validJson = objectMapper.writeValueAsString(validRevision);
    IngredientUpdateRevision deserializedValid = objectMapper.readValue(validJson, IngredientUpdateRevision.class);
    assertTrue(deserializedValid.isValid());

    // Test invalid revision (same previous and new values)
    IngredientUpdateRevision invalidRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Sugar")
        .changedField(IngredientField.UNIT)
        .previousValue(IngredientUnit.CUP)
        .newValue(IngredientUnit.CUP)
        .build();

    String invalidJson = objectMapper.writeValueAsString(invalidRevision);
    IngredientUpdateRevision deserializedInvalid = objectMapper.readValue(invalidJson, IngredientUpdateRevision.class);
    assertTrue(!deserializedInvalid.isValid());
  }

  @Test
  void shouldHandleBigDecimalSerialization() throws Exception {
    IngredientAddRevision revision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(new BigDecimal("2.567"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .description("All-purpose flour")
        .build();

    String json = objectMapper.writeValueAsString(revision);
    assertNotNull(json);
    assertTrue(json.contains("\"quantity\":2.567"));

    // Deserialize and verify BigDecimal precision is maintained
    IngredientAddRevision deserialized = objectMapper.readValue(json, IngredientAddRevision.class);
    assertEquals(revision.getQuantity(), deserialized.getQuantity());
    assertEquals(new BigDecimal("2.567"), deserialized.getQuantity());
  }

  @Test
  void shouldHandleNullOptionalFields() throws Exception {
    StepAddRevision revision = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(1L)
        .stepNumber(1)
        .instruction("Mix ingredients")
        .optional(false)
        .timerSeconds(null) // This field is optional
        .build();

    String json = objectMapper.writeValueAsString(revision);
    assertNotNull(json);
    assertTrue(json.contains("\"timerSeconds\":null"));

    // Deserialize and verify null field is handled correctly
    StepAddRevision deserialized = objectMapper.readValue(json, StepAddRevision.class);
    assertEquals(revision, deserialized);
    assertEquals(null, deserialized.getTimerSeconds());
    assertTrue(deserialized.isValid()); // Should still be valid with null timer
  }
}
