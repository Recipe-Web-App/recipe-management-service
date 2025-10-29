package com.recipe_manager.model.entity.collection;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a collaborator for a collection. Maps to the collection_collaborators table
 * in the database.
 */
@Entity
@Table(name = "collection_collaborators", schema = "recipe_manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"collection"})
@ToString(exclude = {"collection"})
public class CollectionCollaborator {
  /** The composite ID for this collaborator. */
  @EmbeddedId private CollectionCollaboratorId id;

  /** The collection this collaborator has access to. */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("collectionId")
  @JoinColumn(name = "collection_id", nullable = false)
  private RecipeCollection collection;

  /** The user ID who granted collaborator access. */
  @NotNull
  @Column(name = "granted_by", nullable = false)
  private UUID grantedBy;

  /** The timestamp when collaborator access was granted. */
  @CreationTimestamp
  @Column(name = "granted_at", nullable = false, updatable = false)
  private LocalDateTime grantedAt;
}
