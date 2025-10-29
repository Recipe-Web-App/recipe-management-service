package com.recipe_manager.model.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for CollectionDto. */
@Tag("unit")
class CollectionDtoTest {

  @Test
  @DisplayName("Builder sets all fields")
  @Tag("standard-processing")
  void builderSetsAllFields() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionDto dto =
        CollectionDto.builder()
            .collectionId(1L)
            .userId(userId)
            .name("My Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(5)
            .collaboratorCount(2)
            .createdAt(now)
            .updatedAt(now)
            .build();

    assertThat(dto.getCollectionId()).isEqualTo(1L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getName()).isEqualTo("My Collection");
    assertThat(dto.getDescription()).isEqualTo("Test Description");
    assertThat(dto.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(dto.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(dto.getRecipeCount()).isEqualTo(5);
    assertThat(dto.getCollaboratorCount()).isEqualTo(2);
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("No-args constructor sets nulls")
  @Tag("standard-processing")
  void noArgsConstructorSetsDefaults() {
    CollectionDto dto = new CollectionDto();

    assertThat(dto.getCollectionId()).isNull();
    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getName()).isNull();
    assertThat(dto.getDescription()).isNull();
    assertThat(dto.getVisibility()).isNull();
    assertThat(dto.getCollaborationMode()).isNull();
    assertThat(dto.getRecipeCount()).isNull();
    assertThat(dto.getCollaboratorCount()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
  }

  @Test
  @DisplayName("All-args constructor assigns all fields")
  @Tag("standard-processing")
  void allArgsConstructorAssignsFields() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionDto dto =
        new CollectionDto(
            1L,
            userId,
            "My Collection",
            "Test Description",
            CollectionVisibility.PUBLIC,
            CollaborationMode.OWNER_ONLY,
            5,
            2,
            now,
            now);

    assertThat(dto.getCollectionId()).isEqualTo(1L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getName()).isEqualTo("My Collection");
    assertThat(dto.getDescription()).isEqualTo("Test Description");
    assertThat(dto.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(dto.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(dto.getRecipeCount()).isEqualTo(5);
    assertThat(dto.getCollaboratorCount()).isEqualTo(2);
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Setters and getters work for all fields")
  @Tag("standard-processing")
  void settersAndGettersWork() {
    CollectionDto dto = new CollectionDto();
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    dto.setCollectionId(1L);
    dto.setUserId(userId);
    dto.setName("My Collection");
    dto.setDescription("Test Description");
    dto.setVisibility(CollectionVisibility.PUBLIC);
    dto.setCollaborationMode(CollaborationMode.OWNER_ONLY);
    dto.setRecipeCount(5);
    dto.setCollaboratorCount(2);
    dto.setCreatedAt(now);
    dto.setUpdatedAt(now);

    assertThat(dto.getCollectionId()).isEqualTo(1L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getName()).isEqualTo("My Collection");
    assertThat(dto.getDescription()).isEqualTo("Test Description");
    assertThat(dto.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(dto.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(dto.getRecipeCount()).isEqualTo(5);
    assertThat(dto.getCollaboratorCount()).isEqualTo(2);
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Builder works with null description")
  @Tag("standard-processing")
  void builderWorksWithNullDescription() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionDto dto =
        CollectionDto.builder()
            .collectionId(2L)
            .userId(userId)
            .name("Another Collection")
            .description(null)
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .recipeCount(0)
            .collaboratorCount(0)
            .createdAt(now)
            .updatedAt(now)
            .build();

    assertThat(dto.getCollectionId()).isEqualTo(2L);
    assertThat(dto.getDescription()).isNull();
    assertThat(dto.getName()).isEqualTo("Another Collection");
  }

  @Test
  @DisplayName("Equals and hashCode work correctly")
  @Tag("standard-processing")
  void equalsAndHashCodeWork() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionDto dto1 =
        CollectionDto.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Test Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(3)
            .collaboratorCount(1)
            .createdAt(now)
            .updatedAt(now)
            .build();

    CollectionDto dto2 =
        CollectionDto.builder()
            .collectionId(1L)
            .userId(userId)
            .name("Test Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(3)
            .collaboratorCount(1)
            .createdAt(now)
            .updatedAt(now)
            .build();

    assertThat(dto1).isEqualTo(dto2);
    assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
  }

  @Test
  @DisplayName("ToString contains key fields")
  @Tag("standard-processing")
  void toStringContainsKeyFields() {
    UUID userId = UUID.randomUUID();

    CollectionDto dto =
        CollectionDto.builder()
            .collectionId(1L)
            .userId(userId)
            .name("My Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(5)
            .collaboratorCount(2)
            .build();

    String toString = dto.toString();
    assertThat(toString).contains("collectionId=1");
    assertThat(toString).contains("userId=" + userId);
    assertThat(toString).contains("name=My Collection");
    assertThat(toString).contains("visibility=PUBLIC");
    assertThat(toString).contains("collaborationMode=OWNER_ONLY");
  }
}
