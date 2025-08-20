package com.recipe_manager.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for IngredientField enum. */
@Tag("unit")
class IngredientFieldTest {

  @Test
  void shouldHaveCorrectNumberOfValues() {
    IngredientField[] values = IngredientField.values();
    assertEquals(4, values.length);
  }

  @Test
  void shouldContainExpectedValues() {
    IngredientField[] expectedValues = {
        IngredientField.QUANTITY,
        IngredientField.UNIT,
        IngredientField.OPTIONAL_STATUS,
        IngredientField.DESCRIPTION
    };

    for (IngredientField expectedValue : expectedValues) {
      assertNotNull(expectedValue);
    }
  }

  @Test
  void shouldHaveCorrectToStringValues() {
    assertEquals("QUANTITY", IngredientField.QUANTITY.toString());
    assertEquals("UNIT", IngredientField.UNIT.toString());
    assertEquals("OPTIONAL_STATUS", IngredientField.OPTIONAL_STATUS.toString());
    assertEquals("DESCRIPTION", IngredientField.DESCRIPTION.toString());
  }

  @Test
  void shouldSupportValueOfMethod() {
    assertEquals(IngredientField.QUANTITY, IngredientField.valueOf("QUANTITY"));
    assertEquals(IngredientField.UNIT, IngredientField.valueOf("UNIT"));
    assertEquals(IngredientField.OPTIONAL_STATUS, IngredientField.valueOf("OPTIONAL_STATUS"));
    assertEquals(IngredientField.DESCRIPTION, IngredientField.valueOf("DESCRIPTION"));
  }

  @Test
  void shouldHaveConsistentOrdinalValues() {
    assertEquals(0, IngredientField.QUANTITY.ordinal());
    assertEquals(1, IngredientField.UNIT.ordinal());
    assertEquals(2, IngredientField.OPTIONAL_STATUS.ordinal());
    assertEquals(3, IngredientField.DESCRIPTION.ordinal());
  }
}
