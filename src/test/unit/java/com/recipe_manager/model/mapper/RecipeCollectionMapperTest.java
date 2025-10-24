package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import com.recipe_manager.model.dto.collection.RecipeCollectionDto;
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
}
