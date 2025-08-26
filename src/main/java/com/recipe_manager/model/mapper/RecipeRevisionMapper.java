package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.recipe_manager.exception.RevisionSerializationException;
import com.recipe_manager.model.dto.recipe.RecipeRevisionDto;
import com.recipe_manager.model.dto.revision.AbstractRevision;
import com.recipe_manager.model.entity.recipe.RecipeRevision;

/** MapStruct mapper for converting between RecipeRevision entity and RecipeRevisionDto. */
@Mapper(componentModel = "spring")
public interface RecipeRevisionMapper {

  /** ObjectMapper instance configured with JavaTimeModule for JSON processing. */
  ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

  /**
   * Maps a RecipeRevision entity to a RecipeRevisionDto.
   *
   * @param entity the RecipeRevision entity
   * @return the mapped RecipeRevisionDto
   */
  @Mapping(target = "recipeId", source = "recipe.recipeId")
  @Mapping(target = "previousData", source = "previousData", qualifiedByName = "revisionToString")
  @Mapping(target = "newData", source = "newData", qualifiedByName = "revisionToString")
  RecipeRevisionDto toDto(RecipeRevision entity);

  /**
   * Maps a list of RecipeRevision entities to a list of RecipeRevisionDto.
   *
   * @param entities the list of RecipeRevision entities
   * @return the mapped list of RecipeRevisionDto
   */
  List<RecipeRevisionDto> toDtoList(List<RecipeRevision> entities);

  /**
   * Converts an AbstractRevision object to its JSON string representation.
   *
   * @param revision the revision object
   * @return the JSON string representation
   */
  @Named("revisionToString")
  default String revisionToString(AbstractRevision revision) {
    if (revision == null) {
      return null;
    }
    try {
      return OBJECT_MAPPER.writeValueAsString(revision);
    } catch (JsonProcessingException e) {
      throw new RevisionSerializationException("Failed to convert revision to JSON string", e);
    }
  }
}
