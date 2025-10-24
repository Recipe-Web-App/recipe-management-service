package com.recipe_manager.model.entity.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Unit tests for {@link RecipeCollection} entity. */
@Tag("unit")
class RecipeCollectionTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Should create RecipeCollection with builder pattern")
  void shouldCreateRecipeCollectionWithBuilder() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("My Favorites")
            .description("Collection of my favorite recipes")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .build();

    assertEquals(1L, collection.getCollectionId());
    assertEquals(userId, collection.getUserId());
    assertEquals("My Favorites", collection.getName());
    assertEquals("Collection of my favorite recipes", collection.getDescription());
    assertEquals(CollectionVisibility.PUBLIC, collection.getVisibility());
    assertEquals(CollaborationMode.OWNER_ONLY, collection.getCollaborationMode());
    assertEquals(now, collection.getCreatedAt());
    assertEquals(now, collection.getUpdatedAt());
    assertNotNull(collection.getCollectionItems());
    assertNotNull(collection.getCollaborators());
  }

  @Test
  @DisplayName("Should create RecipeCollection with no-args constructor")
  void shouldCreateRecipeCollectionWithNoArgsConstructor() {
    RecipeCollection collection = new RecipeCollection();

    assertNull(collection.getCollectionId());
    assertNull(collection.getUserId());
    assertNull(collection.getName());
    assertNull(collection.getDescription());
    assertNull(collection.getVisibility());
    assertNull(collection.getCollaborationMode());
    assertNull(collection.getCreatedAt());
    assertNull(collection.getUpdatedAt());
    assertNotNull(collection.getCollectionItems());
    assertTrue(collection.getCollectionItems().isEmpty());
    assertNotNull(collection.getCollaborators());
    assertTrue(collection.getCollaborators().isEmpty());
  }

  @Test
  @DisplayName("Should create RecipeCollection with all-args constructor")
  void shouldCreateRecipeCollectionWithAllArgsConstructor() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    ArrayList<RecipeCollectionItem> items = new ArrayList<>();
    ArrayList<CollectionCollaborator> collaborators = new ArrayList<>();

    RecipeCollection collection =
        new RecipeCollection(
            1L,
            userId,
            "Test Collection",
            "Test Description",
            CollectionVisibility.PRIVATE,
            CollaborationMode.ALL_USERS,
            now,
            now,
            items,
            collaborators);

    assertEquals(1L, collection.getCollectionId());
    assertEquals(userId, collection.getUserId());
    assertEquals("Test Collection", collection.getName());
    assertEquals("Test Description", collection.getDescription());
    assertEquals(CollectionVisibility.PRIVATE, collection.getVisibility());
    assertEquals(CollaborationMode.ALL_USERS, collection.getCollaborationMode());
    assertEquals(now, collection.getCreatedAt());
    assertEquals(now, collection.getUpdatedAt());
    assertEquals(items, collection.getCollectionItems());
    assertEquals(collaborators, collection.getCollaborators());
  }

  @Test
  @DisplayName("Should set and get all properties")
  void shouldSetAndGetAllProperties() {
    RecipeCollection collection = new RecipeCollection();
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    ArrayList<RecipeCollectionItem> items = new ArrayList<>();
    ArrayList<CollectionCollaborator> collaborators = new ArrayList<>();

    collection.setCollectionId(2L);
    collection.setUserId(userId);
    collection.setName("Updated Collection");
    collection.setDescription("Updated Description");
    collection.setVisibility(CollectionVisibility.FRIENDS_ONLY);
    collection.setCollaborationMode(CollaborationMode.SPECIFIC_USERS);
    collection.setCreatedAt(now);
    collection.setUpdatedAt(now);
    collection.setCollectionItems(items);
    collection.setCollaborators(collaborators);

    assertEquals(2L, collection.getCollectionId());
    assertEquals(userId, collection.getUserId());
    assertEquals("Updated Collection", collection.getName());
    assertEquals("Updated Description", collection.getDescription());
    assertEquals(CollectionVisibility.FRIENDS_ONLY, collection.getVisibility());
    assertEquals(CollaborationMode.SPECIFIC_USERS, collection.getCollaborationMode());
    assertEquals(now, collection.getCreatedAt());
    assertEquals(now, collection.getUpdatedAt());
    assertEquals(items, collection.getCollectionItems());
    assertEquals(collaborators, collection.getCollaborators());
  }

  @Test
  @DisplayName("Should validate userId is not null")
  void shouldValidateUserIdNotNull() {
    RecipeCollection collection =
        RecipeCollection.builder()
            .userId(null)
            .name("Test Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Set<ConstraintViolation<RecipeCollection>> violations = validator.validate(collection);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<RecipeCollection> violation = violations.iterator().next();
    assertEquals(
        NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    assertEquals("userId", violation.getPropertyPath().toString());
  }

  @Test
  @DisplayName("Should validate name is not blank")
  void shouldValidateNameNotBlank() {
    RecipeCollection collection =
        RecipeCollection.builder()
            .userId(UUID.randomUUID())
            .name("")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Set<ConstraintViolation<RecipeCollection>> violations = validator.validate(collection);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<RecipeCollection> violation = violations.iterator().next();
    assertEquals(
        NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    assertEquals("name", violation.getPropertyPath().toString());
  }

  @Test
  @DisplayName("Should validate name maximum length")
  void shouldValidateNameMaxLength() {
    String longName = "a".repeat(256); // Exceeds 255 character limit

    RecipeCollection collection =
        RecipeCollection.builder()
            .userId(UUID.randomUUID())
            .name(longName)
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Set<ConstraintViolation<RecipeCollection>> violations = validator.validate(collection);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<RecipeCollection> violation = violations.iterator().next();
    assertEquals(Size.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    assertEquals("name", violation.getPropertyPath().toString());
  }

  @Test
  @DisplayName("Should validate visibility is not null")
  void shouldValidateVisibilityNotNull() {
    RecipeCollection collection =
        RecipeCollection.builder()
            .userId(UUID.randomUUID())
            .name("Test Collection")
            .visibility(null)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Set<ConstraintViolation<RecipeCollection>> violations = validator.validate(collection);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<RecipeCollection> violation = violations.iterator().next();
    assertEquals(
        NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    assertEquals("visibility", violation.getPropertyPath().toString());
  }

  @Test
  @DisplayName("Should validate collaborationMode is not null")
  void shouldValidateCollaborationModeNotNull() {
    RecipeCollection collection =
        RecipeCollection.builder()
            .userId(UUID.randomUUID())
            .name("Test Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(null)
            .build();

    Set<ConstraintViolation<RecipeCollection>> violations = validator.validate(collection);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<RecipeCollection> violation = violations.iterator().next();
    assertEquals(
        NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    assertEquals("collaborationMode", violation.getPropertyPath().toString());
  }

  @Test
  @DisplayName("Should allow null description")
  void shouldAllowNullDescription() {
    RecipeCollection collection =
        RecipeCollection.builder()
            .userId(UUID.randomUUID())
            .name("Test Collection")
            .description(null)
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Set<ConstraintViolation<RecipeCollection>> violations = validator.validate(collection);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Should accept valid collection with all required fields")
  void shouldAcceptValidCollection() {
    RecipeCollection collection =
        RecipeCollection.builder()
            .userId(UUID.randomUUID())
            .name("Valid Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Set<ConstraintViolation<RecipeCollection>> violations = validator.validate(collection);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollection collection1 =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Collection")
            .description("Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .build();

    RecipeCollection collection2 =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Collection")
            .description("Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .build();

    RecipeCollection collection3 =
        RecipeCollection.builder()
            .collectionId(2L)
            .userId(userId)
            .name("Collection")
            .description("Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .build();

    assertEquals(collection1, collection2);
    assertEquals(collection1.hashCode(), collection2.hashCode());
    assertNotEquals(collection1, collection3);
    assertNotEquals(collection1.hashCode(), collection3.hashCode());
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(UUID.randomUUID())
            .name("My Collection")
            .description("Test")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    String toString = collection.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("RecipeCollection"));
    assertTrue(toString.contains("collectionId=1"));
    assertTrue(toString.contains("name=My Collection"));
    assertTrue(toString.contains("PUBLIC"));
    assertTrue(toString.contains("OWNER_ONLY"));
    // CollectionItems and collaborators should be excluded from toString
    assertTrue(!toString.contains("collectionItems="));
    assertTrue(!toString.contains("collaborators="));
  }

  @Test
  @DisplayName("Should initialize collections with builder defaults")
  void shouldInitializeCollectionsWithDefaults() {
    RecipeCollection collection =
        RecipeCollection.builder()
            .userId(UUID.randomUUID())
            .name("Test")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    assertNotNull(collection.getCollectionItems());
    assertTrue(collection.getCollectionItems().isEmpty());
    assertNotNull(collection.getCollaborators());
    assertTrue(collection.getCollaborators().isEmpty());
  }
}
