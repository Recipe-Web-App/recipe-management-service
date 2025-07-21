package com.recipe_manager.model.entity.recipe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a tag that can be applied to recipes. Maps to the recipe_tags table in the
 * database.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Entity
@Table(name = "recipe_tags", schema = "recipe_manager")
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString(exclude = "recipes")
@EqualsAndHashCode(exclude = "recipes")
public class RecipeTag {

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

  /** The creation timestamp. */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** The list of recipes associated with this tag. */
  @ManyToMany(mappedBy = "recipeTags")
  private List<Recipe> recipes;

  /**
   * Returns an unmodifiable list of recipes.
   *
   * <p>Override this method with care if you subclass RecipeTag. Document extension safety.
   *
   * @return the list of recipes
   */
  public List<Recipe> getRecipes() {
    return Collections.unmodifiableList(recipes);
  }

  /**
   * Sets the list of recipes.
   *
   * <p>Override this method with care if you subclass RecipeTag. Document extension safety.
   *
   * @param recipes the list of recipes
   */
  public void setRecipes(final List<Recipe> recipes) {
    this.recipes = recipes != null ? new ArrayList<>(recipes) : new ArrayList<>();
  }

  /**
   * All-args constructor for RecipeTag.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param tagId the tag ID
   * @param name the tag name
   * @param createdAt the creation timestamp
   * @param recipes the recipes list
   */
  public RecipeTag(
      final Long tagId,
      final String name,
      final LocalDateTime createdAt,
      final List<Recipe> recipes) {
    this.tagId = tagId;
    this.name = name;
    this.createdAt = createdAt;
    this.recipes = recipes != null ? new ArrayList<>(recipes) : new ArrayList<>();
  }

  /** Builder for RecipeTag. Use to construct instances with clarity and safety. */
  public static class RecipeTagBuilder {
    /**
     * Sets the recipes list.
     *
     * @param recipes the list of recipes
     * @return this builder
     */
    public RecipeTagBuilder recipes(final List<Recipe> recipes) {
      this.recipes = recipes == null ? new ArrayList<>() : new ArrayList<>(recipes);
      return this;
    }
  }
}
