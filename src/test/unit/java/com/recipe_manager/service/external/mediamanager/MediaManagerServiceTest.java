package com.recipe_manager.service.external.mediamanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.recipe_manager.client.mediamanager.MediaManagerClient;
import com.recipe_manager.config.ExternalServicesConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.micrometer.core.instrument.MeterRegistry;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MediaManagerServiceTest {

  @Mock
  private MediaManagerClient mediaManagerClient;

  @Mock
  private ExternalServicesConfig externalServicesConfig;

  @Mock
  private MeterRegistry meterRegistry;

  @InjectMocks
  private MediaManagerService mediaManagerService;

  @BeforeEach
  void setUp() {
    // Setup test data if needed
  }

  @Test
  @DisplayName("Should create media manager service successfully")
  void shouldCreateMediaManagerServiceSuccessfully() {
    // Assert
    assertThat(mediaManagerService).isNotNull();
  }

  @Test
  @DisplayName("Should return service availability based on configuration")
  void shouldReturnServiceAvailabilityBasedOnConfiguration() {
    // Setup
    ExternalServicesConfig.MediaManagerConfig mockConfig = new ExternalServicesConfig.MediaManagerConfig();
    mockConfig.setEnabled(true);

    when(externalServicesConfig.getMediaManager()).thenReturn(mockConfig);

    // Act
    boolean isAvailable = mediaManagerService.isServiceAvailable();

    // Assert
    assertThat(isAvailable).isTrue();
  }
}
