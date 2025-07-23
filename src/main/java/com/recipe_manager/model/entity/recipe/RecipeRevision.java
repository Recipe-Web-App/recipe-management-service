package com.recipe_manager.model.entity.recipe;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a revision to a recipe. Maps to the recipe_revisions table in the database.
 */
@Entity
@Table(name = "recipe_revisions", schema = "recipe_manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RecipeRevision {
  /** The unique ID of the revision. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "revision_id")
  private Long revisionId;

  /** The recipe entity this revision belongs to. */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  /** The user ID who made the revision. */
  @NotNull
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  /** The revision category. */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "revision_category", nullable = false)
  private RevisionCategory revisionCategory;

  /** The revision type. */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "revision_type", nullable = false)
  private RevisionType revisionType;

  /** The previous data in JSON. */
  @NotNull
  @Column(name = "previous_data", columnDefinition = "jsonb", nullable = false)
  private String previousData;

  /** The new data in JSON. */
  @NotNull
  @Column(name = "new_data", columnDefinition = "jsonb", nullable = false)
  private String newData;

  /** The change comment. */
  @Column(name = "change_comment", columnDefinition = "text")
  private String changeComment;

  /** The creation timestamp. */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  @Default
  private LocalDateTime createdAt = LocalDateTime.now();
}
