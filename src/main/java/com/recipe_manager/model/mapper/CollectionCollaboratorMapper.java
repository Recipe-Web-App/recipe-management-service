package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.collection.CollectionCollaboratorDto;
import com.recipe_manager.model.entity.collection.CollectionCollaborator;

/** MapStruct mapper for CollectionCollaborator entity to DTO conversions. */
@Mapper(componentModel = "spring")
public interface CollectionCollaboratorMapper {
  /**
   * Maps a CollectionCollaborator entity to a CollectionCollaboratorDto.
   *
   * @param collaborator the CollectionCollaborator entity
   * @param username the username of the collaborator
   * @return the mapped CollectionCollaboratorDto, or null if collaborator is null
   */
  @Mapping(target = "collectionId", source = "collaborator.id.collectionId")
  @Mapping(target = "userId", source = "collaborator.id.userId")
  @Mapping(target = "username", source = "username")
  @Mapping(target = "grantedBy", source = "collaborator.grantedBy")
  @Mapping(target = "grantedAt", source = "collaborator.grantedAt")
  default CollectionCollaboratorDto toDto(CollectionCollaborator collaborator, String username) {
    if (collaborator == null) {
      return null;
    }
    return mapToDto(collaborator, username);
  }

  /**
   * Internal mapping method used by the default toDto method.
   *
   * @param collaborator the CollectionCollaborator entity
   * @param username the username of the collaborator
   * @return the mapped CollectionCollaboratorDto
   */
  @Mapping(target = "collectionId", source = "collaborator.id.collectionId")
  @Mapping(target = "userId", source = "collaborator.id.userId")
  @Mapping(target = "username", source = "username")
  @Mapping(target = "grantedBy", source = "collaborator.grantedBy")
  @Mapping(target = "grantedAt", source = "collaborator.grantedAt")
  CollectionCollaboratorDto mapToDto(CollectionCollaborator collaborator, String username);
}
