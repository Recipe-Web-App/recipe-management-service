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
  @Mapping(target = "media", ignore = true)
  @Mapping(target = "notes", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  RecipeIngredientDto toDto(RecipeIngredient entity);

  /**
   * Maps a list of RecipeIngredient entities to a list of RecipeIngredientDto.
   *
   * @param entities the list of RecipeIngredient entities
   * @return the mapped list of RecipeIngredientDto
   */
  List<RecipeIngredientDto> toDtoList(List<RecipeIngredient> entities);
}
