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
import com.recipe_manager.model.dto.media.RecipeMediaDto;
import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.entity.media.RecipeMedia;
import com.recipe_manager.model.entity.media.RecipeMediaId;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;
import com.recipe_manager.model.mapper.RecipeMediaMapper;

@SpringBootTest(classes = {
    com.recipe_manager.model.mapper.RecipeMediaMapperImpl.class,
    com.recipe_manager.model.mapper.MediaMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("unit")
class RecipeMediaMapperTest {

  @Autowired
  private RecipeMediaMapper recipeMediaMapper;

  private RecipeMedia testRecipeMedia;
  private RecipeMediaDto testRecipeMediaDto;
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

    testRecipeMedia = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(10L)
        .media(testMedia)
        .build();

    MediaSummaryDto mediaSummaryDto = MediaSummaryDto.builder()
        .mediaId(1L)
        .originalFilename("test-image.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(1024L)
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    testRecipeMediaDto = RecipeMediaDto.builder()
        .mediaId(1L)
        .recipeId(10L)
        .media(mediaSummaryDto)
        .build();
  }

  @Test
  void shouldMapRecipeMediaToDto() {
    RecipeMediaDto result = recipeMediaMapper.toDto(testRecipeMedia);

    assertNotNull(result);
    assertAll("RecipeMedia to DTO mapping",
        () -> assertEquals(testRecipeMedia.getMediaId(), result.getMediaId()),
        () -> assertEquals(testRecipeMedia.getRecipeId(), result.getRecipeId()),
        () -> assertNotNull(result.getMedia()),
        () -> assertEquals(testMedia.getMediaId(), result.getMedia().getMediaId()),
        () -> assertEquals(testMedia.getOriginalFilename(), result.getMedia().getOriginalFilename()),
        () -> assertEquals(testMedia.getMediaType(), result.getMedia().getMediaType()),
        () -> assertEquals(testMedia.getFileSize(), result.getMedia().getFileSize()),
        () -> assertEquals(testMedia.getProcessingStatus(), result.getMedia().getProcessingStatus())
    );
  }

  @Test
  void shouldMapDtoToRecipeMedia() {
    RecipeMedia result = recipeMediaMapper.toEntity(testRecipeMediaDto);

    assertNotNull(result);
    assertAll("DTO to RecipeMedia mapping",
        () -> assertEquals(testRecipeMediaDto.getMediaId(), result.getMediaId()),
        () -> assertEquals(testRecipeMediaDto.getRecipeId(), result.getRecipeId()),
        () -> assertNull(result.getMedia()), // Should be ignored (managed by relationship)
        () -> assertNull(result.getRecipe()) // Should be ignored (managed by relationship)
    );
  }

  @Test
  void shouldHandleNullInput() {
    assertAll("Null input handling",
        () -> assertNull(recipeMediaMapper.toDto(null)),
        () -> assertNull(recipeMediaMapper.toEntity(null))
    );
  }

  @Test
  void shouldHandleNullMediaInRecipeMedia() {
    RecipeMedia recipeMediaWithNullMedia = RecipeMedia.builder()
        .mediaId(testRecipeMedia.getMediaId())
        .recipeId(testRecipeMedia.getRecipeId())
        .media(null)
        .build();

    RecipeMediaDto result = recipeMediaMapper.toDto(recipeMediaWithNullMedia);

    assertNotNull(result);
    assertAll("Null media handling",
        () -> assertEquals(testRecipeMedia.getMediaId(), result.getMediaId()),
        () -> assertEquals(testRecipeMedia.getRecipeId(), result.getRecipeId()),
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
      RecipeMedia recipeMedia = RecipeMedia.builder()
          .mediaId(testRecipeMedia.getMediaId())
          .recipeId(testRecipeMedia.getRecipeId())
          .media(mediaWithType)
          .build();

      RecipeMediaDto result = recipeMediaMapper.toDto(recipeMedia);

      assertNotNull(result.getMedia());
      assertEquals(mediaType, result.getMedia().getMediaType());
    }
  }

  @Test
  void shouldMapDifferentProcessingStatuses() {
    for (ProcessingStatus status : ProcessingStatus.values()) {
      Media mediaWithStatus = Media.builder()
          .mediaId(testMedia.getMediaId())
          .userId(testMedia.getUserId())
          .mediaType(testMedia.getMediaType())
          .originalFilename(testMedia.getOriginalFilename())
          .fileSize(testMedia.getFileSize())
          .contentHash(testMedia.getContentHash())
          .processingStatus(status)
          .createdAt(testMedia.getCreatedAt())
          .updatedAt(testMedia.getUpdatedAt())
          .build();
      RecipeMedia recipeMedia = RecipeMedia.builder()
          .mediaId(testRecipeMedia.getMediaId())
          .recipeId(testRecipeMedia.getRecipeId())
          .media(mediaWithStatus)
          .build();

      RecipeMediaDto result = recipeMediaMapper.toDto(recipeMedia);

      assertNotNull(result.getMedia());
      assertEquals(status, result.getMedia().getProcessingStatus());
    }
  }

  @Test
  void shouldPreserveMediaSummaryData() {
    RecipeMediaDto result = recipeMediaMapper.toDto(testRecipeMedia);

    assertNotNull(result.getMedia());
    assertAll("Media summary data preserved",
        () -> assertEquals(testMedia.getMediaId(), result.getMedia().getMediaId()),
        () -> assertEquals(testMedia.getOriginalFilename(), result.getMedia().getOriginalFilename()),
        () -> assertEquals(testMedia.getFileSize(), result.getMedia().getFileSize())
    );
  }

  @Test
  void shouldIgnoreRelationshipFieldsInEntityMapping() {
    RecipeMediaDto dto = RecipeMediaDto.builder()
        .mediaId(testRecipeMediaDto.getMediaId())
        .recipeId(testRecipeMediaDto.getRecipeId())
        .media(testRecipeMediaDto.getMedia())
        .build();
    RecipeMedia result = recipeMediaMapper.toEntity(dto);

    assertAll("Relationship fields ignored",
        () -> assertNull(result.getMedia()),
        () -> assertNull(result.getRecipe())
    );
  }

  @Test
  void shouldHandleNullMediaInDto() {
    RecipeMediaDto dtoWithNullMedia = RecipeMediaDto.builder()
        .mediaId(testRecipeMediaDto.getMediaId())
        .recipeId(testRecipeMediaDto.getRecipeId())
        .media(null)
        .build();

    RecipeMedia result = recipeMediaMapper.toEntity(dtoWithNullMedia);

    assertNotNull(result);
    assertAll("Null media in DTO",
        () -> assertEquals(dtoWithNullMedia.getMediaId(), result.getMediaId()),
        () -> assertEquals(dtoWithNullMedia.getRecipeId(), result.getRecipeId()),
        () -> assertNull(result.getMedia())
    );
  }
}
