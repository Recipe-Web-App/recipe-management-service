package com.recipe_manager.model.entity.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.recipe_manager.model.entity.recipe.Recipe;
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

/** Unit tests for {@link RecipeCollectionItem} entity. */
@Tag("unit")
class RecipeCollectionItemTest {

  private Validator validator;
  private RecipeCollection collection;
  private Recipe recipe;

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
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    recipe = Recipe.builder().recipeId(1L).userId(UUID.randomUUID()).title("Test Recipe").build();
  }

  @Test
  @DisplayName("Should create RecipeCollectionItem with builder pattern")
  void shouldCreateRecipeCollectionItemWithBuilder() {
    RecipeCollectionItemId id = RecipeCollectionItemId.builder().collectionId(1L).recipeId(1L).build();
    UUID addedBy = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(id)
            .collection(collection)
            .recipe(recipe)
            .displayOrder(10)
            .addedBy(addedBy)
            .addedAt(now)
            .build();

    assertEquals(id, item.getId());
    assertEquals(collection, item.getCollection());
    assertEquals(recipe, item.getRecipe());
    assertEquals(10, item.getDisplayOrder());
    assertEquals(addedBy, item.getAddedBy());
    assertEquals(now, item.getAddedAt());
  }

  @Test
  @DisplayName("Should create RecipeCollectionItem with no-args constructor")
  void shouldCreateRecipeCollectionItemWithNoArgsConstructor() {
    RecipeCollectionItem item = new RecipeCollectionItem();

    assertNull(item.getId());
    assertNull(item.getCollection());
    assertNull(item.getRecipe());
    assertNull(item.getDisplayOrder());
    assertNull(item.getAddedBy());
    assertNull(item.getAddedAt());
  }

  @Test
  @DisplayName("Should set and get all properties")
  void shouldSetAndGetAllProperties() {
    RecipeCollectionItem item = new RecipeCollectionItem();
    RecipeCollectionItemId id = RecipeCollectionItemId.builder().collectionId(1L).recipeId(1L).build();
    UUID addedBy = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    item.setId(id);
    item.setCollection(collection);
    item.setRecipe(recipe);
    item.setDisplayOrder(20);
    item.setAddedBy(addedBy);
    item.setAddedAt(now);

    assertEquals(id, item.getId());
    assertEquals(collection, item.getCollection());
    assertEquals(recipe, item.getRecipe());
    assertEquals(20, item.getDisplayOrder());
    assertEquals(addedBy, item.getAddedBy());
    assertEquals(now, item.getAddedAt());
  }

  @Test
  @DisplayName("Should validate displayOrder is not null")
  void shouldValidateDisplayOrderNotNull() {
    RecipeCollectionItemId id = RecipeCollectionItemId.builder().collectionId(1L).recipeId(1L).build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(id)
            .displayOrder(null)
            .addedBy(UUID.randomUUID())
            .build();

    Set<ConstraintViolation<RecipeCollectionItem>> violations = validator.validate(item);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<RecipeCollectionItem> violation = violations.iterator().next();
    assertEquals(
        NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    assertEquals("displayOrder", violation.getPropertyPath().toString());
  }

  @Test
  @DisplayName("Should validate addedBy is not null")
  void shouldValidateAddedByNotNull() {
    RecipeCollectionItemId id = RecipeCollectionItemId.builder().collectionId(1L).recipeId(1L).build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder().id(id).displayOrder(10).addedBy(null).build();

    Set<ConstraintViolation<RecipeCollectionItem>> violations = validator.validate(item);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<RecipeCollectionItem> violation = violations.iterator().next();
    assertEquals(
        NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    assertEquals("addedBy", violation.getPropertyPath().toString());
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    RecipeCollectionItemId id = RecipeCollectionItemId.builder().collectionId(1L).recipeId(1L).build();
    UUID addedBy = UUID.randomUUID();

    RecipeCollectionItem item1 =
        RecipeCollectionItem.builder().id(id).displayOrder(10).addedBy(addedBy).build();

    RecipeCollectionItem item2 =
        RecipeCollectionItem.builder().id(id).displayOrder(10).addedBy(addedBy).build();

    RecipeCollectionItemId id2 = RecipeCollectionItemId.builder().collectionId(2L).recipeId(1L).build();
    RecipeCollectionItem item3 =
        RecipeCollectionItem.builder().id(id2).displayOrder(10).addedBy(addedBy).build();

    assertEquals(item1, item2);
    assertEquals(item1.hashCode(), item2.hashCode());
    assertNotEquals(item1, item3);
    assertNotEquals(item1.hashCode(), item3.hashCode());
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    RecipeCollectionItemId id = RecipeCollectionItemId.builder().collectionId(1L).recipeId(2L).build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(id)
            .displayOrder(10)
            .addedBy(UUID.randomUUID())
            .build();

    String toString = item.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("RecipeCollectionItem"));
    assertTrue(toString.contains("displayOrder=10"));
    // Collection and recipe should be excluded from toString
    assertTrue(!toString.contains("collection="));
    assertTrue(!toString.contains("recipe="));
  }
}
