package com.recipe_manager.model.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.recipe_manager.model.dto.collection.CollectionTagDto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for CollectionTagResponse. */
@Tag("unit")
class CollectionTagResponseTest {

  @Test
  @DisplayName("Builder sets all fields")
  @Tag("standard-processing")
  void builderSetsAllFields() {
    List<CollectionTagDto> tags =
        Arrays.asList(
            CollectionTagDto.builder().tagId(1L).name("dessert").build(),
            CollectionTagDto.builder().tagId(2L).name("quick-meals").build());

    CollectionTagResponse response =
        CollectionTagResponse.builder().collectionId(10L).tags(tags).build();

    assertThat(response.getCollectionId()).isEqualTo(10L);
    assertThat(response.getTags()).hasSize(2);
    assertThat(response.getTags().get(0).getName()).isEqualTo("dessert");
    assertThat(response.getTags().get(1).getName()).isEqualTo("quick-meals");
  }

  @Test
  @DisplayName("No-args constructor sets nulls")
  @Tag("standard-processing")
  void noArgsConstructorSetsDefaults() {
    CollectionTagResponse response = new CollectionTagResponse();

    assertThat(response.getCollectionId()).isNull();
    assertThat(response.getTags()).isNull();
  }

  @Test
  @DisplayName("All-args constructor assigns all fields")
  @Tag("standard-processing")
  void allArgsConstructorAssignsFields() {
    List<CollectionTagDto> tags =
        Collections.singletonList(CollectionTagDto.builder().tagId(1L).name("dinner").build());

    CollectionTagResponse response = new CollectionTagResponse(5L, tags);

    assertThat(response.getCollectionId()).isEqualTo(5L);
    assertThat(response.getTags()).hasSize(1);
    assertThat(response.getTags().get(0).getName()).isEqualTo("dinner");
  }

  @Test
  @DisplayName("Setters and getters work for all fields")
  @Tag("standard-processing")
  void settersAndGettersWork() {
    CollectionTagResponse response = new CollectionTagResponse();
    List<CollectionTagDto> tags =
        Collections.singletonList(CollectionTagDto.builder().tagId(3L).name("lunch").build());

    response.setCollectionId(20L);
    response.setTags(tags);

    assertThat(response.getCollectionId()).isEqualTo(20L);
    assertThat(response.getTags()).isEqualTo(tags);
  }

  @Test
  @DisplayName("Equals and hashCode work correctly")
  @Tag("standard-processing")
  void equalsHashCodeWork() {
    List<CollectionTagDto> tags1 =
        Collections.singletonList(CollectionTagDto.builder().tagId(1L).name("breakfast").build());
    List<CollectionTagDto> tags2 =
        Collections.singletonList(CollectionTagDto.builder().tagId(1L).name("breakfast").build());

    CollectionTagResponse response1 =
        CollectionTagResponse.builder().collectionId(1L).tags(tags1).build();
    CollectionTagResponse response2 =
        CollectionTagResponse.builder().collectionId(1L).tags(tags2).build();

    assertThat(response1).isEqualTo(response2);
    assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
  }

  @Test
  @DisplayName("Different collection IDs result in non-equal responses")
  @Tag("standard-processing")
  void differentCollectionIdsAreNotEqual() {
    List<CollectionTagDto> tags =
        Collections.singletonList(CollectionTagDto.builder().tagId(1L).name("dinner").build());

    CollectionTagResponse response1 =
        CollectionTagResponse.builder().collectionId(1L).tags(tags).build();
    CollectionTagResponse response2 =
        CollectionTagResponse.builder().collectionId(2L).tags(tags).build();

    assertThat(response1).isNotEqualTo(response2);
  }

  @Test
  @DisplayName("ToString contains relevant fields")
  @Tag("standard-processing")
  void toStringContainsFields() {
    CollectionTagResponse response =
        CollectionTagResponse.builder()
            .collectionId(15L)
            .tags(Collections.emptyList())
            .build();

    String str = response.toString();

    assertThat(str).contains("collectionId=15");
    assertThat(str).contains("tags=[]");
  }

  @Test
  @DisplayName("Empty tags list is handled correctly")
  @Tag("standard-processing")
  void emptyTagsListHandled() {
    CollectionTagResponse response =
        CollectionTagResponse.builder()
            .collectionId(1L)
            .tags(Collections.emptyList())
            .build();

    assertThat(response.getCollectionId()).isEqualTo(1L);
    assertThat(response.getTags()).isNotNull().isEmpty();
  }
}
