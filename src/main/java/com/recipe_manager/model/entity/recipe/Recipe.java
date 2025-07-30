package com.recipe_manager.model.entity.recipe;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.recipe_manager.model.enums.DifficultyLevel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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

/** Entity representing a recipe in the system. Maps to the recipes table in the database. */
@Entity
@Table(name = "recipes", schema = "recipe_manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(
    exclude = {
      "recipeIngredients",
      "recipeSteps",
      "recipeRevisions",
      "recipeFavorites",
      "recipeTags"
    })
@ToString(
    exclude = {
      "recipeIngredients",
      "recipeSteps",
      "recipeRevisions",
      "recipeFavorites",
      "recipeTags"
    })
public class Recipe {
  /** Max name length as defined in DB schema. */
  private static final int MAX_TITLE_LENGTH = 255;

  /** Servings decimal precision as defined in DB schema. */
  private static final int SERVINGS_DECIMAL_PRECISION = 5;

  /** Servings decimal scale as defined in DB schema. */
  private static final int SERVINGS_DECIMAL_SCALE = 2;

  /** The unique ID of the recipe. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "recipe_id")
  private Long recipeId;

  /** The user ID of the recipe owner. */
  @NotNull
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  /** The title of the recipe. */
  @NotBlank
  @Size(max = MAX_TITLE_LENGTH)
  @Column(name = "title", nullable = false)
  private String title;

  /** The description of the recipe. */
  @Column(name = "description", columnDefinition = "text")
  private String description;

  /** The origin URL of the recipe. */
  @Column(name = "origin_url", columnDefinition = "text")
  private String originUrl;

  /** The number of servings. */
  @Column(name = "servings", precision = SERVINGS_DECIMAL_PRECISION, scale = SERVINGS_DECIMAL_SCALE)
  private BigDecimal servings;

  /** Preparation time in minutes. */
  @Column(name = "preparation_time")
  private Integer preparationTime;

  /** Cooking time in minutes. */
  @Column(name = "cooking_time")
  private Integer cookingTime;

  /** The difficulty level. */
  @Enumerated(EnumType.STRING)
  @Column(name = "difficulty")
  private DifficultyLevel difficulty;

  /** The creation timestamp. */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /** The list of recipe ingredients. */
  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  @Default
  private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

  /** The list of recipe steps. */
  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("stepNumber ASC")
  @Default
  private List<RecipeStep> recipeSteps = new ArrayList<>();

  /** The list of recipe revisions. */
  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  @Default
  private List<RecipeRevision> recipeRevisions = new ArrayList<>();

  /** The list of recipe favorites. */
  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  @Default
  private List<RecipeFavorite> recipeFavorites = new ArrayList<>();

  /** The list of recipe tags. */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "recipe_tag_junction",
      schema = "recipe_manager",
      joinColumns = @JoinColumn(name = "recipe_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @Default
  private List<RecipeTag> recipeTags = new ArrayList<>();
}
