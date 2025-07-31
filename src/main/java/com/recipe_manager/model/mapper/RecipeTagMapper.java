package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.recipe_manager.model.dto.recipe.RecipeTagDto;
import com.recipe_manager.model.entity.recipe.RecipeTag;

/** MapStruct mapper for converting between RecipeTag entity and RecipeTagDto. */
@Mapper(componentModel = "spring")
public interface RecipeTagMapper {

  /**
   * Maps a RecipeTag entity to a RecipeTagDto.
   *
   * @param entity the RecipeTag entity
   * @return the mapped RecipeTagDto
   */
  RecipeTagDto toDto(RecipeTag entity);

  /**
   * Maps a list of RecipeTag entities to a list of RecipeTagDto.
   *
   * @param entities the list of RecipeTag entities
   * @return the mapped list of RecipeTagDto
   */
  List<RecipeTagDto> toDtoList(List<RecipeTag> entities);
}
