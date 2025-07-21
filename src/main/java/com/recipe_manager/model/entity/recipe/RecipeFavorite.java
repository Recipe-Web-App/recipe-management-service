package com.recipe_manager.model.entity.recipe;

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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a user's favorite recipe. Maps to the recipe_favorites table in the database.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Entity
@Table(name = "recipe_favorites", schema = "recipe_manager")
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class RecipeFavorite {

  /** The composite ID for this favorite. */
  @EmbeddedId private RecipeFavoriteId id;

  /** The recipe entity. */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("recipeId")
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  /** The user ID. */
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  /** The timestamp when the recipe was favorited. */
  @CreationTimestamp
  @Column(name = "favorited_at")
  private LocalDateTime favoritedAt;

  /**
   * Returns the composite ID.
   *
   * <p>Override this method with care if you subclass RecipeFavorite. Document extension safety.
   *
   * @return the composite ID
   */
  public RecipeFavoriteId getId() {
    return id == null ? null : new RecipeFavoriteId(id);
  }

  /**
   * Sets the composite ID.
   *
   * <p>Override this method with care if you subclass RecipeFavorite. Document extension safety.
   *
   * @param id the composite ID
   */
  public void setId(final RecipeFavoriteId id) {
    this.id = id == null ? null : new RecipeFavoriteId(id);
  }

  /**
   * Returns the recipe entity.
   *
   * <p>Override this method with care if you subclass RecipeFavorite. Document extension safety.
   *
   * @return the recipe
   */
  public Recipe getRecipe() {
    return recipe == null ? null : new Recipe(recipe);
  }

  /**
   * Sets the recipe entity.
   *
   * <p>Override this method with care if you subclass RecipeFavorite. Document extension safety.
   *
   * @param recipe the recipe
   */
  public void setRecipe(final Recipe recipe) {
    this.recipe = recipe == null ? null : new Recipe(recipe);
  }

  /**
   * All-args constructor for RecipeFavorite.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param id the composite ID
   * @param recipe the recipe
   * @param userId the user ID
   * @param favoritedAt the favorited timestamp
   */
  public RecipeFavorite(
      final RecipeFavoriteId id,
      final Recipe recipe,
      final UUID userId,
      final LocalDateTime favoritedAt) {
    this.id = id == null ? null : new RecipeFavoriteId(id);
    this.recipe = recipe == null ? null : new Recipe(recipe);
    this.userId = userId;
    this.favoritedAt = favoritedAt;
  }

  /** Builder for RecipeFavorite. Use to construct instances with clarity and safety. */
  public static class RecipeFavoriteBuilder {
    /**
     * Sets the composite ID.
     *
     * @param id the composite ID
     * @return this builder
     */
    public RecipeFavoriteBuilder id(final RecipeFavoriteId id) {
      this.id = id == null ? null : new RecipeFavoriteId(id);
      return this;
    }

    /**
     * Sets the recipe.
     *
     * @param recipe the recipe
     * @return this builder
     */
    public RecipeFavoriteBuilder recipe(final Recipe recipe) {
      this.recipe = recipe == null ? null : new Recipe(recipe);
      return this;
    }
  }
}
