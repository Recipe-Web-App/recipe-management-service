package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.recipe_manager.model.dto.ingredient.IngredientCommentDto;
import com.recipe_manager.model.entity.ingredient.IngredientComment;

/** MapStruct mapper for converting between IngredientComment entities and DTOs. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IngredientCommentMapper {

  /**
   * Convert IngredientComment entity to DTO.
   *
   * @param entity the entity to convert
   * @return the DTO
   */
  IngredientCommentDto toDto(IngredientComment entity);

  /**
   * Convert list of IngredientComment entities to DTOs.
   *
   * @param entities the entities to convert
   * @return the DTOs
   */
  List<IngredientCommentDto> toDtoList(List<IngredientComment> entities);

  /**
   * Convert IngredientCommentDto to entity.
   *
   * @param dto the DTO to convert
   * @return the entity
   */
  IngredientComment toEntity(IngredientCommentDto dto);
}
