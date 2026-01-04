package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.recipe_manager.model.dto.collection.CollectionTagDto;
import com.recipe_manager.model.entity.collection.CollectionTag;

/** MapStruct mapper for converting between CollectionTag entity and CollectionTagDto. */
@Mapper(componentModel = "spring")
public interface CollectionTagMapper {

  /**
   * Maps a CollectionTag entity to a CollectionTagDto.
   *
   * @param entity the CollectionTag entity
   * @return the mapped CollectionTagDto
   */
  CollectionTagDto toDto(CollectionTag entity);

  /**
   * Maps a list of CollectionTag entities to a list of CollectionTagDto.
   *
   * @param entities the list of CollectionTag entities
   * @return the mapped list of CollectionTagDto
   */
  List<CollectionTagDto> toDtoList(List<CollectionTag> entities);
}
