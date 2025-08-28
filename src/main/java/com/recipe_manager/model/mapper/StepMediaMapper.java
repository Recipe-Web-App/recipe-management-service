package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.media.StepMediaDto;
import com.recipe_manager.model.entity.media.StepMedia;

/**
 * MapStruct mapper for StepMedia entity and related DTOs. Handles the relationship between steps
 * and media with proper nested mapping.
 */
@Mapper(
    componentModel = "spring",
    uses = {MediaMapper.class})
public interface StepMediaMapper {

  /**
   * Maps a StepMedia entity to a StepMediaDto.
   *
   * @param stepMedia the StepMedia entity
   * @return the mapped StepMediaDto
   */
  @Mapping(target = "mediaId", source = "id.mediaId")
  @Mapping(target = "stepId", source = "id.stepId")
  @Mapping(target = "media", source = "media")
  StepMediaDto toDto(StepMedia stepMedia);

  /**
   * Maps a StepMediaDto to a StepMedia entity.
   *
   * @param stepMediaDto the StepMediaDto
   * @return the mapped StepMedia entity
   */
  @Mapping(target = "id.mediaId", source = "mediaId")
  @Mapping(target = "id.stepId", source = "stepId")
  @Mapping(target = "media", ignore = true) // Managed by relationship, not direct mapping
  @Mapping(target = "step", ignore = true) // Managed by relationship, not direct mapping
  @Mapping(target = "recipe", ignore = true) // Managed by relationship, not direct mapping
  StepMedia toEntity(StepMediaDto stepMediaDto);
}
