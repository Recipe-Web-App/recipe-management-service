package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.media.RecipeMediaDto;
import com.recipe_manager.model.entity.media.RecipeMedia;

/**
 * MapStruct mapper for RecipeMedia entity and related DTOs. Handles the relationship between
 * recipes and media with proper nested mapping.
 */
@Mapper(
    componentModel = "spring",
    uses = {MediaMapper.class})
public interface RecipeMediaMapper {

  /**
   * Maps a RecipeMedia entity to a RecipeMediaDto.
   *
   * @param recipeMedia the RecipeMedia entity
   * @return the mapped RecipeMediaDto
   */
  @Mapping(target = "mediaId", source = "mediaId")
  @Mapping(target = "recipeId", source = "recipeId")
  @Mapping(target = "media", source = "media")
  RecipeMediaDto toDto(RecipeMedia recipeMedia);

  /**
   * Maps a RecipeMediaDto to a RecipeMedia entity.
   *
   * @param recipeMediaDto the RecipeMediaDto
   * @return the mapped RecipeMedia entity
   */
  @Mapping(target = "mediaId", source = "mediaId")
  @Mapping(target = "recipeId", source = "recipeId")
  @Mapping(target = "media", ignore = true) // Managed by relationship, not direct mapping
  @Mapping(target = "recipe", ignore = true) // Managed by relationship, not direct mapping
  RecipeMedia toEntity(RecipeMediaDto recipeMediaDto);
}
