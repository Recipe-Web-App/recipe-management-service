package com.recipe_manager.unit_tests.model.entity.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.entity.media.IngredientMediaId;

/**
 * Unit tests for IngredientMediaId composite key class.
 */
@Tag("unit")
class IngredientMediaIdTest {

  @Test
  void testBuilder_AllFields_CreatesCorrectId() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    assertEquals(1L, id.getRecipeId());
    assertEquals(2L, id.getIngredientId());
    assertEquals(3L, id.getMediaId());
  }

  @Test
  void testNoArgsConstructor_CreatesEmptyId() {
    IngredientMediaId id = new IngredientMediaId();

    assertNull(id.getRecipeId());
    assertNull(id.getIngredientId());
    assertNull(id.getMediaId());
  }

  @Test
  void testAllArgsConstructor_AllFields_CreatesCorrectId() {
    IngredientMediaId id = new IngredientMediaId(1L, 2L, 3L);

    assertEquals(1L, id.getRecipeId());
    assertEquals(2L, id.getIngredientId());
    assertEquals(3L, id.getMediaId());
  }

  @Test
  void testGettersAndSetters_AllFields_WorkCorrectly() {
    IngredientMediaId id = new IngredientMediaId();
    id.setRecipeId(1L);
    id.setIngredientId(2L);
    id.setMediaId(3L);

    assertEquals(1L, id.getRecipeId());
    assertEquals(2L, id.getIngredientId());
    assertEquals(3L, id.getMediaId());
  }

  @Test
  void testEquals_SameObjects_ReturnsTrue() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    assertEquals(id, id);
  }

  @Test
  void testEquals_IdenticalObjects_ReturnsTrue() {
    IngredientMediaId id1 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMediaId id2 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    assertEquals(id1, id2);
  }

  @Test
  void testEquals_DifferentRecipeId_ReturnsFalse() {
    IngredientMediaId id1 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMediaId id2 = IngredientMediaId.builder()
        .recipeId(4L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    assertNotEquals(id1, id2);
  }

  @Test
  void testEquals_DifferentIngredientId_ReturnsFalse() {
    IngredientMediaId id1 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMediaId id2 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(4L)
        .mediaId(3L)
        .build();

    assertNotEquals(id1, id2);
  }

  @Test
  void testEquals_DifferentMediaId_ReturnsFalse() {
    IngredientMediaId id1 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMediaId id2 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(4L)
        .build();

    assertNotEquals(id1, id2);
  }

  @Test
  void testEquals_NullObject_ReturnsFalse() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    assertNotEquals(id, null);
  }

  @Test
  void testEquals_DifferentClass_ReturnsFalse() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    assertNotEquals(id, "not an ingredient media id");
  }

  @Test
  void testHashCode_IdenticalObjects_ReturnsSameHashCode() {
    IngredientMediaId id1 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMediaId id2 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    assertEquals(id1.hashCode(), id2.hashCode());
  }

  @Test
  void testHashCode_DifferentObjects_ReturnsDifferentHashCode() {
    IngredientMediaId id1 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMediaId id2 = IngredientMediaId.builder()
        .recipeId(4L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    assertNotEquals(id1.hashCode(), id2.hashCode());
  }

  @Test
  void testToString_ContainsExpectedFields() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    String toString = id.toString();

    assertNotNull(toString);
    // Verify key fields are present in toString
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("recipeId=1"));
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("ingredientId=2"));
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("mediaId=3"));
  }

  @Test
  void testSerialization_SerializationAndDeserialization_WorksCorrectly() throws IOException, ClassNotFoundException {
    IngredientMediaId originalId = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    // Serialize
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(byteOut);
    out.writeObject(originalId);
    out.close();

    // Deserialize
    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
    ObjectInputStream in = new ObjectInputStream(byteIn);
    IngredientMediaId deserializedId = (IngredientMediaId) in.readObject();
    in.close();

    // Verify
    assertEquals(originalId, deserializedId);
    assertEquals(originalId.getRecipeId(), deserializedId.getRecipeId());
    assertEquals(originalId.getIngredientId(), deserializedId.getIngredientId());
    assertEquals(originalId.getMediaId(), deserializedId.getMediaId());
  }

  @Test
  void testCompositeKey_AllThreeFieldsRequired() {
    IngredientMediaId id1 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMediaId id2 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(4L)
        .build();

    IngredientMediaId id3 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(5L)
        .mediaId(3L)
        .build();

    IngredientMediaId id4 = IngredientMediaId.builder()
        .recipeId(6L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    // All combinations should be different
    assertNotEquals(id1, id2);
    assertNotEquals(id1, id3);
    assertNotEquals(id1, id4);
    assertNotEquals(id2, id3);
    assertNotEquals(id2, id4);
    assertNotEquals(id3, id4);
  }
}
