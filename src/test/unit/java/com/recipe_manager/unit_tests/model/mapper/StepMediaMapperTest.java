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

import com.recipe_manager.model.dto.media.MediaSummaryDto;
import com.recipe_manager.model.dto.media.StepMediaDto;
import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.entity.media.StepMedia;
import com.recipe_manager.model.entity.media.StepMediaId;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;
import com.recipe_manager.model.mapper.StepMediaMapper;

@SpringBootTest(classes = {
    com.recipe_manager.model.mapper.StepMediaMapperImpl.class,
    com.recipe_manager.model.mapper.MediaMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("unit")
class StepMediaMapperTest {

  @Autowired
  private StepMediaMapper stepMediaMapper;

  private StepMedia testStepMedia;
  private StepMediaDto testStepMediaDto;
  private Media testMedia;
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

    StepMediaId id = StepMediaId.builder()
        .stepId(20L)
        .mediaId(1L)
        .build();

    testStepMedia = StepMedia.builder()
        .id(id)
        .media(testMedia)
        .build();

    MediaSummaryDto mediaSummaryDto = MediaSummaryDto.builder()
        .mediaId(1L)
        .originalFilename("test-image.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(1024L)
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    testStepMediaDto = StepMediaDto.builder()
        .mediaId(1L)
        .stepId(20L)
        .media(mediaSummaryDto)
        .build();
  }

  @Test
  void shouldMapStepMediaToDto() {
    StepMediaDto result = stepMediaMapper.toDto(testStepMedia);

    assertNotNull(result);
    assertAll("StepMedia to DTO mapping",
        () -> assertEquals(testStepMedia.getId().getMediaId(), result.getMediaId()),
        () -> assertEquals(testStepMedia.getId().getStepId(), result.getStepId()),
        () -> assertNotNull(result.getMedia()),
        () -> assertEquals(testMedia.getMediaId(), result.getMedia().getMediaId()),
        () -> assertEquals(testMedia.getOriginalFilename(), result.getMedia().getOriginalFilename()),
        () -> assertEquals(testMedia.getMediaType(), result.getMedia().getMediaType()),
        () -> assertEquals(testMedia.getFileSize(), result.getMedia().getFileSize()),
        () -> assertEquals(testMedia.getProcessingStatus(), result.getMedia().getProcessingStatus())
    );
  }

  @Test
  void shouldMapDtoToStepMedia() {
    StepMedia result = stepMediaMapper.toEntity(testStepMediaDto);

    assertNotNull(result);
    assertAll("DTO to StepMedia mapping",
        () -> assertNotNull(result.getId()),
        () -> assertEquals(testStepMediaDto.getMediaId(), result.getId().getMediaId()),
        () -> assertEquals(testStepMediaDto.getStepId(), result.getId().getStepId()),
        () -> assertNull(result.getMedia()), // Should be ignored (managed by relationship)
        () -> assertNull(result.getStep()) // Should be ignored (managed by relationship)
    );
  }

  @Test
  void shouldHandleNullInput() {
    assertAll("Null input handling",
        () -> assertNull(stepMediaMapper.toDto(null)),
        () -> assertNull(stepMediaMapper.toEntity(null))
    );
  }

  @Test
  void shouldHandleNullMediaInStepMedia() {
    StepMedia stepMediaWithNullMedia = StepMedia.builder()
        .id(testStepMedia.getId())
        .media(null)
        .build();

    StepMediaDto result = stepMediaMapper.toDto(stepMediaWithNullMedia);

    assertNotNull(result);
    assertAll("Null media handling",
        () -> assertEquals(testStepMedia.getId().getMediaId(), result.getMediaId()),
        () -> assertEquals(testStepMedia.getId().getStepId(), result.getStepId()),
        () -> assertNull(result.getMedia())
    );
  }

  @Test
  void shouldMapDifferentMediaTypes() {
    for (MediaType mediaType : MediaType.values()) {
      Media mediaWithType = Media.builder()
          .mediaId(testMedia.getMediaId())
          .userId(testMedia.getUserId())
          .mediaType(mediaType)
          .originalFilename(testMedia.getOriginalFilename())
          .fileSize(testMedia.getFileSize())
          .contentHash(testMedia.getContentHash())
          .processingStatus(testMedia.getProcessingStatus())
          .createdAt(testMedia.getCreatedAt())
          .updatedAt(testMedia.getUpdatedAt())
          .build();
      StepMedia stepMedia = StepMedia.builder()
          .id(testStepMedia.getId())
          .media(mediaWithType)
          .build();

      StepMediaDto result = stepMediaMapper.toDto(stepMedia);

      assertNotNull(result.getMedia());
      assertEquals(mediaType, result.getMedia().getMediaType());
    }
  }

  @Test
  void shouldIgnoreRelationshipFieldsInEntityMapping() {
    StepMediaDto dto = StepMediaDto.builder()
        .mediaId(testStepMediaDto.getMediaId())
        .stepId(testStepMediaDto.getStepId())
        .media(testStepMediaDto.getMedia())
        .build();
    StepMedia result = stepMediaMapper.toEntity(dto);

    assertAll("Relationship fields ignored",
        () -> assertNull(result.getMedia()),
        () -> assertNull(result.getStep())
    );
  }
}
