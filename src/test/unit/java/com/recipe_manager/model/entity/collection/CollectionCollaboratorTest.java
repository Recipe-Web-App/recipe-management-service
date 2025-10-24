package com.recipe_manager.model.entity.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;

/** Unit tests for {@link CollectionCollaborator} entity. */
@Tag("unit")
class CollectionCollaboratorTest {

  private Validator validator;
  private RecipeCollection collection;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(UUID.randomUUID())
            .name("Test Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();
  }

  @Test
  @DisplayName("Should create CollectionCollaborator with builder pattern")
  void shouldCreateCollectionCollaboratorWithBuilder() {
    UUID userId = UUID.randomUUID();
    UUID grantedBy = UUID.randomUUID();
    CollectionCollaboratorId id =
        CollectionCollaboratorId.builder().collectionId(1L).userId(userId).build();
    LocalDateTime now = LocalDateTime.now();

    CollectionCollaborator collaborator =
        CollectionCollaborator.builder()
            .id(id)
            .collection(collection)
            .grantedBy(grantedBy)
            .grantedAt(now)
            .build();

    assertEquals(id, collaborator.getId());
    assertEquals(collection, collaborator.getCollection());
    assertEquals(grantedBy, collaborator.getGrantedBy());
    assertEquals(now, collaborator.getGrantedAt());
  }

  @Test
  @DisplayName("Should create CollectionCollaborator with no-args constructor")
  void shouldCreateCollectionCollaboratorWithNoArgsConstructor() {
    CollectionCollaborator collaborator = new CollectionCollaborator();

    assertNull(collaborator.getId());
    assertNull(collaborator.getCollection());
    assertNull(collaborator.getGrantedBy());
    assertNull(collaborator.getGrantedAt());
  }

  @Test
  @DisplayName("Should set and get all properties")
  void shouldSetAndGetAllProperties() {
    CollectionCollaborator collaborator = new CollectionCollaborator();
    UUID userId = UUID.randomUUID();
    UUID grantedBy = UUID.randomUUID();
    CollectionCollaboratorId id =
        CollectionCollaboratorId.builder().collectionId(1L).userId(userId).build();
    LocalDateTime now = LocalDateTime.now();

    collaborator.setId(id);
    collaborator.setCollection(collection);
    collaborator.setGrantedBy(grantedBy);
    collaborator.setGrantedAt(now);

    assertEquals(id, collaborator.getId());
    assertEquals(collection, collaborator.getCollection());
    assertEquals(grantedBy, collaborator.getGrantedBy());
    assertEquals(now, collaborator.getGrantedAt());
  }

  @Test
  @DisplayName("Should validate grantedBy is not null")
  void shouldValidateGrantedByNotNull() {
    UUID userId = UUID.randomUUID();
    CollectionCollaboratorId id =
        CollectionCollaboratorId.builder().collectionId(1L).userId(userId).build();

    CollectionCollaborator collaborator =
        CollectionCollaborator.builder().id(id).grantedBy(null).build();

    Set<ConstraintViolation<CollectionCollaborator>> violations = validator.validate(collaborator);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<CollectionCollaborator> violation = violations.iterator().next();
    assertEquals(
        NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    assertEquals("grantedBy", violation.getPropertyPath().toString());
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    UUID grantedBy = UUID.randomUUID();

    CollectionCollaboratorId id1 =
        CollectionCollaboratorId.builder().collectionId(1L).userId(userId1).build();
    CollectionCollaborator collaborator1 =
        CollectionCollaborator.builder().id(id1).grantedBy(grantedBy).build();

    CollectionCollaboratorId id2 =
        CollectionCollaboratorId.builder().collectionId(1L).userId(userId1).build();
    CollectionCollaborator collaborator2 =
        CollectionCollaborator.builder().id(id2).grantedBy(grantedBy).build();

    CollectionCollaboratorId id3 =
        CollectionCollaboratorId.builder().collectionId(1L).userId(userId2).build();
    CollectionCollaborator collaborator3 =
        CollectionCollaborator.builder().id(id3).grantedBy(grantedBy).build();

    assertEquals(collaborator1, collaborator2);
    assertEquals(collaborator1.hashCode(), collaborator2.hashCode());
    assertNotEquals(collaborator1, collaborator3);
    assertNotEquals(collaborator1.hashCode(), collaborator3.hashCode());
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    UUID userId = UUID.randomUUID();
    UUID grantedBy = UUID.randomUUID();
    CollectionCollaboratorId id =
        CollectionCollaboratorId.builder().collectionId(1L).userId(userId).build();

    CollectionCollaborator collaborator =
        CollectionCollaborator.builder().id(id).grantedBy(grantedBy).build();

    String toString = collaborator.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("CollectionCollaborator"));
    assertTrue(toString.contains("grantedBy=" + grantedBy));
    // Collection should be excluded from toString
    assertTrue(!toString.contains("collection="));
  }
}
