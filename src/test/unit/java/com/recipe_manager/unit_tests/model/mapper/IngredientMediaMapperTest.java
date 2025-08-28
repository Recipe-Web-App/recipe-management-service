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

import com.recipe_manager.model.dto.media.IngredientMediaDto;
import com.recipe_manager.model.dto.media.MediaSummaryDto;
import com.recipe_manager.model.entity.media.IngredientMedia;
import com.recipe_manager.model.entity.media.IngredientMediaId;
import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;
import com.recipe_manager.model.mapper.IngredientMediaMapper;

@SpringBootTest(classes = {
    com.recipe_manager.model.mapper.IngredientMediaMapperImpl.class,
    com.recipe_manager.model.mapper.MediaMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("unit")
class IngredientMediaMapperTest {

  @Autowired
  private IngredientMediaMapper ingredientMediaMapper;

  private IngredientMedia testIngredientMedia;
  private IngredientMediaDto testIngredientMediaDto;
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

    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(10L)
        .ingredientId(5L)
        .mediaId(1L)
        .build();

    testIngredientMedia = IngredientMedia.builder()
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

    testIngredientMediaDto = IngredientMediaDto.builder()
        .mediaId(1L)
        .recipeId(10L)
        .ingredientId(5L)
        .media(mediaSummaryDto)
        .build();
  }

  @Test
  void shouldMapIngredientMediaToDto() {
    IngredientMediaDto result = ingredientMediaMapper.toDto(testIngredientMedia);

    assertNotNull(result);
    assertAll("IngredientMedia to DTO mapping",
        () -> assertEquals(testIngredientMedia.getId().getMediaId(), result.getMediaId()),
        () -> assertEquals(testIngredientMedia.getId().getRecipeId(), result.getRecipeId()),
        () -> assertEquals(testIngredientMedia.getId().getIngredientId(), result.getIngredientId()),
        () -> assertNotNull(result.getMedia()),
        () -> assertEquals(testMedia.getMediaId(), result.getMedia().getMediaId()),
        () -> assertEquals(testMedia.getOriginalFilename(), result.getMedia().getOriginalFilename()),
        () -> assertEquals(testMedia.getMediaType(), result.getMedia().getMediaType()),
        () -> assertEquals(testMedia.getFileSize(), result.getMedia().getFileSize()),
        () -> assertEquals(testMedia.getProcessingStatus(), result.getMedia().getProcessingStatus())
    );
  }

  @Test
  void shouldMapDtoToIngredientMedia() {
    IngredientMedia result = ingredientMediaMapper.toEntity(testIngredientMediaDto);

    assertNotNull(result);
    assertAll("DTO to IngredientMedia mapping",
        () -> assertNotNull(result.getId()),
        () -> assertEquals(testIngredientMediaDto.getMediaId(), result.getId().getMediaId()),
        () -> assertEquals(testIngredientMediaDto.getRecipeId(), result.getId().getRecipeId()),
        () -> assertEquals(testIngredientMediaDto.getIngredientId(), result.getId().getIngredientId()),
        () -> assertNull(result.getMedia()), // Should be ignored (managed by relationship)
        () -> assertNull(result.getIngredient()) // Should be ignored (managed by relationship)
    );
  }

  @Test
  void shouldHandleNullInput() {
    assertAll("Null input handling",
        () -> assertNull(ingredientMediaMapper.toDto(null)),
        () -> assertNull(ingredientMediaMapper.toEntity(null))
    );
  }

  @Test
  void shouldHandleNullMediaInIngredientMedia() {
    IngredientMedia ingredientMediaWithNullMedia = IngredientMedia.builder()
        .id(testIngredientMedia.getId())
        .media(null)
        .build();

    IngredientMediaDto result = ingredientMediaMapper.toDto(ingredientMediaWithNullMedia);

    assertNotNull(result);
    assertAll("Null media handling",
        () -> assertEquals(testIngredientMedia.getId().getMediaId(), result.getMediaId()),
        () -> assertEquals(testIngredientMedia.getId().getRecipeId(), result.getRecipeId()),
        () -> assertEquals(testIngredientMedia.getId().getIngredientId(), result.getIngredientId()),
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
      IngredientMedia ingredientMedia = IngredientMedia.builder()
          .id(testIngredientMedia.getId())
          .media(mediaWithType)
          .build();

      IngredientMediaDto result = ingredientMediaMapper.toDto(ingredientMedia);

      assertNotNull(result.getMedia());
      assertEquals(mediaType, result.getMedia().getMediaType());
    }
  }

  @Test
  void shouldIgnoreRelationshipFieldsInEntityMapping() {
    IngredientMediaDto dto = IngredientMediaDto.builder()
        .mediaId(testIngredientMediaDto.getMediaId())
        .recipeId(testIngredientMediaDto.getRecipeId())
        .ingredientId(testIngredientMediaDto.getIngredientId())
        .media(testIngredientMediaDto.getMedia())
        .build();
    IngredientMedia result = ingredientMediaMapper.toEntity(dto);

    assertAll("Relationship fields ignored",
        () -> assertNull(result.getMedia()),
        () -> assertNull(result.getIngredient())
    );
  }
}
