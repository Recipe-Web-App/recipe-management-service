package com.recipe_manager.model.entity.collection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
 * Entity representing a recipe collection in the system. Maps to the recipe_collections table in
 * the database.
 */
@Entity
@Table(name = "recipe_collections", schema = "recipe_manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"collectionItems", "collaborators", "collectionTags"})
@ToString(exclude = {"collectionItems", "collaborators", "collectionTags"})
public class RecipeCollection {
  /** Max name length as defined in DB schema. */
  private static final int MAX_NAME_LENGTH = 255;

  /** The unique ID of the collection. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "collection_id")
  private Long collectionId;

  /** The user ID of the collection owner. */
  @NotNull
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  /** The name of the collection. */
  @NotBlank
  @Size(max = MAX_NAME_LENGTH)
  @Column(name = "name", nullable = false)
  private String name;

  /** The optional description of the collection. */
  @Column(name = "description", columnDefinition = "text")
  private String description;

  /** The visibility level of the collection (who can view). */
  @NotNull
  @Enumerated
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "visibility", nullable = false)
  private CollectionVisibility visibility;

  /** The collaboration mode (who can edit). */
  @NotNull
  @Enumerated
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "collaboration_mode", nullable = false)
  private CollaborationMode collaborationMode;

  /** The creation timestamp. */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /** The list of recipe items in this collection. */
  @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
  @Default
  private List<RecipeCollectionItem> collectionItems = new ArrayList<>();

  /** The list of collaborators for this collection. */
  @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
  @Default
  private List<CollectionCollaborator> collaborators = new ArrayList<>();

  /** The list of tags associated with this collection. */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "collection_tag_junction",
      schema = "recipe_manager",
      joinColumns = @JoinColumn(name = "collection_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @Default
  private List<CollectionTag> collectionTags = new ArrayList<>();
}
