package com.recipe_manager.model.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.collection.CollectionRecipeDto;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for CollectionDetailsResponse. */
@Tag("unit")
class CollectionDetailsResponseTest {

  @Test
  @DisplayName("Builder sets all fields")
  @Tag("standard-processing")
  void builderSetsAllFields() {
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

    CollectionDetailsResponse response =
        CollectionDetailsResponse.builder()
            .collectionId(1L)
            .userId(userId)
            .name("My Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(1)
            .collaboratorCount(0)
            .createdAt(now)
            .updatedAt(now)
            .recipes(recipes)
            .build();

    assertThat(response.getCollectionId()).isEqualTo(1L);
    assertThat(response.getUserId()).isEqualTo(userId);
    assertThat(response.getName()).isEqualTo("My Collection");
    assertThat(response.getDescription()).isEqualTo("Test Description");
    assertThat(response.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(response.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(response.getRecipeCount()).isEqualTo(1);
    assertThat(response.getCollaboratorCount()).isEqualTo(0);
    assertThat(response.getCreatedAt()).isEqualTo(now);
    assertThat(response.getUpdatedAt()).isEqualTo(now);
    assertThat(response.getRecipes()).isEqualTo(recipes);
    assertThat(response.getRecipes()).hasSize(1);
  }

  @Test
  @DisplayName("No-args constructor sets nulls")
  @Tag("standard-processing")
  void noArgsConstructorSetsDefaults() {
    CollectionDetailsResponse response = new CollectionDetailsResponse();

    assertThat(response.getCollectionId()).isNull();
    assertThat(response.getUserId()).isNull();
    assertThat(response.getName()).isNull();
    assertThat(response.getDescription()).isNull();
    assertThat(response.getVisibility()).isNull();
    assertThat(response.getCollaborationMode()).isNull();
    assertThat(response.getRecipeCount()).isNull();
    assertThat(response.getCollaboratorCount()).isNull();
    assertThat(response.getCreatedAt()).isNull();
    assertThat(response.getUpdatedAt()).isNull();
    assertThat(response.getRecipes()).isNull();
  }

  @Test
  @DisplayName("Builder works with empty recipes list")
  @Tag("standard-processing")
  void builderWorksWithEmptyRecipesList() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionDetailsResponse response =
        CollectionDetailsResponse.builder()
            .collectionId(2L)
            .userId(userId)
            .name("Empty Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .recipeCount(0)
            .collaboratorCount(0)
            .createdAt(now)
            .updatedAt(now)
            .recipes(List.of())
            .build();

    assertThat(response.getRecipeCount()).isEqualTo(0);
    assertThat(response.getRecipes()).isEmpty();
  }
}
