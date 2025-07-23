package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.recipe.RecipeRevisionDto;
import com.recipe_manager.model.entity.recipe.RecipeRevision;

/** MapStruct mapper for converting between RecipeRevision entity and RecipeRevisionDto. */
@Mapper(componentModel = "spring")
public interface RecipeRevisionMapper {

  /**
   * Maps a RecipeRevision entity to a RecipeRevisionDto.
   *
   * @param entity the RecipeRevision entity
   * @return the mapped RecipeRevisionDto
   */
  @Mapping(target = "recipeId", source = "recipe.recipeId")
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "media", ignore = true)
  RecipeRevisionDto toDto(RecipeRevision entity);

  /**
   * Maps a list of RecipeRevision entities to a list of RecipeRevisionDto.
   *
   * @param entities the list of RecipeRevision entities
   * @return the mapped list of RecipeRevisionDto
   */
  List<RecipeRevisionDto> toDtoList(List<RecipeRevision> entities);
}
