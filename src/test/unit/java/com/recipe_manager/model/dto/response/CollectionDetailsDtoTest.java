package com.recipe_manager.model.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.collection.CollectionCollaboratorDto;
import com.recipe_manager.model.dto.collection.CollectionRecipeDto;
import com.recipe_manager.model.dto.collection.CollectionTagDto;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for CollectionDetailsDto. */
@Tag("unit")
class CollectionDetailsDtoTest {

  @Test
  @DisplayName("Builder sets all fields correctly")
  @Tag("standard-processing")
  void builderSetsAllFields() {
    // Given
    UUID userId = UUID.randomUUID();
    UUID addedBy = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionRecipeDto recipeDto =
        CollectionRecipeDto.builder()
            .recipeId(1L)
            .recipeTitle("Test Recipe")
            .recipeDescription("Test Description")
            .displayOrder(10)
            .addedBy(addedBy)
            .addedAt(now)
            .build();
    List<CollectionRecipeDto> recipes = List.of(recipeDto);

    List<CollectionCollaboratorDto> collaborators = List.of();

    CollectionTagDto tagDto = CollectionTagDto.builder().tagId(1L).name("breakfast").build();
    List<CollectionTagDto> tags = List.of(tagDto);

    // When
    CollectionDetailsDto dto =
        CollectionDetailsDto.builder()
            .collectionId(1L)
            .userId(userId)
            .name("My Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .recipes(recipes)
            .collaborators(collaborators)
            .tags(tags)
            .build();

    // Then
    assertThat(dto.getCollectionId()).isEqualTo(1L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getName()).isEqualTo("My Collection");
    assertThat(dto.getDescription()).isEqualTo("Test Description");
    assertThat(dto.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(dto.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
    assertThat(dto.getRecipes()).isEqualTo(recipes);
    assertThat(dto.getRecipes()).hasSize(1);
    assertThat(dto.getCollaborators()).isEmpty();
    assertThat(dto.getTags()).hasSize(1);
    assertThat(dto.getTags().get(0).getName()).isEqualTo("breakfast");
  }

  @Test
  @DisplayName("No-args constructor initializes with null values")
  @Tag("standard-processing")
  void noArgsConstructorSetsDefaults() {
    // When
    CollectionDetailsDto dto = new CollectionDetailsDto();

    // Then
    assertThat(dto.getCollectionId()).isNull();
    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getName()).isNull();
    assertThat(dto.getDescription()).isNull();
    assertThat(dto.getVisibility()).isNull();
    assertThat(dto.getCollaborationMode()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
    assertThat(dto.getRecipes()).isNull();
    assertThat(dto.getCollaborators()).isNull();
    assertThat(dto.getTags()).isNull();
  }

  @Test
  @DisplayName("Builder works with empty recipes list")
  @Tag("standard-processing")
  void builderWorksWithEmptyRecipesList() {
    // Given
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    // When
    CollectionDetailsDto dto =
        CollectionDetailsDto.builder()
            .collectionId(2L)
            .userId(userId)
            .name("Empty Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .createdAt(now)
            .updatedAt(now)
            .recipes(List.of())
            .collaborators(List.of())
            .build();

    // Then
    assertThat(dto.getRecipes()).isEmpty();
    assertThat(dto.getCollaborators()).isEmpty();
  }

  @Test
  @DisplayName("Builder works with null description")
  @Tag("standard-processing")
  void builderWorksWithNullDescription() {
    // Given
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    // When
    CollectionDetailsDto dto =
        CollectionDetailsDto.builder()
            .collectionId(3L)
            .userId(userId)
            .name("Collection Without Description")
            .description(null)
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(now)
            .updatedAt(now)
            .recipes(List.of())
            .collaborators(List.of())
            .build();

    // Then
    assertThat(dto.getDescription()).isNull();
    assertThat(dto.getName()).isEqualTo("Collection Without Description");
  }

  @Test
  @DisplayName("Builder works with multiple recipes in correct order")
  @Tag("standard-processing")
  void builderWorksWithMultipleRecipes() {
    // Given
    UUID userId = UUID.randomUUID();
    UUID addedBy = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionRecipeDto recipe1 =
        CollectionRecipeDto.builder()
            .recipeId(1L)
            .recipeTitle("First Recipe")
            .displayOrder(10)
            .addedBy(addedBy)
            .addedAt(now.minusDays(2))
            .build();

    CollectionRecipeDto recipe2 =
        CollectionRecipeDto.builder()
            .recipeId(2L)
            .recipeTitle("Second Recipe")
            .displayOrder(20)
            .addedBy(addedBy)
            .addedAt(now.minusDays(1))
            .build();

    List<CollectionRecipeDto> recipes = List.of(recipe1, recipe2);

    CollectionCollaboratorDto collaborator1 =
        CollectionCollaboratorDto.builder()
            .collectionId(4L)
            .userId(UUID.randomUUID())
            .grantedBy(userId)
            .grantedAt(now)
            .build();

    List<CollectionCollaboratorDto> collaborators = List.of(collaborator1);

    // When
    CollectionDetailsDto dto =
        CollectionDetailsDto.builder()
            .collectionId(4L)
            .userId(userId)
            .name("Multi-Recipe Collection")
            .visibility(CollectionVisibility.FRIENDS_ONLY)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .createdAt(now)
            .updatedAt(now)
            .recipes(recipes)
            .collaborators(collaborators)
            .build();

    // Then
    assertThat(dto.getRecipes()).hasSize(2);
    assertThat(dto.getRecipes().get(0).getRecipeId()).isEqualTo(1L);
    assertThat(dto.getRecipes().get(1).getRecipeId()).isEqualTo(2L);
    assertThat(dto.getCollaborators()).hasSize(1);
  }

  @Test
  @DisplayName("All-args constructor sets all fields correctly")
  @Tag("standard-processing")
  void allArgsConstructorSetsAllFields() {
    // Given
    UUID userId = UUID.randomUUID();
    UUID addedBy = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionRecipeDto recipeDto =
        CollectionRecipeDto.builder()
            .recipeId(1L)
            .recipeTitle("Test Recipe")
            .displayOrder(10)
            .addedBy(addedBy)
            .addedAt(now)
            .build();
    List<CollectionRecipeDto> recipes = List.of(recipeDto);

    List<CollectionCollaboratorDto> collaborators = List.of();

    CollectionTagDto tagDto = CollectionTagDto.builder().tagId(1L).name("quick").build();
    List<CollectionTagDto> tags = List.of(tagDto);

    // When
    CollectionDetailsDto dto =
        new CollectionDetailsDto(
            5L,
            userId,
            "My Collection",
            "Description",
            CollectionVisibility.PUBLIC,
            CollaborationMode.OWNER_ONLY,
            now,
            now,
            recipes,
            collaborators,
            tags);

    // Then
    assertThat(dto.getCollectionId()).isEqualTo(5L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getName()).isEqualTo("My Collection");
    assertThat(dto.getDescription()).isEqualTo("Description");
    assertThat(dto.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(dto.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
    assertThat(dto.getRecipes()).hasSize(1);
    assertThat(dto.getCollaborators()).isEmpty();
    assertThat(dto.getTags()).hasSize(1);
    assertThat(dto.getTags().get(0).getName()).isEqualTo("quick");
  }

  @Test
  @DisplayName("Setters work correctly")
  @Tag("standard-processing")
  void settersWorkCorrectly() {
    // Given
    CollectionDetailsDto dto = new CollectionDetailsDto();
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    List<CollectionRecipeDto> recipes = List.of();
    List<CollectionCollaboratorDto> collaborators = List.of();
    List<CollectionTagDto> tags = List.of();

    // When
    dto.setCollectionId(10L);
    dto.setUserId(userId);
    dto.setName("Updated Name");
    dto.setDescription("Updated Description");
    dto.setVisibility(CollectionVisibility.PRIVATE);
    dto.setCollaborationMode(CollaborationMode.ALL_USERS);
    dto.setCreatedAt(now);
    dto.setUpdatedAt(now);
    dto.setRecipes(recipes);
    dto.setCollaborators(collaborators);
    dto.setTags(tags);

    // Then
    assertThat(dto.getCollectionId()).isEqualTo(10L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getName()).isEqualTo("Updated Name");
    assertThat(dto.getDescription()).isEqualTo("Updated Description");
    assertThat(dto.getVisibility()).isEqualTo(CollectionVisibility.PRIVATE);
    assertThat(dto.getCollaborationMode()).isEqualTo(CollaborationMode.ALL_USERS);
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
    assertThat(dto.getRecipes()).isEmpty();
    assertThat(dto.getCollaborators()).isEmpty();
    assertThat(dto.getTags()).isEmpty();
  }
}
