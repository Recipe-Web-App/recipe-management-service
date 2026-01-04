package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.dto.response.CollectionSummaryResponse;
import com.recipe_manager.model.entity.collection.CollectionTag;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.model.entity.collection.CollectionCollaborator;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.repository.collection.CollectionSummaryProjection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/** Unit tests for CollectionMapper. */
@Tag("unit")
@SpringBootTest(classes = {CollectionMapperImpl.class})
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
class CollectionMapperTest {

  @Autowired private CollectionMapper collectionMapper;

  @Test
  @DisplayName("Should map CollectionSummaryResponse to CollectionDto")
  @Tag("standard-processing")
  void shouldMapCollectionSummaryResponseToDto() {
    UUID ownerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionSummaryResponse summary =
        CollectionSummaryResponse.builder()
            .collectionId(1L)
            .ownerId(ownerId)
            .ownerUsername("john_doe")
            .name("My Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(5)
            .collaboratorCount(2)
            .createdAt(now)
            .updatedAt(now)
            .build();

    CollectionDto result = collectionMapper.toDto(summary);

    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(1L);
    assertThat(result.getUserId()).isEqualTo(ownerId); // ownerId mapped to userId
    assertThat(result.getName()).isEqualTo("My Collection");
    assertThat(result.getDescription()).isEqualTo("Test Description");
    assertThat(result.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(result.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(result.getRecipeCount()).isEqualTo(5);
    assertThat(result.getCollaboratorCount()).isEqualTo(2);
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should handle null CollectionSummaryResponse")
  @Tag("standard-processing")
  void shouldHandleNullCollectionSummaryResponse() {
    CollectionDto result = collectionMapper.toDto((CollectionSummaryResponse) null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should map with null description")
  @Tag("standard-processing")
  void shouldMapWithNullDescription() {
    UUID ownerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionSummaryResponse summary =
        CollectionSummaryResponse.builder()
            .collectionId(2L)
            .ownerId(ownerId)
            .name("Another Collection")
            .description(null)
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .recipeCount(0)
            .collaboratorCount(0)
            .createdAt(now)
            .updatedAt(now)
            .build();

    CollectionDto result = collectionMapper.toDto(summary);

    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(2L);
    assertThat(result.getUserId()).isEqualTo(ownerId);
    assertThat(result.getName()).isEqualTo("Another Collection");
    assertThat(result.getDescription()).isNull();
  }

  @Test
  @DisplayName("Should map all visibility types")
  @Tag("standard-processing")
  void shouldMapAllVisibilityTypes() {
    UUID ownerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    for (CollectionVisibility visibility : CollectionVisibility.values()) {
      CollectionSummaryResponse summary =
          CollectionSummaryResponse.builder()
              .collectionId(1L)
              .ownerId(ownerId)
              .name("Test")
              .visibility(visibility)
              .collaborationMode(CollaborationMode.OWNER_ONLY)
              .recipeCount(0)
              .collaboratorCount(0)
              .createdAt(now)
              .updatedAt(now)
              .build();

      CollectionDto result = collectionMapper.toDto(summary);

      assertThat(result.getVisibility()).isEqualTo(visibility);
    }
  }

  @Test
  @DisplayName("Should map all collaboration modes")
  @Tag("standard-processing")
  void shouldMapAllCollaborationModes() {
    UUID ownerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    for (CollaborationMode mode : CollaborationMode.values()) {
      CollectionSummaryResponse summary =
          CollectionSummaryResponse.builder()
              .collectionId(1L)
              .ownerId(ownerId)
              .name("Test")
              .visibility(CollectionVisibility.PUBLIC)
              .collaborationMode(mode)
              .recipeCount(0)
              .collaboratorCount(0)
              .createdAt(now)
              .updatedAt(now)
              .build();

      CollectionDto result = collectionMapper.toDto(summary);

      assertThat(result.getCollaborationMode()).isEqualTo(mode);
    }
  }

  @Test
  @DisplayName("Should not include ownerUsername in mapped DTO")
  @Tag("standard-processing")
  void shouldNotIncludeOwnerUsernameInMappedDto() {
    UUID ownerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionSummaryResponse summary =
        CollectionSummaryResponse.builder()
            .collectionId(1L)
            .ownerId(ownerId)
            .ownerUsername("john_doe") // This field should not be in CollectionDto
            .name("My Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(5)
            .collaboratorCount(2)
            .createdAt(now)
            .updatedAt(now)
            .build();

    CollectionDto result = collectionMapper.toDto(summary);

    assertThat(result).isNotNull();
    // CollectionDto should not have ownerUsername field
    assertThat(result.getUserId()).isEqualTo(ownerId);
  }

  @Test
  @DisplayName("Should handle zero counts")
  @Tag("standard-processing")
  void shouldHandleZeroCounts() {
    UUID ownerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionSummaryResponse summary =
        CollectionSummaryResponse.builder()
            .collectionId(1L)
            .ownerId(ownerId)
            .name("Empty Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(0)
            .collaboratorCount(0)
            .createdAt(now)
            .updatedAt(now)
            .build();

    CollectionDto result = collectionMapper.toDto(summary);

    assertThat(result.getRecipeCount()).isEqualTo(0);
    assertThat(result.getCollaboratorCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("Should map CollectionSummaryProjection to CollectionDto")
  @Tag("standard-processing")
  void shouldMapCollectionSummaryProjectionToDto() {
    UUID ownerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionSummaryProjection projection = createTestProjection(1L, ownerId, now);

    CollectionDto result = collectionMapper.fromProjection(projection);

    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(1L);
    assertThat(result.getUserId()).isEqualTo(ownerId); // ownerId mapped to userId
    assertThat(result.getName()).isEqualTo("My Collection");
    assertThat(result.getDescription()).isEqualTo("Test Description");
    assertThat(result.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(result.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(result.getRecipeCount()).isEqualTo(5);
    assertThat(result.getCollaboratorCount()).isEqualTo(2);
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should handle null CollectionSummaryProjection")
  @Tag("standard-processing")
  void shouldHandleNullCollectionSummaryProjection() {
    CollectionDto result = collectionMapper.fromProjection(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should map projection with null description")
  @Tag("standard-processing")
  void shouldMapProjectionWithNullDescription() {
    UUID ownerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionSummaryProjection projection =
        new CollectionSummaryProjection() {
          @Override
          public Long getCollectionId() {
            return 2L;
          }

          @Override
          public String getName() {
            return "Another Collection";
          }

          @Override
          public String getDescription() {
            return null;
          }

          @Override
          public CollectionVisibility getVisibility() {
            return CollectionVisibility.PRIVATE;
          }

          @Override
          public CollaborationMode getCollaborationMode() {
            return CollaborationMode.ALL_USERS;
          }

          @Override
          public UUID getOwnerId() {
            return ownerId;
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
            return now;
          }

          @Override
          public LocalDateTime getUpdatedAt() {
            return now;
          }
        };

    CollectionDto result = collectionMapper.fromProjection(projection);

    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(2L);
    assertThat(result.getUserId()).isEqualTo(ownerId);
    assertThat(result.getName()).isEqualTo("Another Collection");
    assertThat(result.getDescription()).isNull();
  }

  @Test
  @DisplayName("Should map projection with all visibility types")
  @Tag("standard-processing")
  void shouldMapProjectionWithAllVisibilityTypes() {
    UUID ownerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    for (CollectionVisibility visibility : CollectionVisibility.values()) {
      CollectionSummaryProjection projection = createTestProjection(1L, ownerId, now, visibility);

      CollectionDto result = collectionMapper.fromProjection(projection);

      assertThat(result.getVisibility()).isEqualTo(visibility);
    }
  }

  @Test
  @DisplayName("Should map projection with all collaboration modes")
  @Tag("standard-processing")
  void shouldMapProjectionWithAllCollaborationModes() {
    UUID ownerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    for (CollaborationMode mode : CollaborationMode.values()) {
      CollectionSummaryProjection projection = createTestProjection(1L, ownerId, now, mode);

      CollectionDto result = collectionMapper.fromProjection(projection);

      assertThat(result.getCollaborationMode()).isEqualTo(mode);
    }
  }

  @Test
  @DisplayName("Should handle projection with zero counts")
  @Tag("standard-processing")
  void shouldHandleProjectionWithZeroCounts() {
    UUID ownerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionSummaryProjection projection =
        new CollectionSummaryProjection() {
          @Override
          public Long getCollectionId() {
            return 1L;
          }

          @Override
          public String getName() {
            return "Empty Collection";
          }

          @Override
          public String getDescription() {
            return "Test";
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
            return ownerId;
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
            return now;
          }

          @Override
          public LocalDateTime getUpdatedAt() {
            return now;
          }
        };

    CollectionDto result = collectionMapper.fromProjection(projection);

    assertThat(result.getRecipeCount()).isEqualTo(0);
    assertThat(result.getCollaboratorCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("Should map RecipeCollection entity to CollectionDto")
  @Tag("standard-processing")
  void shouldMapRecipeCollectionEntityToDto() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("My Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .build();

    CollectionDto result = collectionMapper.toDto(collection);

    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(1L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getName()).isEqualTo("My Collection");
    assertThat(result.getDescription()).isEqualTo("Test Description");
    assertThat(result.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(result.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(result.getRecipeCount()).isEqualTo(0);
    assertThat(result.getCollaboratorCount()).isEqualTo(0);
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should handle null RecipeCollection entity")
  @Tag("standard-processing")
  void shouldHandleNullRecipeCollectionEntity() {
    CollectionDto result = collectionMapper.toDto((RecipeCollection) null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should calculate recipe count from collection items")
  @Tag("standard-processing")
  void shouldCalculateRecipeCountFromCollectionItems() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    List<RecipeCollectionItem> items = new ArrayList<>();
    items.add(new RecipeCollectionItem()); // Add mock items
    items.add(new RecipeCollectionItem());
    items.add(new RecipeCollectionItem());

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Collection With Items")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .collectionItems(items)
            .collaborators(new ArrayList<>())
            .build();

    CollectionDto result = collectionMapper.toDto(collection);

    assertThat(result.getRecipeCount()).isEqualTo(3);
    assertThat(result.getCollaboratorCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("Should calculate collaborator count from collaborators list")
  @Tag("standard-processing")
  void shouldCalculateCollaboratorCountFromCollaboratorsList() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    List<CollectionCollaborator> collaborators = new ArrayList<>();
    collaborators.add(new CollectionCollaborator()); // Add mock collaborators
    collaborators.add(new CollectionCollaborator());

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Collection With Collaborators")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .createdAt(now)
            .updatedAt(now)
            .collectionItems(new ArrayList<>())
            .collaborators(collaborators)
            .build();

    CollectionDto result = collectionMapper.toDto(collection);

    assertThat(result.getRecipeCount()).isEqualTo(0);
    assertThat(result.getCollaboratorCount()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should handle null collection items list")
  @Tag("standard-processing")
  void shouldHandleNullCollectionItemsList() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .collectionItems(null)
            .collaborators(new ArrayList<>())
            .build();

    CollectionDto result = collectionMapper.toDto(collection);

    assertThat(result.getRecipeCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("Should handle null collaborators list")
  @Tag("standard-processing")
  void shouldHandleNullCollaboratorsList() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .collectionItems(new ArrayList<>())
            .collaborators(null)
            .build();

    CollectionDto result = collectionMapper.toDto(collection);

    assertThat(result.getCollaboratorCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("Should map entity with null description")
  @Tag("standard-processing")
  void shouldMapEntityWithNullDescription() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Collection Without Description")
            .description(null)
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .createdAt(now)
            .updatedAt(now)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .build();

    CollectionDto result = collectionMapper.toDto(collection);

    assertThat(result).isNotNull();
    assertThat(result.getDescription()).isNull();
  }

  @Test
  @DisplayName("Should map entity with all visibility types")
  @Tag("standard-processing")
  void shouldMapEntityWithAllVisibilityTypes() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    for (CollectionVisibility visibility : CollectionVisibility.values()) {
      RecipeCollection collection =
          RecipeCollection.builder()
              .collectionId(1L)
              .userId(userId)
              .name("Test")
              .visibility(visibility)
              .collaborationMode(CollaborationMode.OWNER_ONLY)
              .createdAt(now)
              .updatedAt(now)
              .collectionItems(new ArrayList<>())
              .collaborators(new ArrayList<>())
              .build();

      CollectionDto result = collectionMapper.toDto(collection);

      assertThat(result.getVisibility()).isEqualTo(visibility);
    }
  }

  @Test
  @DisplayName("Should map entity with all collaboration modes")
  @Tag("standard-processing")
  void shouldMapEntityWithAllCollaborationModes() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    for (CollaborationMode mode : CollaborationMode.values()) {
      RecipeCollection collection =
          RecipeCollection.builder()
              .collectionId(1L)
              .userId(userId)
              .name("Test")
              .visibility(CollectionVisibility.PUBLIC)
              .collaborationMode(mode)
              .createdAt(now)
              .updatedAt(now)
              .collectionItems(new ArrayList<>())
              .collaborators(new ArrayList<>())
              .build();

      CollectionDto result = collectionMapper.toDto(collection);

      assertThat(result.getCollaborationMode()).isEqualTo(mode);
    }
  }

  private CollectionSummaryProjection createTestProjection(
      Long collectionId, UUID ownerId, LocalDateTime now) {
    return createTestProjection(
        collectionId, ownerId, now, CollectionVisibility.PUBLIC, CollaborationMode.OWNER_ONLY);
  }

  private CollectionSummaryProjection createTestProjection(
      Long collectionId, UUID ownerId, LocalDateTime now, CollectionVisibility visibility) {
    return createTestProjection(
        collectionId, ownerId, now, visibility, CollaborationMode.OWNER_ONLY);
  }

  private CollectionSummaryProjection createTestProjection(
      Long collectionId, UUID ownerId, LocalDateTime now, CollaborationMode collaborationMode) {
    return createTestProjection(
        collectionId, ownerId, now, CollectionVisibility.PUBLIC, collaborationMode);
  }

  private CollectionSummaryProjection createTestProjection(
      Long collectionId,
      UUID ownerId,
      LocalDateTime now,
      CollectionVisibility visibility,
      CollaborationMode collaborationMode) {
    return new CollectionSummaryProjection() {
      @Override
      public Long getCollectionId() {
        return collectionId;
      }

      @Override
      public String getName() {
        return "My Collection";
      }

      @Override
      public String getDescription() {
        return "Test Description";
      }

      @Override
      public CollectionVisibility getVisibility() {
        return visibility;
      }

      @Override
      public CollaborationMode getCollaborationMode() {
        return collaborationMode;
      }

      @Override
      public UUID getOwnerId() {
        return ownerId;
      }

      @Override
      public Integer getRecipeCount() {
        return 5;
      }

      @Override
      public Integer getCollaboratorCount() {
        return 2;
      }

      @Override
      public LocalDateTime getCreatedAt() {
        return now;
      }

      @Override
      public LocalDateTime getUpdatedAt() {
        return now;
      }
    };
  }

  @Test
  @DisplayName("Should map RecipeCollection to CollectionDetailsDto with empty recipes")
  @Tag("standard-processing")
  void shouldMapRecipeCollectionToCollectionDetailsDtoWithEmptyRecipes() {
    // Given
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("My Detailed Collection")
            .description("Detailed Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .collectionTags(new ArrayList<>())
            .build();

    // When
    var result = collectionMapper.toDetailsDto(collection);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(1L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getName()).isEqualTo("My Detailed Collection");
    assertThat(result.getDescription()).isEqualTo("Detailed Description");
    assertThat(result.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(result.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getUpdatedAt()).isEqualTo(now);
    assertThat(result.getRecipes()).isEmpty();
    assertThat(result.getCollaborators()).isEmpty();
    assertThat(result.getTags()).isEmpty();
  }

  @Test
  @DisplayName("Should map RecipeCollection to CollectionDetailsDto with tags")
  @Tag("standard-processing")
  void shouldMapRecipeCollectionToCollectionDetailsDtoWithTags() {
    // Given
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionTag tag1 = CollectionTag.builder().tagId(1L).name("breakfast").build();
    CollectionTag tag2 = CollectionTag.builder().tagId(2L).name("quick").build();
    List<CollectionTag> tags = new ArrayList<>();
    tags.add(tag1);
    tags.add(tag2);

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Tagged Collection")
            .description("Collection with tags")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .collectionTags(tags)
            .build();

    // When
    var result = collectionMapper.toDetailsDto(collection);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTags()).hasSize(2);
    assertThat(result.getTags().get(0).getTagId()).isEqualTo(1L);
    assertThat(result.getTags().get(0).getName()).isEqualTo("breakfast");
    assertThat(result.getTags().get(1).getTagId()).isEqualTo(2L);
    assertThat(result.getTags().get(1).getName()).isEqualTo("quick");
  }

  @Test
  @DisplayName("Should handle null RecipeCollection for toDetailsDto")
  @Tag("standard-processing")
  void shouldHandleNullForToDetailsDto() {
    // When
    var result = collectionMapper.toDetailsDto(null);

    // Then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should map RecipeCollectionItem to CollectionRecipeDto with recipe metadata")
  @Tag("standard-processing")
  void shouldMapRecipeCollectionItemToCollectionRecipeDto() {
    // Given
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    com.recipe_manager.model.entity.recipe.Recipe recipe =
        com.recipe_manager.model.entity.recipe.Recipe.builder()
            .recipeId(1L)
            .title("Test Recipe")
            .description("A delicious test recipe")
            .userId(userId)
            .createdAt(now)
            .updatedAt(now)
            .build();

    com.recipe_manager.model.entity.collection.RecipeCollectionItemId itemId =
        com.recipe_manager.model.entity.collection.RecipeCollectionItemId.builder()
            .collectionId(10L)
            .recipeId(1L)
            .build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(itemId)
            .recipe(recipe)
            .displayOrder(5)
            .addedBy(userId)
            .addedAt(now)
            .build();

    // When
    var result = collectionMapper.toRecipeDto(item);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(1L);
    assertThat(result.getRecipeTitle()).isEqualTo("Test Recipe");
    assertThat(result.getRecipeDescription()).isEqualTo("A delicious test recipe");
    assertThat(result.getDisplayOrder()).isEqualTo(5);
    assertThat(result.getAddedBy()).isEqualTo(userId);
    assertThat(result.getAddedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should handle null RecipeCollectionItem for toRecipeDto")
  @Tag("standard-processing")
  void shouldHandleNullRecipeCollectionItemForToRecipeDto() {
    // When
    var result = collectionMapper.toRecipeDto(null);

    // Then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeCollectionItem with null recipe")
  @Tag("standard-processing")
  void shouldHandleRecipeCollectionItemWithNullRecipe() {
    // Given
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    com.recipe_manager.model.entity.collection.RecipeCollectionItemId itemId =
        com.recipe_manager.model.entity.collection.RecipeCollectionItemId.builder()
            .collectionId(10L)
            .recipeId(1L)
            .build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(itemId)
            .recipe(null)
            .displayOrder(5)
            .addedBy(userId)
            .addedAt(now)
            .build();

    // When
    var result = collectionMapper.toRecipeDto(item);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(1L);
    assertThat(result.getRecipeTitle()).isNull();
    assertThat(result.getRecipeDescription()).isNull();
    assertThat(result.getDisplayOrder()).isEqualTo(5);
  }

  @Test
  @DisplayName("Should map list of RecipeCollectionItems to list of CollectionRecipeDtos")
  @Tag("standard-processing")
  void shouldMapRecipeCollectionItemListToCollectionRecipeDtoList() {
    // Given
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    com.recipe_manager.model.entity.recipe.Recipe recipe1 =
        com.recipe_manager.model.entity.recipe.Recipe.builder()
            .recipeId(1L)
            .title("Recipe 1")
            .description("Description 1")
            .userId(userId)
            .createdAt(now)
            .updatedAt(now)
            .build();

    com.recipe_manager.model.entity.recipe.Recipe recipe2 =
        com.recipe_manager.model.entity.recipe.Recipe.builder()
            .recipeId(2L)
            .title("Recipe 2")
            .description("Description 2")
            .userId(userId)
            .createdAt(now)
            .updatedAt(now)
            .build();

    RecipeCollectionItem item1 =
        RecipeCollectionItem.builder()
            .id(
                com.recipe_manager.model.entity.collection.RecipeCollectionItemId.builder()
                    .collectionId(10L)
                    .recipeId(1L)
                    .build())
            .recipe(recipe1)
            .displayOrder(10)
            .addedBy(userId)
            .addedAt(now)
            .build();

    RecipeCollectionItem item2 =
        RecipeCollectionItem.builder()
            .id(
                com.recipe_manager.model.entity.collection.RecipeCollectionItemId.builder()
                    .collectionId(10L)
                    .recipeId(2L)
                    .build())
            .recipe(recipe2)
            .displayOrder(20)
            .addedBy(userId)
            .addedAt(now)
            .build();

    List<RecipeCollectionItem> items = List.of(item1, item2);

    // When
    var result = collectionMapper.toRecipeDtoList(items);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getRecipeId()).isEqualTo(1L);
    assertThat(result.get(0).getRecipeTitle()).isEqualTo("Recipe 1");
    assertThat(result.get(0).getRecipeDescription()).isEqualTo("Description 1");
    assertThat(result.get(0).getDisplayOrder()).isEqualTo(10);
    assertThat(result.get(1).getRecipeId()).isEqualTo(2L);
    assertThat(result.get(1).getRecipeTitle()).isEqualTo("Recipe 2");
    assertThat(result.get(1).getRecipeDescription()).isEqualTo("Description 2");
    assertThat(result.get(1).getDisplayOrder()).isEqualTo(20);
  }

  @Test
  @DisplayName("Should handle empty list for toRecipeDtoList")
  @Tag("standard-processing")
  void shouldHandleEmptyListForToRecipeDtoList() {
    // When
    var result = collectionMapper.toRecipeDtoList(new ArrayList<>());

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should handle null list for toRecipeDtoList")
  @Tag("standard-processing")
  void shouldHandleNullListForToRecipeDtoList() {
    // When
    var result = collectionMapper.toRecipeDtoList(null);

    // Then
    assertThat(result).isNull();
  }
}
