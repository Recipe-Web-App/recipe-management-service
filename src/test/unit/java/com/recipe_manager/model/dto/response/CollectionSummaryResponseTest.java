package com.recipe_manager.model.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for CollectionSummaryResponse. */
@Tag("unit")
class CollectionSummaryResponseTest {

  @Test
  @DisplayName("Builder sets all fields")
  @Tag("standard-processing")
  void builderSetsAllFields() {
    UUID ownerId = UUID.randomUUID();

    CollectionSummaryResponse response =
        CollectionSummaryResponse.builder()
            .collectionId(1L)
            .name("Test Collection")
            .description("Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .ownerId(ownerId)
            .ownerUsername("testuser")
            .recipeCount(5)
            .collaboratorCount(2)
            .build();

    assertThat(response.getCollectionId()).isEqualTo(1L);
    assertThat(response.getName()).isEqualTo("Test Collection");
    assertThat(response.getDescription()).isEqualTo("Description");
    assertThat(response.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(response.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(response.getOwnerId()).isEqualTo(ownerId);
    assertThat(response.getOwnerUsername()).isEqualTo("testuser");
    assertThat(response.getRecipeCount()).isEqualTo(5);
    assertThat(response.getCollaboratorCount()).isEqualTo(2);
  }
}
