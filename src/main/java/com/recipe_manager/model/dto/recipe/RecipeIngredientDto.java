package com.recipe_manager.model.dto.recipe;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.recipe_manager.model.dto.media.RecipeIngredientMediaDto;
import com.recipe_manager.model.enums.IngredientUnit;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for RecipeIngredient entity. Used for transferring recipe ingredient data
 * between layers.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public final class RecipeIngredientDto {

  /** The recipe ID. */
  private Long recipeId;

  /** The ingredient ID. */
  private Long ingredientId;

  /** The ingredient name. */
  private String ingredientName;

  /** The quantity of the ingredient. */
  private BigDecimal quantity;

  /** The unit of measurement. */
  private IngredientUnit unit;

  /** Whether the ingredient is optional. */
  private Boolean isOptional;

  /** Notes about the ingredient. */
  private String notes;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /** The list of media associated with this ingredient. */
  @Valid private List<RecipeIngredientMediaDto> media;

  /** Builder for RecipeIngredientDto. Use to construct instances with clarity and safety. */
  public static final class RecipeIngredientDtoBuilder {
    /**
     * Sets the media list.
     *
     * @param media the list of media
     * @return this builder
     */
    public RecipeIngredientDtoBuilder media(final List<RecipeIngredientMediaDto> media) {
      this.media = media == null ? new ArrayList<>() : new ArrayList<>(media);
      return this;
    }
  }

  /**
   * Returns an unmodifiable list of media.
   *
   * @return the media list
   */
  public List<RecipeIngredientMediaDto> getMedia() {
    return Collections.unmodifiableList(media);
  }

  /**
   * Sets the media list.
   *
   * @param media the list of media
   */
  public void setMedia(final List<RecipeIngredientMediaDto> media) {
    this.media = media != null ? new ArrayList<>(media) : new ArrayList<>();
  }

  /**
   * All-args constructor with defensive copying for mutable fields.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @param ingredientName the ingredient name
   * @param quantity the quantity
   * @param unit the unit
   * @param isOptional whether the ingredient is optional
   * @param notes notes about the ingredient
   * @param createdAt the creation timestamp
   * @param updatedAt the update timestamp
   * @param media the media list
   */
  // CHECKSTYLE:OFF: ParameterNumber
  public RecipeIngredientDto(
      final Long recipeId,
      final Long ingredientId,
      final String ingredientName,
      final BigDecimal quantity,
      final IngredientUnit unit,
      final Boolean isOptional,
      final String notes,
      final LocalDateTime createdAt,
      final LocalDateTime updatedAt,
      final List<RecipeIngredientMediaDto> media) {
    this.recipeId = recipeId;
    this.ingredientId = ingredientId;
    this.ingredientName = ingredientName;
    this.quantity = quantity;
    this.unit = unit;
    this.isOptional = isOptional;
    this.notes = notes;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.media = media != null ? new ArrayList<>(media) : new ArrayList<>();
  }

  // CHECKSTYLE:ON: ParameterNumber

  /**
   * Checks equality based on all fields.
   *
   * @param obj the object to compare
   * @return true if equal, false otherwise
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final RecipeIngredientDto that = (RecipeIngredientDto) obj;
    return Objects.equals(recipeId, that.recipeId)
        && Objects.equals(ingredientId, that.ingredientId)
        && Objects.equals(ingredientName, that.ingredientName)
        && Objects.equals(quantity, that.quantity)
        && unit == that.unit
        && Objects.equals(isOptional, that.isOptional)
        && Objects.equals(notes, that.notes)
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(updatedAt, that.updatedAt)
        && Objects.equals(media, that.media);
  }

  /**
   * Computes hash code based on all fields.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(
        recipeId,
        ingredientId,
        ingredientName,
        quantity,
        unit,
        isOptional,
        notes,
        createdAt,
        updatedAt,
        media);
  }
}
