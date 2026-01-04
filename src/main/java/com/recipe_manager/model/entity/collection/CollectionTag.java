package com.recipe_manager.model.entity.collection;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a tag that can be applied to collections. Maps to the collection_tags table
 * in the database.
 */
@Entity
@Table(name = "collection_tags", schema = "recipe_manager")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "collections")
@ToString(exclude = "collections")
public class CollectionTag {
  /** Max name length as defined in DB schema. */
  private static final int MAX_NAME_LENGTH = 50;

  /** The unique ID of the tag. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tag_id")
  private Long tagId;

  /** The name of the tag. */
  @NotBlank
  @Size(max = MAX_NAME_LENGTH)
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  /** The list of collections associated with this tag. */
  @ManyToMany(mappedBy = "collectionTags")
  @Default
  private List<RecipeCollection> collections = new ArrayList<>();
}
