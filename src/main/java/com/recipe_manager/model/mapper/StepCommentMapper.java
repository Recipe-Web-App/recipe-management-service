package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.recipe_manager.model.dto.recipe.StepCommentDto;
import com.recipe_manager.model.entity.recipe.StepComment;

/** MapStruct mapper for converting between StepComment entities and DTOs. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StepCommentMapper {

  /**
   * Convert StepComment entity to DTO.
   *
   * @param entity the entity to convert
   * @return the DTO
   */
  @Mapping(source = "step.stepId", target = "stepId")
  StepCommentDto toDto(StepComment entity);

  /**
   * Convert list of StepComment entities to DTOs.
   *
   * @param entities the entities to convert
   * @return the DTOs
   */
  List<StepCommentDto> toDtoList(List<StepComment> entities);

  /**
   * Convert StepCommentDto to entity.
   *
   * @param dto the DTO to convert
   * @return the entity
   */
  @Mapping(target = "step", ignore = true)
  StepComment toEntity(StepCommentDto dto);
}
