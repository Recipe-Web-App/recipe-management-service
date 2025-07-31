package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.entity.recipe.RecipeStep;

/** MapStruct mapper for converting between CreateRecipeStepRequest and RecipeStep. */
@Mapper(componentModel = "spring")
public interface RecipeStepMapper {
  /**
   * Maps a CreateRecipeStepRequest to a RecipeStep. All fields are mapped directly (Lombok
   * DTO/entity).
   *
   * @param request the CreateRecipeStepRequest to map
   * @return the mapped RecipeStep
   */
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "recipe", ignore = true)
  @Mapping(target = "stepId", ignore = true)
  RecipeStep toEntity(RecipeStepDto request);

  /**
   * Maps a list of CreateRecipeStepRequest to a list of RecipeStep.
   *
   * @param requests the list of CreateRecipeStepRequest to map
   * @return the mapped list of RecipeStep
   */
  List<RecipeStep> toEntityList(List<RecipeStepDto> requests);

  /**
   * Maps a RecipeStep entity to a RecipeStepDto.
   *
   * @param entity the RecipeStep entity
   * @return the mapped RecipeStepDto
   */
  @Mapping(target = "recipeId", source = "recipe.recipeId")
  RecipeStepDto toDto(RecipeStep entity);

  /**
   * Maps a list of RecipeStep entities to a list of RecipeStepDto.
   *
   * @param entities the list of RecipeStep entities
   * @return the mapped list of RecipeStepDto
   */
  List<RecipeStepDto> toDtoList(List<RecipeStep> entities);
}
