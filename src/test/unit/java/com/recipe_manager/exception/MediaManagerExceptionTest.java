package com.recipe_manager.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.recipe_manager.model.enums.ExternalServiceName;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class MediaManagerExceptionTest {

  @Test
  @DisplayName("Should create exception with message only")
  void shouldCreateExceptionWithMessageOnly() {
    // Act
    MediaManagerException exception = new MediaManagerException("Test error message");

    // Assert
    assertThat(exception.getMessage()).isEqualTo("Test error message");
    assertThat(exception.getServiceName()).isEqualTo(ExternalServiceName.MEDIA_SERVICE);
    assertThat(exception.getMediaId()).isNull();
    assertThat(exception.getCause()).isNull();
  }

  @Test
  @DisplayName("Should create exception with message and cause")
  void shouldCreateExceptionWithMessageAndCause() {
    // Arrange
    RuntimeException cause = new RuntimeException("Root cause");

    // Act
    MediaManagerException exception = new MediaManagerException("Test error message", cause);

    // Assert
    assertThat(exception.getMessage()).isEqualTo("Test error message");
    assertThat(exception.getServiceName()).isEqualTo(ExternalServiceName.MEDIA_SERVICE);
    assertThat(exception.getMediaId()).isNull();
    assertThat(exception.getCause()).isEqualTo(cause);
  }

  @Test
  @DisplayName("Should create exception with media ID and message")
  void shouldCreateExceptionWithMediaIdAndMessage() {
    // Act
    MediaManagerException exception = new MediaManagerException(123L, "Test error message");

    // Assert
    assertThat(exception.getMessage()).isEqualTo("Media manager error for media 123: Test error message");
    assertThat(exception.getServiceName()).isEqualTo(ExternalServiceName.MEDIA_SERVICE);
    assertThat(exception.getMediaId()).isEqualTo(123L);
    assertThat(exception.getCause()).isNull();
  }

  @Test
  @DisplayName("Should create exception with media ID, message and cause")
  void shouldCreateExceptionWithMediaIdMessageAndCause() {
    // Arrange
    RuntimeException cause = new RuntimeException("Root cause");

    // Act
    MediaManagerException exception = new MediaManagerException(123L, "Test error message", cause);

    // Assert
    assertThat(exception.getMessage()).isEqualTo("Media manager error for media 123: Test error message");
    assertThat(exception.getServiceName()).isEqualTo(ExternalServiceName.MEDIA_SERVICE);
    assertThat(exception.getMediaId()).isEqualTo(123L);
    assertThat(exception.getCause()).isEqualTo(cause);
  }

  @Test
  @DisplayName("Should create exception with media ID, status code and message")
  void shouldCreateExceptionWithMediaIdStatusCodeAndMessage() {
    // Act
    MediaManagerException exception = new MediaManagerException(123L, 404, "Media not found");

    // Assert
    assertThat(exception.getMessage()).isEqualTo("Media manager error for media 123: Media not found");
    assertThat(exception.getServiceName()).isEqualTo(ExternalServiceName.MEDIA_SERVICE);
    assertThat(exception.getMediaId()).isEqualTo(123L);
    assertThat(exception.getStatusCode()).isEqualTo(404);
  }
}
