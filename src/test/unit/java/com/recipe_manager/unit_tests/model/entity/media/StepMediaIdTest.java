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

import com.recipe_manager.model.entity.media.StepMediaId;

/**
 * Unit tests for StepMediaId composite key class.
 */
@Tag("unit")
class StepMediaIdTest {

  @Test
  void testBuilder_AllFields_CreatesCorrectId() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    assertEquals(1L, id.getStepId());
    assertEquals(2L, id.getMediaId());
  }

  @Test
  void testNoArgsConstructor_CreatesEmptyId() {
    StepMediaId id = new StepMediaId();

    assertNull(id.getStepId());
    assertNull(id.getMediaId());
  }

  @Test
  void testAllArgsConstructor_AllFields_CreatesCorrectId() {
    StepMediaId id = new StepMediaId(1L, 2L);

    assertEquals(1L, id.getStepId());
    assertEquals(2L, id.getMediaId());
  }

  @Test
  void testGettersAndSetters_AllFields_WorkCorrectly() {
    StepMediaId id = new StepMediaId();
    id.setStepId(1L);
    id.setMediaId(2L);

    assertEquals(1L, id.getStepId());
    assertEquals(2L, id.getMediaId());
  }

  @Test
  void testEquals_SameObjects_ReturnsTrue() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    assertEquals(id, id);
  }

  @Test
  void testEquals_IdenticalObjects_ReturnsTrue() {
    StepMediaId id1 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMediaId id2 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    assertEquals(id1, id2);
  }

  @Test
  void testEquals_DifferentStepId_ReturnsFalse() {
    StepMediaId id1 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMediaId id2 = StepMediaId.builder()
        .stepId(3L)
        .mediaId(2L)
        .build();

    assertNotEquals(id1, id2);
  }

  @Test
  void testEquals_DifferentMediaId_ReturnsFalse() {
    StepMediaId id1 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMediaId id2 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(3L)
        .build();

    assertNotEquals(id1, id2);
  }

  @Test
  void testEquals_NullObject_ReturnsFalse() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    assertNotEquals(id, null);
  }

  @Test
  void testEquals_DifferentClass_ReturnsFalse() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    assertNotEquals(id, "not a step media id");
  }

  @Test
  void testHashCode_IdenticalObjects_ReturnsSameHashCode() {
    StepMediaId id1 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMediaId id2 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    assertEquals(id1.hashCode(), id2.hashCode());
  }

  @Test
  void testHashCode_DifferentObjects_ReturnsDifferentHashCode() {
    StepMediaId id1 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMediaId id2 = StepMediaId.builder()
        .stepId(3L)
        .mediaId(2L)
        .build();

    assertNotEquals(id1.hashCode(), id2.hashCode());
  }

  @Test
  void testToString_ContainsExpectedFields() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    String toString = id.toString();

    assertNotNull(toString);
    // Verify key fields are present in toString
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("stepId=1"));
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("mediaId=2"));
  }

  @Test
  void testSerialization_SerializationAndDeserialization_WorksCorrectly() throws IOException, ClassNotFoundException {
    StepMediaId originalId = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    // Serialize
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(byteOut);
    out.writeObject(originalId);
    out.close();

    // Deserialize
    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
    ObjectInputStream in = new ObjectInputStream(byteIn);
    StepMediaId deserializedId = (StepMediaId) in.readObject();
    in.close();

    // Verify
    assertEquals(originalId, deserializedId);
    assertEquals(originalId.getStepId(), deserializedId.getStepId());
    assertEquals(originalId.getMediaId(), deserializedId.getMediaId());
  }

  @Test
  void testCompositeKey_BothFieldsRequired() {
    StepMediaId id1 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMediaId id2 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(3L)
        .build();

    StepMediaId id3 = StepMediaId.builder()
        .stepId(4L)
        .mediaId(2L)
        .build();

    // All combinations should be different
    assertNotEquals(id1, id2);
    assertNotEquals(id1, id3);
    assertNotEquals(id2, id3);
  }
}
