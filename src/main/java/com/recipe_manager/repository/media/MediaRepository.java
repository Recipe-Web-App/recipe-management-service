package com.recipe_manager.repository.media;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

/** Repository interface for Media entity. Provides data access methods for media operations. */
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

  /**
   * Find all media belonging to a specific user.
   *
   * @param userId the user ID
   * @return list of media owned by the user
   */
  List<Media> findByUserId(UUID userId);

  /**
   * Find all media of a specific type.
   *
   * @param mediaType the media type
   * @return list of media with the specified type
   */
  List<Media> findByMediaType(MediaType mediaType);

  /**
   * Find all media with a specific processing status.
   *
   * @param processingStatus the processing status
   * @return list of media with the specified processing status
   */
  List<Media> findByProcessingStatus(ProcessingStatus processingStatus);

  /**
   * Find all media belonging to a specific user with a specific media type.
   *
   * @param userId the user ID
   * @param mediaType the media type
   * @return list of user's media with the specified type
   */
  List<Media> findByUserIdAndMediaType(UUID userId, MediaType mediaType);

  /**
   * Find all media belonging to a specific user with a specific processing status.
   *
   * @param userId the user ID
   * @param processingStatus the processing status
   * @return list of user's media with the specified processing status
   */
  List<Media> findByUserIdAndProcessingStatus(UUID userId, ProcessingStatus processingStatus);

  /**
   * Count the number of media items belonging to a specific user.
   *
   * @param userId the user ID
   * @return the count of media items owned by the user
   */
  long countByUserId(UUID userId);

  /**
   * Check if media exists for a specific user.
   *
   * @param userId the user ID
   * @return true if the user has any media, false otherwise
   */
  boolean existsByUserId(UUID userId);
}
