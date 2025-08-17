package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;

/** MapStruct mapper for converting between CreateRecipeIngredientRequest and RecipeIngredient. */
@Mapper(componentModel = "spring")
public interface RecipeIngredientMapper {
  /**
   * Maps a CreateRecipeIngredientRequest to a RecipeIngredient. All fields are mapped directly
   * (Lombok DTO/entity).
   *
   * @param request the ingredient request DTO
   * @return the mapped RecipeIngredient
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "ingredient", ignore = true)
  @Mapping(target = "recipe", ignore = true)
  RecipeIngredient toEntity(RecipeIngredientDto request);

  /**
   * Maps a list of CreateRecipeIngredientRequest to a list of RecipeIngredient.
   *
   * @param requests the list of ingredient request DTOs
   * @return the mapped list of RecipeIngredient
   */
  List<RecipeIngredient> toEntityList(List<RecipeIngredientDto> requests);

  /**
   * Maps a RecipeIngredient entity to a RecipeIngredientDto.
   *
   * @param entity the RecipeIngredient entity
   * @return the mapped RecipeIngredientDto
   */
  @Mapping(target = "ingredientId", source = "ingredient.ingredientId")
  @Mapping(target = "ingredientName", source = "ingredient.name")
  @Mapping(target = "recipeId", source = "recipe.recipeId")
  RecipeIngredientDto toDto(RecipeIngredient entity);

  /**
   * Maps a list of RecipeIngredient entities to a list of RecipeIngredientDto.
   *
   * @param entities the list of RecipeIngredient entities
   * @return the mapped list of RecipeIngredientDto
   */
  List<RecipeIngredientDto> toDtoList(List<RecipeIngredient> entities);

  /**
   * Maps a RecipeIngredient entity to a RecipeIngredientDto with scaled quantity.
   *
   * @param entity the RecipeIngredient entity
   * @param scaleFactor the factor to scale the quantity by
   * @return the mapped RecipeIngredientDto with scaled quantity
   */
  @Mapping(target = "ingredientId", expression = "java(entity.getIngredient().getIngredientId())")
  @Mapping(target = "ingredientName", expression = "java(entity.getIngredient().getName())")
  @Mapping(target = "recipeId", expression = "java(entity.getRecipe().getRecipeId())")
  @Mapping(
      target = "quantity",
      expression = "java(entity.getQuantity().multiply(java.math.BigDecimal.valueOf(scaleFactor)))")
  @Mapping(target = "unit", expression = "java(entity.getUnit())")
  @Mapping(target = "isOptional", expression = "java(entity.getIsOptional())")
  RecipeIngredientDto toDtoWithScale(RecipeIngredient entity, float scaleFactor);

  /**
   * Maps a list of RecipeIngredient entities to a list of RecipeIngredientDto with scaled
   * quantities.
   *
   * @param entities the list of RecipeIngredient entities
   * @param scaleFactor the factor to scale the quantities by
   * @return the mapped list of RecipeIngredientDto with scaled quantities
   */
  default List<RecipeIngredientDto> toDtoListWithScale(
      List<RecipeIngredient> entities, float scaleFactor) {
    return entities.stream().map(entity -> toDtoWithScale(entity, scaleFactor)).toList();
  }
}
