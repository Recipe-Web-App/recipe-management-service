package com.recipe_manager.model.dto.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.recipe_manager.model.enums.IngredientUnit;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Request DTO for creating a recipe ingredient. Contains ingredient data for recipe creation.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public final class CreateRecipeIngredientRequest {

  /** The ingredient name. */
  private String ingredientName;

  /** The ingredient ID. */
  private Long ingredientId;

  /** The quantity of the ingredient. */
  private BigDecimal quantity;

  /** The unit of measurement. */
  private IngredientUnit unit;

  /** Whether the ingredient is optional. */
  private Boolean isOptional;

  /** Notes about the ingredient. */
  private String notes;

  /** The list of media URLs for this ingredient. */
  @Valid private List<String> mediaUrls;

  /**
   * Builder for CreateRecipeIngredientRequest. Use to construct instances with clarity and safety.
   */
  public static class CreateRecipeIngredientRequestBuilder {
    /**
     * Sets the media URLs list.
     *
     * @param mediaUrls the list of media URLs
     * @return this builder
     */
    public CreateRecipeIngredientRequestBuilder mediaUrls(final List<String> mediaUrls) {
      this.mediaUrls = mediaUrls == null ? new ArrayList<>() : new ArrayList<>(mediaUrls);
      return this;
    }
  }

  /**
   * Returns an unmodifiable list of media URLs.
   *
   * @return the media URLs list
   */
  public List<String> getMediaUrls() {
    return Collections.unmodifiableList(mediaUrls);
  }

  /**
   * Sets the media URLs list.
   *
   * @param mediaUrls the list of media URLs
   */
  public void setMediaUrls(final List<String> mediaUrls) {
    this.mediaUrls = mediaUrls != null ? new ArrayList<>(mediaUrls) : new ArrayList<>();
  }

  /**
   * All-args constructor with defensive copying for mutable fields.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param ingredientName the ingredient name
   * @param ingredientId the ingredient ID
   * @param quantity the quantity
   * @param unit the unit
   * @param isOptional whether the ingredient is optional
   * @param notes notes about the ingredient
   * @param mediaUrls the media URLs list
   */
  public CreateRecipeIngredientRequest(
      final String ingredientName,
      final Long ingredientId,
      final BigDecimal quantity,
      final IngredientUnit unit,
      final Boolean isOptional,
      final String notes,
      final List<String> mediaUrls) {
    this.ingredientName = ingredientName;
    this.ingredientId = ingredientId;
    this.quantity = quantity;
    this.unit = unit;
    this.isOptional = isOptional;
    this.notes = notes;
    this.mediaUrls = mediaUrls != null ? new ArrayList<>(mediaUrls) : new ArrayList<>();
  }

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
    final CreateRecipeIngredientRequest that = (CreateRecipeIngredientRequest) obj;
    return Objects.equals(ingredientName, that.ingredientName)
        && Objects.equals(ingredientId, that.ingredientId)
        && Objects.equals(quantity, that.quantity)
        && unit == that.unit
        && Objects.equals(isOptional, that.isOptional)
        && Objects.equals(notes, that.notes)
        && Objects.equals(mediaUrls, that.mediaUrls);
  }

  /**
   * Computes hash code based on all fields.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(ingredientName, ingredientId, quantity, unit, isOptional, notes, mediaUrls);
  }
}
