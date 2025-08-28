package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.media.IngredientMediaDto;
import com.recipe_manager.model.entity.media.IngredientMedia;

/**
 * MapStruct mapper for IngredientMedia entity and related DTOs. Handles the relationship between
 * ingredients and media with proper nested mapping.
 */
@Mapper(
    componentModel = "spring",
    uses = {MediaMapper.class})
public interface IngredientMediaMapper {

  /**
   * Maps an IngredientMedia entity to an IngredientMediaDto.
   *
   * @param ingredientMedia the IngredientMedia entity
   * @return the mapped IngredientMediaDto
   */
  @Mapping(target = "mediaId", source = "id.mediaId")
  @Mapping(target = "recipeId", source = "id.recipeId")
  @Mapping(target = "ingredientId", source = "id.ingredientId")
  @Mapping(target = "media", source = "media")
  IngredientMediaDto toDto(IngredientMedia ingredientMedia);

  /**
   * Maps an IngredientMediaDto to an IngredientMedia entity.
   *
   * @param ingredientMediaDto the IngredientMediaDto
   * @return the mapped IngredientMedia entity
   */
  @Mapping(target = "id.mediaId", source = "mediaId")
  @Mapping(target = "id.recipeId", source = "recipeId")
  @Mapping(target = "id.ingredientId", source = "ingredientId")
  @Mapping(target = "media", ignore = true) // Managed by relationship, not direct mapping
  @Mapping(target = "ingredient", ignore = true) // Managed by relationship, not direct mapping
  @Mapping(target = "recipe", ignore = true) // Managed by relationship, not direct mapping
  IngredientMedia toEntity(IngredientMediaDto ingredientMediaDto);
}
