package com.recipe_manager.unit_tests.model.revision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.recipe_manager.model.revision.IngredientUpdateRevision;

/** Unit tests for IngredientUpdateRevision class. */
@Tag("unit")
class IngredientUpdateRevisionTest {

  private IngredientUpdateRevision revision;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

    revision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Flour")
        .changedField(IngredientField.QUANTITY)
        .previousValue(new BigDecimal("1.0"))
        .newValue(new BigDecimal("2.0"))
        .build();
  }

  @Test
  void shouldCreateRevisionWithCorrectCategoryAndType() {
    assertEquals(RevisionCategory.INGREDIENT, revision.getCategory());
    assertEquals(RevisionType.UPDATE, revision.getType());
  }

  @Test
  void shouldSetAllFields() {
    assertEquals(1L, revision.getIngredientId());
    assertEquals("Flour", revision.getIngredientName());
    assertEquals(IngredientField.QUANTITY, revision.getChangedField());
    assertEquals(new BigDecimal("1.0"), revision.getPreviousValue());
    assertEquals(new BigDecimal("2.0"), revision.getNewValue());
  }

  @Test
  void shouldCreateDefaultRevision() {
    IngredientUpdateRevision defaultRevision = IngredientUpdateRevision.builder().build();
    assertNull(defaultRevision.getCategory());
    assertNull(defaultRevision.getType());
    assertNull(defaultRevision.getIngredientId());
    assertNull(defaultRevision.getIngredientName());
    assertNull(defaultRevision.getChangedField());
    assertNull(defaultRevision.getPreviousValue());
    assertNull(defaultRevision.getNewValue());
  }

  @Test
  void shouldValidateCorrectRevision() {
    assertTrue(revision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithMissingIngredientData() {
    IngredientUpdateRevision invalidRevision = IngredientUpdateRevision.builder()
        .changedField(IngredientField.QUANTITY)
        .previousValue(new BigDecimal("1.0"))
        .newValue(new BigDecimal("2.0"))
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNullChangedField() {
    IngredientUpdateRevision invalidRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Flour")
        .changedField(null)
        .previousValue(new BigDecimal("1.0"))
        .newValue(new BigDecimal("2.0"))
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNullPreviousValue() {
    IngredientUpdateRevision invalidRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Flour")
        .changedField(IngredientField.QUANTITY)
        .previousValue(null)
        .newValue(new BigDecimal("2.0"))
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNullNewValue() {
    IngredientUpdateRevision invalidRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Flour")
        .changedField(IngredientField.QUANTITY)
        .previousValue(new BigDecimal("1.0"))
        .newValue(null)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithSamePreviousAndNewValue() {
    IngredientUpdateRevision invalidRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Flour")
        .changedField(IngredientField.QUANTITY)
        .previousValue(new BigDecimal("2.0"))
        .newValue(new BigDecimal("2.0"))
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldHandleDifferentFieldTypes() {
    // Test with unit change
    IngredientUpdateRevision unitRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Sugar")
        .changedField(IngredientField.UNIT)
        .previousValue(IngredientUnit.CUP)
        .newValue(IngredientUnit.TBSP)
        .build();
    assertTrue(unitRevision.isValid());

    // Test with optional status change
    IngredientUpdateRevision optionalRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Salt")
        .changedField(IngredientField.OPTIONAL_STATUS)
        .previousValue(false)
        .newValue(true)
        .build();
    assertTrue(optionalRevision.isValid());

    // Test with description change
    IngredientUpdateRevision descriptionRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Flour")
        .changedField(IngredientField.DESCRIPTION)
        .previousValue("Old description")
        .newValue("New description")
        .build();
    assertTrue(descriptionRevision.isValid());
  }

  @Test
  void shouldSerializeToJson() throws Exception {
    String json = objectMapper.writeValueAsString(revision);
    assertNotNull(json);
    assertTrue(json.contains("\"ingredientId\":1"));
    assertTrue(json.contains("\"ingredientName\":\"Flour\""));
    assertTrue(json.contains("\"changedField\":\"QUANTITY\""));
    assertTrue(json.contains("\"previousValue\":1.0"));
    assertTrue(json.contains("\"newValue\":2.0"));
  }

  @Test
  void shouldDeserializeFromJson() throws Exception {
    String json = objectMapper.writeValueAsString(revision);
    IngredientUpdateRevision newRevision = objectMapper.readValue(json, IngredientUpdateRevision.class);

    assertEquals(revision.getIngredientId(), newRevision.getIngredientId());
    assertEquals(revision.getIngredientName(), newRevision.getIngredientName());
    assertEquals(revision.getChangedField(), newRevision.getChangedField());
    assertEquals(revision.getPreviousValue(), newRevision.getPreviousValue());
    assertEquals(revision.getNewValue(), newRevision.getNewValue());
  }

  @Test
  void shouldSupportSuperBuilderPattern() {
    IngredientUpdateRevision builtRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(123L)
        .ingredientName("Sugar")
        .changedField(IngredientField.UNIT)
        .previousValue(IngredientUnit.CUP)
        .newValue(IngredientUnit.TSP)
        .build();

    assertEquals(RevisionCategory.INGREDIENT, builtRevision.getCategory());
    assertEquals(RevisionType.UPDATE, builtRevision.getType());
    assertEquals(123L, builtRevision.getIngredientId());
    assertEquals("Sugar", builtRevision.getIngredientName());
    assertEquals(IngredientField.UNIT, builtRevision.getChangedField());
    assertEquals(IngredientUnit.CUP, builtRevision.getPreviousValue());
    assertEquals(IngredientUnit.TSP, builtRevision.getNewValue());
    assertTrue(builtRevision.isValid());
  }

  @Test
  void shouldSupportEqualsAndHashCode() {
    IngredientUpdateRevision sameRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(1L)
        .ingredientName("Flour")
        .changedField(IngredientField.QUANTITY)
        .previousValue(new BigDecimal("1.0"))
        .newValue(new BigDecimal("2.0"))
        .build();

    IngredientUpdateRevision differentRevision = IngredientUpdateRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(2L)
        .ingredientName("Sugar")
        .changedField(IngredientField.UNIT)
        .previousValue(IngredientUnit.CUP)
        .newValue(IngredientUnit.TSP)
        .build();

    assertEquals(revision, sameRevision);
    assertEquals(revision.hashCode(), sameRevision.hashCode());
    assertFalse(revision.equals(differentRevision));
  }
}
