package com.recipe_manager.service.external.mediamanager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MediaManagerFallbackTest {

  private MediaManagerFallback mediaManagerFallback;

  @BeforeEach
  void setUp() {
    mediaManagerFallback = new MediaManagerFallback();
  }

  @Test
  @DisplayName("Should create media manager fallback successfully")
  void shouldCreateMediaManagerFallbackSuccessfully() {
    // Assert
    assertThat(mediaManagerFallback).isNotNull();
  }
}
