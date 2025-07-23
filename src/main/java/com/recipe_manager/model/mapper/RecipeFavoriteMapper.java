package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.recipe.RecipeFavoriteDto;
import com.recipe_manager.model.entity.recipe.RecipeFavorite;

/** MapStruct mapper for converting between RecipeFavorite entity and RecipeFavoriteDto. */
@Mapper(componentModel = "spring")
public interface RecipeFavoriteMapper {

  /**
   * Maps a RecipeFavorite entity to a RecipeFavoriteDto.
   *
   * @param entity the RecipeFavorite entity
   * @return the mapped RecipeFavoriteDto
   */
  @Mapping(target = "recipeId", source = "recipe.recipeId")
  @Mapping(target = "userId", source = "id.userId")
  @Mapping(target = "favoritedAt", source = "favoritedAt")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "media", ignore = true)
  RecipeFavoriteDto toDto(RecipeFavorite entity);

  /**
   * Maps a list of RecipeFavorite entities to a list of RecipeFavoriteDto.
   *
   * @param entities the list of RecipeFavorite entities
   * @return the mapped list of RecipeFavoriteDto
   */
  List<RecipeFavoriteDto> toDtoList(List<RecipeFavorite> entities);
}
