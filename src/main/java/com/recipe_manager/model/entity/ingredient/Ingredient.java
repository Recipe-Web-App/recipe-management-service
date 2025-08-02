package com.recipe_manager.model.entity.ingredient;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.recipe_manager.model.entity.recipe.RecipeIngredient;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing an ingredient in the system. Maps to the ingredients table in the database.
 */
@Entity
@Table(name = "ingredients", schema = "recipe_manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "recipeIngredients")
@ToString(exclude = "recipeIngredients")
public class Ingredient {
  /** Max length allowed by database schema. */
  private static final int MAX_INGREDIENT_NAME_LENGTH = 255;

  /** The unique ID of the ingredient. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ingredient_id")
  private Long ingredientId;

  /** The name of the ingredient. */
  @NotBlank
  @Size(max = MAX_INGREDIENT_NAME_LENGTH)
  @Column(name = "name", nullable = false)
  private String name;

  /** The description of the ingredient. */
  @Column(name = "description", columnDefinition = "text")
  private String description;

  /** Whether the ingredient is optional. */
  @Column(name = "is_optional")
  @Builder.Default
  private Boolean isOptional = false;

  /** The creation timestamp. */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /** The list of recipe ingredients that use this ingredient. */
  @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RecipeIngredient> recipeIngredients;
}
