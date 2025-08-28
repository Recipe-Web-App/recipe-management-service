package com.recipe_manager.unit_tests.model.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.recipe_manager.model.dto.media.MediaDto;
import com.recipe_manager.model.dto.media.MediaSummaryDto;
import com.recipe_manager.model.dto.request.CreateMediaRequest;
import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;
import com.recipe_manager.model.mapper.MediaMapper;

@SpringBootTest(classes = {com.recipe_manager.model.mapper.MediaMapperImpl.class})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("unit")
class MediaMapperTest {

  @Autowired
  private MediaMapper mediaMapper;

  private Media testMedia;
  private MediaDto testMediaDto;
  private CreateMediaRequest testCreateRequest;
  private UUID testUserId;
  private LocalDateTime testDateTime;

  @BeforeEach
  void setUp() {
    testUserId = UUID.randomUUID();
    testDateTime = LocalDateTime.now();

    testMedia = Media.builder()
        .mediaId(1L)
        .userId(testUserId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/uploads/test-image.jpg")
        .originalFilename("test-image.jpg")
        .fileSize(1024L)
        .contentHash("abc123")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(testDateTime)
        .updatedAt(testDateTime)
        .build();

    testMediaDto = MediaDto.builder()
        .mediaId(1L)
        .userId(testUserId)
        .mediaType(MediaType.IMAGE_JPEG)
        .originalFilename("test-image.jpg")
        .fileSize(1024L)
        .contentHash("abc123")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(testDateTime)
        .updatedAt(testDateTime)
        .build();

    testCreateRequest = CreateMediaRequest.builder()
        .userId(testUserId)
        .originalFilename("test-image.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(1024L)
        .contentHash("abc123")
        .build();
  }

  @Test
  void shouldMapMediaToDto() {
    MediaDto result = mediaMapper.toDto(testMedia);

    assertNotNull(result);
    assertAll("Media to DTO mapping",
        () -> assertEquals(testMedia.getMediaId(), result.getMediaId()),
        () -> assertEquals(testMedia.getUserId(), result.getUserId()),
        () -> assertEquals(testMedia.getMediaType(), result.getMediaType()),
        () -> assertEquals(testMedia.getOriginalFilename(), result.getOriginalFilename()),
        () -> assertEquals(testMedia.getFileSize(), result.getFileSize()),
        () -> assertEquals(testMedia.getContentHash(), result.getContentHash()),
        () -> assertEquals(testMedia.getProcessingStatus(), result.getProcessingStatus()),
        () -> assertEquals(testMedia.getCreatedAt(), result.getCreatedAt()),
        () -> assertEquals(testMedia.getUpdatedAt(), result.getUpdatedAt())
    );
  }

  @Test
  void shouldMapDtoToMedia() {
    Media result = mediaMapper.toEntity(testMediaDto);

    assertNotNull(result);
    assertAll("DTO to Media mapping",
        () -> assertEquals(testMediaDto.getMediaId(), result.getMediaId()),
        () -> assertEquals(testMediaDto.getUserId(), result.getUserId()),
        () -> assertEquals(testMediaDto.getMediaType(), result.getMediaType()),
        () -> assertEquals(testMediaDto.getOriginalFilename(), result.getOriginalFilename()),
        () -> assertEquals(testMediaDto.getFileSize(), result.getFileSize()),
        () -> assertEquals(testMediaDto.getContentHash(), result.getContentHash()),
        () -> assertEquals(testMediaDto.getProcessingStatus(), result.getProcessingStatus()),
        () -> assertEquals(testMediaDto.getCreatedAt(), result.getCreatedAt()),
        () -> assertEquals(testMediaDto.getUpdatedAt(), result.getUpdatedAt()),
        () -> assertNull(result.getMediaPath()) // Should be ignored for security
    );
  }

  @Test
  void shouldMapMediaToSummaryDto() {
    MediaSummaryDto result = mediaMapper.toSummaryDto(testMedia);

    assertNotNull(result);
    assertAll("Media to SummaryDTO mapping",
        () -> assertEquals(testMedia.getMediaId(), result.getMediaId()),
        () -> assertEquals(testMedia.getOriginalFilename(), result.getOriginalFilename()),
        () -> assertEquals(testMedia.getMediaType(), result.getMediaType()),
        () -> assertEquals(testMedia.getFileSize(), result.getFileSize()),
        () -> assertEquals(testMedia.getProcessingStatus(), result.getProcessingStatus())
    );
  }

  @Test
  void shouldMapCreateRequestToMedia() {
    Media result = mediaMapper.toEntity(testCreateRequest);

    assertNotNull(result);
    assertAll("CreateRequest to Media mapping",
        () -> assertNull(result.getMediaId()), // Should be ignored (generated by DB)
        () -> assertEquals(testCreateRequest.getUserId(), result.getUserId()),
        () -> assertEquals(testCreateRequest.getMediaType(), result.getMediaType()),
        () -> assertEquals(testCreateRequest.getOriginalFilename(), result.getOriginalFilename()),
        () -> assertEquals(testCreateRequest.getFileSize(), result.getFileSize()),
        () -> assertEquals(testCreateRequest.getContentHash(), result.getContentHash()),
        () -> assertEquals(ProcessingStatus.INITIATED, result.getProcessingStatus()), // Constant
        () -> assertNull(result.getMediaPath()), // Should be ignored (set by service)
        () -> assertNull(result.getCreatedAt()), // Should be ignored (set by Hibernate)
        () -> assertNull(result.getUpdatedAt()) // Should be ignored (set by Hibernate)
    );
  }

  @Test
  void shouldHandleNullInput() {
    assertAll("Null input handling",
        () -> assertNull(mediaMapper.toDto(null)),
        () -> assertNull(mediaMapper.toEntity((MediaDto) null)),
        () -> assertNull(mediaMapper.toSummaryDto(null)),
        () -> assertNull(mediaMapper.toEntity((CreateMediaRequest) null))
    );
  }

  @Test
  void shouldMapAllMediaTypes() {
    for (MediaType mediaType : MediaType.values()) {
      Media media = Media.builder()
          .mediaId(testMedia.getMediaId())
          .userId(testMedia.getUserId())
          .mediaType(mediaType)
          .mediaPath(testMedia.getMediaPath())
          .originalFilename(testMedia.getOriginalFilename())
          .fileSize(testMedia.getFileSize())
          .contentHash(testMedia.getContentHash())
          .processingStatus(testMedia.getProcessingStatus())
          .createdAt(testMedia.getCreatedAt())
          .updatedAt(testMedia.getUpdatedAt())
          .build();
      MediaDto dto = mediaMapper.toDto(media);

      assertEquals(mediaType, dto.getMediaType());
    }
  }

  @Test
  void shouldMapAllProcessingStatuses() {
    for (ProcessingStatus status : ProcessingStatus.values()) {
      Media media = Media.builder()
          .mediaId(testMedia.getMediaId())
          .userId(testMedia.getUserId())
          .mediaType(testMedia.getMediaType())
          .mediaPath(testMedia.getMediaPath())
          .originalFilename(testMedia.getOriginalFilename())
          .fileSize(testMedia.getFileSize())
          .contentHash(testMedia.getContentHash())
          .processingStatus(status)
          .createdAt(testMedia.getCreatedAt())
          .updatedAt(testMedia.getUpdatedAt())
          .build();
      MediaDto dto = mediaMapper.toDto(media);

      assertEquals(status, dto.getProcessingStatus());
    }
  }

  @Test
  void shouldIgnoreMediaPathInDtoToEntityMapping() {
    MediaDto dtoWithPath = MediaDto.builder()
        .mediaId(testMediaDto.getMediaId())
        .userId(testMediaDto.getUserId())
        .mediaType(testMediaDto.getMediaType())
        .originalFilename(testMediaDto.getOriginalFilename())
        .fileSize(testMediaDto.getFileSize())
        .contentHash(testMediaDto.getContentHash())
        .processingStatus(testMediaDto.getProcessingStatus())
        .createdAt(testMediaDto.getCreatedAt())
        .updatedAt(testMediaDto.getUpdatedAt())
        .build();
    Media result = mediaMapper.toEntity(dtoWithPath);

    assertNull(result.getMediaPath());
  }

  @Test
  void shouldSetInitiatedStatusForCreateRequest() {
    CreateMediaRequest request = CreateMediaRequest.builder()
        .userId(testCreateRequest.getUserId())
        .originalFilename(testCreateRequest.getOriginalFilename())
        .mediaType(testCreateRequest.getMediaType())
        .fileSize(testCreateRequest.getFileSize())
        .contentHash(testCreateRequest.getContentHash())
        .build();
    Media result = mediaMapper.toEntity(request);

    assertEquals(ProcessingStatus.INITIATED, result.getProcessingStatus());
  }

  @Test
  void shouldHandlePartialData() {
    Media partialMedia = Media.builder()
        .mediaId(1L)
        .userId(testUserId)
        .mediaType(MediaType.IMAGE_JPEG)
        .processingStatus(ProcessingStatus.INITIATED)
        .build();

    MediaDto result = mediaMapper.toDto(partialMedia);

    assertAll("Partial data mapping",
        () -> assertEquals(1L, result.getMediaId()),
        () -> assertEquals(testUserId, result.getUserId()),
        () -> assertEquals(MediaType.IMAGE_JPEG, result.getMediaType()),
        () -> assertEquals(ProcessingStatus.INITIATED, result.getProcessingStatus()),
        () -> assertNull(result.getOriginalFilename()),
        () -> assertNull(result.getFileSize()),
        () -> assertNull(result.getContentHash())
    );
  }
}
