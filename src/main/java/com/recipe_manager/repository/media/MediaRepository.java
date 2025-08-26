package com.recipe_manager.repository.media;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

/** Repository interface for Media entity. Provides data access methods for media operations. */
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

  /**
   * Find all media owned by a specific user.
   *
   * @param userId the user ID
   * @return list of media owned by the user
   */
  List<Media> findByUserId(UUID userId);

  /**
   * Find all media by type.
   *
   * @param mediaType the media type
   * @return list of media of the specified type
   */
  List<Media> findByMediaType(MediaType mediaType);

  /**
   * Find all media by processing status.
   *
   * @param processingStatus the processing status
   * @return list of media with the specified processing status
   */
  List<Media> findByProcessingStatus(ProcessingStatus processingStatus);

  /**
   * Find all media by user and type.
   *
   * @param userId the user ID
   * @param mediaType the media type
   * @return list of media owned by the user of the specified type
   */
  List<Media> findByUserIdAndMediaType(UUID userId, MediaType mediaType);

  /**
   * Find all media by user and processing status.
   *
   * @param userId the user ID
   * @param processingStatus the processing status
   * @return list of media owned by the user with the specified processing status
   */
  List<Media> findByUserIdAndProcessingStatus(UUID userId, ProcessingStatus processingStatus);

  /**
   * Find media by content hash for duplicate detection.
   *
   * @param contentHash the content hash
   * @return list of media with the same content hash
   */
  List<Media> findByContentHash(String contentHash);

  /**
   * Check if media exists with the given content hash.
   *
   * @param contentHash the content hash
   * @return true if media exists with the hash, false otherwise
   */
  boolean existsByContentHash(String contentHash);

  /**
   * Count media by user ID.
   *
   * @param userId the user ID
   * @return number of media items owned by the user
   */
  long countByUserId(UUID userId);

  /**
   * Count media by processing status.
   *
   * @param processingStatus the processing status
   * @return number of media items with the specified processing status
   */
  long countByProcessingStatus(ProcessingStatus processingStatus);

  /**
   * Find all media that are currently being processed or have failed processing.
   *
   * @return list of media that need attention
   */
  @Query(
      "SELECT m FROM Media m WHERE m.processingStatus IN ('PROCESSING', 'FAILED') ORDER BY m.createdAt ASC")
  List<Media> findMediaNeedingAttention();

  /**
   * Delete all media owned by a specific user.
   *
   * @param userId the user ID
   */
  void deleteByUserId(UUID userId);
}
