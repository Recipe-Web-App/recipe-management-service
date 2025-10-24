package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for UpdateCollectionRequest. */
@Tag("unit")
class UpdateCollectionRequestTest {

  @Test
  @DisplayName("Builder sets all fields")
  @Tag("standard-processing")
  void builderSetsAllFields() {
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder()
            .name("Updated Name")
            .description("Updated Description")
            .visibility(CollectionVisibility.FRIENDS_ONLY)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    assertThat(request.getName()).isEqualTo("Updated Name");
    assertThat(request.getDescription()).isEqualTo("Updated Description");
    assertThat(request.getVisibility()).isEqualTo(CollectionVisibility.FRIENDS_ONLY);
    assertThat(request.getCollaborationMode()).isEqualTo(CollaborationMode.SPECIFIC_USERS);
  }

  @Test
  @DisplayName("All fields are optional")
  @Tag("standard-processing")
  void allFieldsAreOptional() {
    UpdateCollectionRequest request = UpdateCollectionRequest.builder().build();

    assertThat(request.getName()).isNull();
    assertThat(request.getDescription()).isNull();
    assertThat(request.getVisibility()).isNull();
    assertThat(request.getCollaborationMode()).isNull();
  }
}
