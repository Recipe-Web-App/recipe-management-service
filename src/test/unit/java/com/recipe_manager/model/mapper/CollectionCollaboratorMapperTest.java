package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.dto.collection.CollectionCollaboratorDto;
import com.recipe_manager.model.entity.collection.CollectionCollaborator;
import com.recipe_manager.model.entity.collection.CollectionCollaboratorId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/** Unit tests for CollectionCollaboratorMapper. */
@Tag("unit")
@SpringBootTest(classes = {CollectionCollaboratorMapperImpl.class})
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
class CollectionCollaboratorMapperTest {

  @Autowired private CollectionCollaboratorMapper collectionCollaboratorMapper;

  @Test
  @DisplayName("Should map CollectionCollaborator entity to CollectionCollaboratorDto")
  void shouldMapCollectionCollaboratorEntityToDto() {
    UUID userId = UUID.randomUUID();
    UUID grantedBy = UUID.randomUUID();
    CollectionCollaboratorId id =
        CollectionCollaboratorId.builder().collectionId(1L).userId(userId).build();
    LocalDateTime now = LocalDateTime.now();
    String username = "john_doe";
    String grantedByUsername = "admin_user";

    CollectionCollaborator collaborator =
        CollectionCollaborator.builder().id(id).grantedBy(grantedBy).grantedAt(now).build();

    CollectionCollaboratorDto result =
        collectionCollaboratorMapper.toDto(collaborator, username, grantedByUsername);

    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(1L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getUsername()).isEqualTo("john_doe");
    assertThat(result.getGrantedBy()).isEqualTo(grantedBy);
    assertThat(result.getGrantedByUsername()).isEqualTo("admin_user");
    assertThat(result.getGrantedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should handle null CollectionCollaborator entity")
  void shouldHandleNullCollectionCollaboratorEntity() {
    CollectionCollaboratorDto result =
        collectionCollaboratorMapper.toDto(null, "test_user", "granted_user");
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle null username")
  void shouldHandleNullUsername() {
    UUID userId = UUID.randomUUID();
    UUID grantedBy = UUID.randomUUID();
    CollectionCollaboratorId id =
        CollectionCollaboratorId.builder().collectionId(1L).userId(userId).build();

    CollectionCollaborator collaborator =
        CollectionCollaborator.builder().id(id).grantedBy(grantedBy).build();

    CollectionCollaboratorDto result =
        collectionCollaboratorMapper.toDto(collaborator, null, "granted_user");

    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isNull();
    assertThat(result.getGrantedByUsername()).isEqualTo("granted_user");
  }

  @Test
  @DisplayName("Should map composite ID correctly")
  void shouldMapCompositeIdCorrectly() {
    UUID userId = UUID.randomUUID();
    CollectionCollaboratorId id =
        CollectionCollaboratorId.builder().collectionId(5L).userId(userId).build();

    CollectionCollaborator collaborator =
        CollectionCollaborator.builder().id(id).grantedBy(UUID.randomUUID()).build();

    CollectionCollaboratorDto result =
        collectionCollaboratorMapper.toDto(collaborator, "jane_smith", "owner_user");

    assertThat(result.getCollectionId()).isEqualTo(5L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getUsername()).isEqualTo("jane_smith");
    assertThat(result.getGrantedByUsername()).isEqualTo("owner_user");
  }

  @Test
  @DisplayName("Should handle both null parameters")
  void shouldHandleBothNull() {
    CollectionCollaboratorDto result = collectionCollaboratorMapper.toDto(null, null, null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle null grantedByUsername")
  void shouldHandleNullGrantedByUsername() {
    UUID userId = UUID.randomUUID();
    UUID grantedBy = UUID.randomUUID();
    CollectionCollaboratorId id =
        CollectionCollaboratorId.builder().collectionId(1L).userId(userId).build();

    CollectionCollaborator collaborator =
        CollectionCollaborator.builder().id(id).grantedBy(grantedBy).build();

    CollectionCollaboratorDto result =
        collectionCollaboratorMapper.toDto(collaborator, "test_user", null);

    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo("test_user");
    assertThat(result.getGrantedByUsername()).isNull();
  }
}
