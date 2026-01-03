package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.collection.CollectionFavoriteDto;
import com.recipe_manager.model.entity.collection.CollectionFavorite;

/** MapStruct mapper for converting between CollectionFavorite entity and CollectionFavoriteDto. */
@Mapper(componentModel = "spring")
public interface CollectionFavoriteMapper {

  /**
   * Maps a CollectionFavorite entity to a CollectionFavoriteDto.
   *
   * @param entity the CollectionFavorite entity
   * @return the mapped CollectionFavoriteDto
   */
  @Mapping(target = "collectionId", source = "collection.collectionId")
  @Mapping(target = "userId", source = "id.userId")
  @Mapping(target = "favoritedAt", source = "favoritedAt")
  CollectionFavoriteDto toDto(CollectionFavorite entity);

  /**
   * Maps a list of CollectionFavorite entities to a list of CollectionFavoriteDto.
   *
   * @param entities the list of CollectionFavorite entities
   * @return the mapped list of CollectionFavoriteDto
   */
  List<CollectionFavoriteDto> toDtoList(List<CollectionFavorite> entities);
}
