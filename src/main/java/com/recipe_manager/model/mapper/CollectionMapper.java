package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.dto.response.CollectionSummaryResponse;
import com.recipe_manager.repository.collection.CollectionSummaryProjection;

/** MapStruct mapper for converting collection response objects. */
@Mapper(componentModel = "spring")
public interface CollectionMapper {
  /**
   * Converts CollectionSummaryResponse (from database view) to CollectionDto (for API response).
   * Maps ownerId field to userId to match OpenAPI specification.
   *
   * @param summary the collection summary from database view
   * @return the API response DTO
   */
  @Mapping(target = "userId", source = "ownerId")
  CollectionDto toDto(CollectionSummaryResponse summary);

  /**
   * Converts CollectionSummaryProjection (from native query) to CollectionDto (for API response).
   * Maps ownerId field to userId to match OpenAPI specification.
   *
   * @param projection the collection summary projection from native query
   * @return the API response DTO
   */
  @Mapping(target = "userId", source = "ownerId")
  CollectionDto fromProjection(CollectionSummaryProjection projection);
}
