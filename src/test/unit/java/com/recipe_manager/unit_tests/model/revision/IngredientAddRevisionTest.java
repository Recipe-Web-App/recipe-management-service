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
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;
import com.recipe_manager.model.revision.IngredientAddRevision;

/** Unit tests for IngredientAddRevision class. */
@Tag("unit")
class IngredientAddRevisionTest {

  private IngredientAddRevision revision;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

    revision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(new BigDecimal("2.5"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .description("All-purpose flour")
        .build();
  }

  @Test
  void shouldCreateRevisionWithCorrectCategoryAndType() {
    assertEquals(RevisionCategory.INGREDIENT, revision.getCategory());
    assertEquals(RevisionType.ADD, revision.getType());
  }

  @Test
  void shouldSetAllFields() {
    assertEquals(1L, revision.getIngredientId());
    assertEquals("Flour", revision.getIngredientName());
    assertEquals(new BigDecimal("2.5"), revision.getQuantity());
    assertEquals(IngredientUnit.CUP, revision.getUnit());
    assertEquals(false, revision.getIsOptional());
    assertEquals("All-purpose flour", revision.getDescription());
  }

  @Test
  void shouldCreateDefaultRevision() {
    IngredientAddRevision defaultRevision = IngredientAddRevision.builder().build();
    assertNull(defaultRevision.getCategory());
    assertNull(defaultRevision.getType());
    assertNull(defaultRevision.getIngredientId());
    assertNull(defaultRevision.getIngredientName());
    assertNull(defaultRevision.getQuantity());
    assertNull(defaultRevision.getUnit());
    assertNull(defaultRevision.getIsOptional());
    assertNull(defaultRevision.getDescription());
  }

  @Test
  void shouldValidateCorrectRevision() {
    assertTrue(revision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithMissingIngredientData() {
    IngredientAddRevision invalidRevision = IngredientAddRevision.builder()
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNullQuantity() {
    IngredientAddRevision invalidRevision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(null)
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithZeroQuantity() {
    IngredientAddRevision invalidRevision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(BigDecimal.ZERO)
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNegativeQuantity() {
    IngredientAddRevision invalidRevision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(new BigDecimal("-1.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNullUnit() {
    IngredientAddRevision invalidRevision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(new BigDecimal("2.5"))
        .unit(null)
        .isOptional(false)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNullOptionalFlag() {
    IngredientAddRevision invalidRevision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(new BigDecimal("2.5"))
        .unit(IngredientUnit.CUP)
        .isOptional(null)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldSerializeToJson() throws Exception {
    String json = objectMapper.writeValueAsString(revision);
    assertNotNull(json);
    assertTrue(json.contains("\"ingredientId\":1"));
    assertTrue(json.contains("\"ingredientName\":\"Flour\""));
    assertTrue(json.contains("\"quantity\":2.5"));
    assertTrue(json.contains("\"unit\":\"CUP\""));
    assertTrue(json.contains("\"isOptional\":false"));
    assertTrue(json.contains("\"description\":\"All-purpose flour\""));
  }

  @Test
  void shouldDeserializeFromJson() throws Exception {
    String json = objectMapper.writeValueAsString(revision);
    IngredientAddRevision newRevision = objectMapper.readValue(json, IngredientAddRevision.class);

    assertEquals(revision.getIngredientId(), newRevision.getIngredientId());
    assertEquals(revision.getIngredientName(), newRevision.getIngredientName());
    assertEquals(revision.getQuantity(), newRevision.getQuantity());
    assertEquals(revision.getUnit(), newRevision.getUnit());
    assertEquals(revision.getIsOptional(), newRevision.getIsOptional());
    assertEquals(revision.getDescription(), newRevision.getDescription());
  }

  @Test
  void shouldSupportSuperBuilderPattern() {
    IngredientAddRevision builtRevision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(123L)
        .ingredientName("Sugar")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(true)
        .description("White sugar")
        .build();

    assertEquals(RevisionCategory.INGREDIENT, builtRevision.getCategory());
    assertEquals(RevisionType.ADD, builtRevision.getType());
    assertEquals(123L, builtRevision.getIngredientId());
    assertEquals("Sugar", builtRevision.getIngredientName());
    assertEquals(new BigDecimal("1.0"), builtRevision.getQuantity());
    assertEquals(IngredientUnit.CUP, builtRevision.getUnit());
    assertTrue(builtRevision.getIsOptional());
    assertEquals("White sugar", builtRevision.getDescription());
    assertTrue(builtRevision.isValid());
  }

  @Test
  void shouldSupportEqualsAndHashCode() {
    IngredientAddRevision sameRevision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(new BigDecimal("2.5"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .description("All-purpose flour")
        .build();

    IngredientAddRevision differentRevision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(2L)
        .ingredientName("Sugar")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .isOptional(true)
        .description("Brown sugar")
        .build();

    assertEquals(revision, sameRevision);
    assertEquals(revision.hashCode(), sameRevision.hashCode());
    assertFalse(revision.equals(differentRevision));
  }
}
