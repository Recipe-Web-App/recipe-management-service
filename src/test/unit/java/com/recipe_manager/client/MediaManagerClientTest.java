package com.recipe_manager.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.recipe_manager.client.mediamanager.MediaManagerClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MediaManagerClientTest {

  @Mock
  private MediaManagerClient mediaManagerClient;

  @BeforeEach
  void setUp() {
    // Setup test data if needed
  }

  @Test
  @DisplayName("Should create media manager client successfully")
  void shouldCreateMediaManagerClientSuccessfully() {
    // Assert
    assertThat(mediaManagerClient).isNotNull();
  }
}
