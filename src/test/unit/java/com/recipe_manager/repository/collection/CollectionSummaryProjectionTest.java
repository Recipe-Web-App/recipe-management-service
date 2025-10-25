package com.recipe_manager.repository.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

/** Unit tests for CollectionSummaryProjection interface. */
@Tag("unit")
class CollectionSummaryProjectionTest {

  /**
   * Test that the projection interface methods are correctly defined and can be implemented.
   *
   * <p>This test creates an anonymous implementation of the projection interface and verifies all
   * getters work correctly.
   */
  @Test
  void testProjectionInterfaceMethods() {
    // Given: Create test data
    final Long expectedCollectionId = 123L;
    final String expectedName = "Test Collection";
    final String expectedDescription = "Test Description";
    final CollectionVisibility expectedVisibility = CollectionVisibility.PUBLIC;
    final CollaborationMode expectedCollaborationMode = CollaborationMode.OWNER_ONLY;
    final UUID expectedOwnerId = UUID.randomUUID();
    final Integer expectedRecipeCount = 5;
    final Integer expectedCollaboratorCount = 2;
    final LocalDateTime expectedCreatedAt = LocalDateTime.now().minusDays(1);
    final LocalDateTime expectedUpdatedAt = LocalDateTime.now();

    // When: Create an anonymous implementation of the projection
    CollectionSummaryProjection projection =
        new CollectionSummaryProjection() {
          @Override
          public Long getCollectionId() {
            return expectedCollectionId;
          }

          @Override
          public String getName() {
            return expectedName;
          }

          @Override
          public String getDescription() {
            return expectedDescription;
          }

          @Override
          public CollectionVisibility getVisibility() {
            return expectedVisibility;
          }

          @Override
          public CollaborationMode getCollaborationMode() {
            return expectedCollaborationMode;
          }

          @Override
          public UUID getOwnerId() {
            return expectedOwnerId;
          }

          @Override
          public Integer getRecipeCount() {
            return expectedRecipeCount;
          }

          @Override
          public Integer getCollaboratorCount() {
            return expectedCollaboratorCount;
          }

          @Override
          public LocalDateTime getCreatedAt() {
            return expectedCreatedAt;
          }

          @Override
          public LocalDateTime getUpdatedAt() {
            return expectedUpdatedAt;
          }
        };

    // Then: Verify all getters return expected values
    assertNotNull(projection, "Projection should not be null");
    assertEquals(expectedCollectionId, projection.getCollectionId(), "Collection ID should match");
    assertEquals(expectedName, projection.getName(), "Name should match");
    assertEquals(expectedDescription, projection.getDescription(), "Description should match");
    assertEquals(expectedVisibility, projection.getVisibility(), "Visibility should match");
    assertEquals(
        expectedCollaborationMode,
        projection.getCollaborationMode(),
        "Collaboration mode should match");
    assertEquals(expectedOwnerId, projection.getOwnerId(), "Owner ID should match");
    assertEquals(expectedRecipeCount, projection.getRecipeCount(), "Recipe count should match");
    assertEquals(
        expectedCollaboratorCount,
        projection.getCollaboratorCount(),
        "Collaborator count should match");
    assertEquals(expectedCreatedAt, projection.getCreatedAt(), "Created at should match");
    assertEquals(expectedUpdatedAt, projection.getUpdatedAt(), "Updated at should match");
  }

  /** Test that projection handles null description correctly. */
  @Test
  void testProjectionWithNullDescription() {
    // Given: Create projection with null description
    CollectionSummaryProjection projection =
        new CollectionSummaryProjection() {
          @Override
          public Long getCollectionId() {
            return 1L;
          }

          @Override
          public String getName() {
            return "Test";
          }

          @Override
          public String getDescription() {
            return null; // Description is optional
          }

          @Override
          public CollectionVisibility getVisibility() {
            return CollectionVisibility.PRIVATE;
          }

          @Override
          public CollaborationMode getCollaborationMode() {
            return CollaborationMode.OWNER_ONLY;
          }

          @Override
          public UUID getOwnerId() {
            return UUID.randomUUID();
          }

          @Override
          public Integer getRecipeCount() {
            return 0;
          }

          @Override
          public Integer getCollaboratorCount() {
            return 0;
          }

          @Override
          public LocalDateTime getCreatedAt() {
            return LocalDateTime.now();
          }

          @Override
          public LocalDateTime getUpdatedAt() {
            return LocalDateTime.now();
          }
        };

    // Then: Verify null description is handled
    assertEquals(null, projection.getDescription(), "Description should be null");
  }
}
