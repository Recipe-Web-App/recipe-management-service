package com.recipe_manager.model.entity.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class CollectionTagTest {

  @Test
  @DisplayName("All-args constructor assigns all fields")
  @Tag("standard-processing")
  void allArgsConstructorAssignsFields() {
    List<RecipeCollection> collections = new ArrayList<>();
    CollectionTag tag = new CollectionTag(1L, "dessert", collections);

    assertThat(tag.getTagId()).isEqualTo(1L);
    assertThat(tag.getName()).isEqualTo("dessert");
    assertThat(tag.getCollections()).isSameAs(collections);
  }

  @Test
  @DisplayName("No-args constructor sets nulls and empty list")
  @Tag("standard-processing")
  void noArgsConstructorSetsDefaults() {
    CollectionTag tag = new CollectionTag();

    assertThat(tag.getTagId()).isNull();
    assertThat(tag.getName()).isNull();
    assertThat(tag.getCollections()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("Builder creates entity with default collections list")
  @Tag("standard-processing")
  void builderCreatesWithDefaults() {
    CollectionTag tag = CollectionTag.builder()
        .tagId(2L)
        .name("quick-meals")
        .build();

    assertThat(tag.getTagId()).isEqualTo(2L);
    assertThat(tag.getName()).isEqualTo("quick-meals");
    assertThat(tag.getCollections()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("Setters and getters work for all fields")
  @Tag("standard-processing")
  void settersAndGettersWork() {
    CollectionTag tag = new CollectionTag();

    tag.setTagId(3L);
    tag.setName("favorites");
    List<RecipeCollection> collections = new ArrayList<>();
    tag.setCollections(collections);

    assertThat(tag.getTagId()).isEqualTo(3L);
    assertThat(tag.getName()).isEqualTo("favorites");
    assertThat(tag.getCollections()).isSameAs(collections);
  }

  @Test
  @DisplayName("Equals/hashCode exclude collections field")
  @Tag("standard-processing")
  void equalsHashCodeExcludeCollections() {
    List<RecipeCollection> collections1 = new ArrayList<>();
    List<RecipeCollection> collections2 = new ArrayList<>();
    collections2.add(RecipeCollection.builder().collectionId(1L).build());

    CollectionTag tag1 = CollectionTag.builder()
        .tagId(1L)
        .name("dinner")
        .collections(collections1)
        .build();
    CollectionTag tag2 = CollectionTag.builder()
        .tagId(1L)
        .name("dinner")
        .collections(collections2)
        .build();

    // Tags should be equal despite different collections (collections excluded from equals)
    assertThat(tag1).isEqualTo(tag2);
    assertThat(tag1.hashCode()).isEqualTo(tag2.hashCode());
  }

  @Test
  @DisplayName("ToString excludes collections field")
  @Tag("standard-processing")
  void toStringExcludesCollections() {
    CollectionTag tag = CollectionTag.builder()
        .tagId(1L)
        .name("lunch")
        .build();

    String str = tag.toString();

    assertThat(str).contains("tagId=1");
    assertThat(str).contains("name=lunch");
    assertThat(str).doesNotContain("collections");
  }

  @Test
  @DisplayName("Different names result in non-equal tags")
  @Tag("standard-processing")
  void differentNamesAreNotEqual() {
    CollectionTag tag1 = CollectionTag.builder().tagId(1L).name("breakfast").build();
    CollectionTag tag2 = CollectionTag.builder().tagId(1L).name("brunch").build();

    assertThat(tag1).isNotEqualTo(tag2);
  }
}
