package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.collection.RecipeCollectionItemDto;
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;

/** MapStruct mapper for RecipeCollectionItem entity to DTO conversions. */
@Mapper(componentModel = "spring")
public interface RecipeCollectionItemMapper {
  /**
   * Maps a RecipeCollectionItem entity to a RecipeCollectionItemDto.
   *
   * @param item the RecipeCollectionItem entity
   * @return the mapped RecipeCollectionItemDto
   */
  @Mapping(target = "collectionId", source = "id.collectionId")
  @Mapping(target = "recipeId", source = "id.recipeId")
  RecipeCollectionItemDto toDto(RecipeCollectionItem item);
}
