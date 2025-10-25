package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import com.recipe_manager.model.dto.collection.RecipeCollectionDto;
import com.recipe_manager.model.dto.request.CreateCollectionRequest;
import com.recipe_manager.model.dto.request.UpdateCollectionRequest;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/** Unit tests for RecipeCollectionMapper. */
@Tag("unit")
@SpringBootTest(
    classes = {
      RecipeCollectionMapperImpl.class,
      RecipeCollectionItemMapperImpl.class,
      CollectionCollaboratorMapperImpl.class
    })
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
class RecipeCollectionMapperTest {

  @Autowired private RecipeCollectionMapper recipeCollectionMapper;

  @Test
  @DisplayName("Should map RecipeCollection entity to RecipeCollectionDto")
  void shouldMapRecipeCollectionEntityToDto() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Test Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .build();

    RecipeCollectionDto result = recipeCollectionMapper.toDto(collection);

    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(1L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getName()).isEqualTo("Test Collection");
    assertThat(result.getDescription()).isEqualTo("Test Description");
    assertThat(result.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(result.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getUpdatedAt()).isEqualTo(now);
    assertThat(result.getItems()).isNotNull();
    assertThat(result.getCollaborators()).isNotNull();
  }

  @Test
  @DisplayName("Should handle null RecipeCollection entity")
  void shouldHandleNullRecipeCollectionEntity() {
    RecipeCollectionDto result = recipeCollectionMapper.toDto(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should update collection from request")
  void shouldUpdateCollectionFromRequest() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollection originalCollection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Original Name")
            .description("Original Description")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .build();

    UpdateCollectionRequest updateRequest =
        UpdateCollectionRequest.builder()
            .name("Updated Name")
            .description("Updated Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .build();

    recipeCollectionMapper.updateCollectionFromRequest(updateRequest, originalCollection);

    assertThat(originalCollection.getName()).isEqualTo("Updated Name");
    assertThat(originalCollection.getDescription()).isEqualTo("Updated Description");
    assertThat(originalCollection.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(originalCollection.getCollaborationMode()).isEqualTo(CollaborationMode.ALL_USERS);
    // Verify immutable fields are not changed
    assertThat(originalCollection.getCollectionId()).isEqualTo(1L);
    assertThat(originalCollection.getUserId()).isEqualTo(userId);
    assertThat(originalCollection.getCreatedAt()).isEqualTo(now);
    assertThat(originalCollection.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should handle partial update with null fields")
  void shouldHandlePartialUpdateWithNullFields() {
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .name("Original Name")
            .description("Original Description")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    UpdateCollectionRequest partialRequest =
        UpdateCollectionRequest.builder().name("Updated Name").build();

    recipeCollectionMapper.updateCollectionFromRequest(partialRequest, collection);

    assertThat(collection.getName()).isEqualTo("Updated Name");
    // Null fields set original to null (MapStruct default behavior)
    assertThat(collection.getDescription()).isNull();
    assertThat(collection.getVisibility()).isNull();
    assertThat(collection.getCollaborationMode()).isNull();
  }

  @Test
  @DisplayName("Should handle null update request")
  void shouldHandleNullUpdateRequest() {
    RecipeCollection collection =
        RecipeCollection.builder()
            .name("Original Name")
            .visibility(CollectionVisibility.PUBLIC)
            .build();

    recipeCollectionMapper.updateCollectionFromRequest(null, collection);

    assertThat(collection.getName()).isEqualTo("Original Name");
    assertThat(collection.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
  }

  @Test
  @DisplayName("Should map CreateCollectionRequest to RecipeCollection entity")
  void shouldMapCreateCollectionRequestToEntity() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("New Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollection result = recipeCollectionMapper.fromRequest(request);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("New Collection");
    assertThat(result.getDescription()).isEqualTo("Test Description");
    assertThat(result.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(result.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    // Verify auto-generated fields are null
    assertThat(result.getCollectionId()).isNull();
    assertThat(result.getUserId()).isNull();
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
    // Verify collection fields are empty (MapStruct initializes them due to @Builder.Default)
    assertThat(result.getCollectionItems()).isEmpty();
    assertThat(result.getCollaborators()).isEmpty();
  }

  @Test
  @DisplayName("Should handle null CreateCollectionRequest")
  void shouldHandleNullCreateCollectionRequest() {
    RecipeCollection result = recipeCollectionMapper.fromRequest(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should map request with null description")
  void shouldMapRequestWithNullDescription() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Collection Without Description")
            .description(null)
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .build();

    RecipeCollection result = recipeCollectionMapper.fromRequest(request);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Collection Without Description");
    assertThat(result.getDescription()).isNull();
    assertThat(result.getVisibility()).isEqualTo(CollectionVisibility.PRIVATE);
    assertThat(result.getCollaborationMode()).isEqualTo(CollaborationMode.ALL_USERS);
  }

  @Test
  @DisplayName("Should map request with all visibility types")
  void shouldMapRequestWithAllVisibilityTypes() {
    for (CollectionVisibility visibility : CollectionVisibility.values()) {
      CreateCollectionRequest request =
          CreateCollectionRequest.builder()
              .name("Test Collection")
              .visibility(visibility)
              .collaborationMode(CollaborationMode.OWNER_ONLY)
              .build();

      RecipeCollection result = recipeCollectionMapper.fromRequest(request);

      assertThat(result.getVisibility()).isEqualTo(visibility);
    }
  }

  @Test
  @DisplayName("Should map request with all collaboration modes")
  void shouldMapRequestWithAllCollaborationModes() {
    for (CollaborationMode mode : CollaborationMode.values()) {
      CreateCollectionRequest request =
          CreateCollectionRequest.builder()
              .name("Test Collection")
              .visibility(CollectionVisibility.PUBLIC)
              .collaborationMode(mode)
              .build();

      RecipeCollection result = recipeCollectionMapper.fromRequest(request);

      assertThat(result.getCollaborationMode()).isEqualTo(mode);
    }
  }

  @Test
  @DisplayName("Should verify ignored fields remain null when mapping from request")
  void shouldVerifyIgnoredFieldsRemainNullWhenMappingFromRequest() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Test Collection")
            .description("Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollection result = recipeCollectionMapper.fromRequest(request);

    // All auto-generated fields should be null
    assertThat(result.getCollectionId())
        .as("collectionId should be null - set by JPA on save")
        .isNull();
    assertThat(result.getUserId()).as("userId should be null - set by service layer").isNull();
    assertThat(result.getCreatedAt())
        .as("createdAt should be null - set by @CreationTimestamp")
        .isNull();
    assertThat(result.getUpdatedAt())
        .as("updatedAt should be null - set by @UpdateTimestamp")
        .isNull();
    // Collection fields are initialized to empty lists due to @Builder.Default annotation
    assertThat(result.getCollectionItems())
        .as("collectionItems should be empty - initialized by entity")
        .isEmpty();
    assertThat(result.getCollaborators())
        .as("collaborators should be empty - initialized by entity")
        .isEmpty();
  }

  @Test
  @DisplayName("Should map all fields correctly for new collection")
  void shouldMapAllFieldsCorrectlyForNewCollection() {
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Complete Collection")
            .description("Complete Description")
            .visibility(CollectionVisibility.FRIENDS_ONLY)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    RecipeCollection result = recipeCollectionMapper.fromRequest(request);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Complete Collection");
    assertThat(result.getDescription()).isEqualTo("Complete Description");
    assertThat(result.getVisibility()).isEqualTo(CollectionVisibility.FRIENDS_ONLY);
    assertThat(result.getCollaborationMode()).isEqualTo(CollaborationMode.SPECIFIC_USERS);
  }
}
