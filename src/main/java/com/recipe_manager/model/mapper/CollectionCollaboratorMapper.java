package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.collection.CollectionCollaboratorDto;
import com.recipe_manager.model.entity.collection.CollectionCollaborator;

/** MapStruct mapper for CollectionCollaborator entity to DTO conversions. */
@Mapper(componentModel = "spring")
public interface CollectionCollaboratorMapper {
  /**
   * Maps a CollectionCollaborator entity to a CollectionCollaboratorDto without username lookup.
   * Used for automatic collection mapping by MapStruct. Username fields will be null.
   *
   * @param collaborator the CollectionCollaborator entity
   * @return the mapped CollectionCollaboratorDto with null usernames, or null if collaborator is
   *     null
   */
  @Mapping(target = "collectionId", source = "id.collectionId")
  @Mapping(target = "userId", source = "id.userId")
  @Mapping(target = "username", ignore = true)
  @Mapping(target = "grantedByUsername", ignore = true)
  CollectionCollaboratorDto toDto(CollectionCollaborator collaborator);

  /**
   * Maps a CollectionCollaborator entity to a CollectionCollaboratorDto with usernames.
   *
   * @param collaborator the CollectionCollaborator entity
   * @param username the username of the collaborator
   * @param grantedByUsername the username of the user who granted access
   * @return the mapped CollectionCollaboratorDto, or null if collaborator is null
   */
  @Mapping(target = "collectionId", source = "collaborator.id.collectionId")
  @Mapping(target = "userId", source = "collaborator.id.userId")
  @Mapping(target = "username", source = "username")
  @Mapping(target = "grantedBy", source = "collaborator.grantedBy")
  @Mapping(target = "grantedByUsername", source = "grantedByUsername")
  @Mapping(target = "grantedAt", source = "collaborator.grantedAt")
  default CollectionCollaboratorDto toDtoWithUsernames(
      CollectionCollaborator collaborator, String username, String grantedByUsername) {
    if (collaborator == null) {
      return null;
    }
    return mapToDtoWithUsernames(collaborator, username, grantedByUsername);
  }

  /**
   * Internal mapping method used by the toDtoWithUsernames method.
   *
   * @param collaborator the CollectionCollaborator entity
   * @param username the username of the collaborator
   * @param grantedByUsername the username of the user who granted access
   * @return the mapped CollectionCollaboratorDto
   */
  @Mapping(target = "collectionId", source = "collaborator.id.collectionId")
  @Mapping(target = "userId", source = "collaborator.id.userId")
  @Mapping(target = "username", source = "username")
  @Mapping(target = "grantedBy", source = "collaborator.grantedBy")
  @Mapping(target = "grantedByUsername", source = "grantedByUsername")
  @Mapping(target = "grantedAt", source = "collaborator.grantedAt")
  CollectionCollaboratorDto mapToDtoWithUsernames(
      CollectionCollaborator collaborator, String username, String grantedByUsername);
}
