package com.recipe_manager.model.entity.media;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Entity representing media content in the system. Maps to the media table in the database. */
@Entity
@Table(name = "media", schema = "recipe_manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Media {
  /** Max content hash length as defined in DB schema. */
  private static final int MAX_CONTENT_HASH_LENGTH = 64;

  /** The unique ID of the media. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "media_id")
  private Long mediaId;

  /** The user ID of the media owner. */
  @NotNull
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  /** The media type (MIME type). */
  @NotNull
  @Enumerated
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "media_type", nullable = false)
  private MediaType mediaType;

  /** The file system path to the media. */
  @NotBlank
  @Column(name = "media_path", nullable = false, columnDefinition = "text")
  private String mediaPath;

  /** The file size in bytes. */
  @Column(name = "file_size")
  private Long fileSize;

  /** The content hash for integrity checking. */
  @Size(max = MAX_CONTENT_HASH_LENGTH)
  @Column(name = "content_hash", length = MAX_CONTENT_HASH_LENGTH)
  private String contentHash;

  /** The original filename when uploaded. */
  @Column(name = "original_filename", columnDefinition = "text")
  private String originalFilename;

  /** The processing status of the media. */
  @NotNull
  @Enumerated
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "processing_status", nullable = false)
  private ProcessingStatus processingStatus;

  /** The creation timestamp. */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
