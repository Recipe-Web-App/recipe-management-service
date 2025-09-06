package com.recipe_manager.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

/**
 * Unit tests for {@link HttpClientConfig}.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class HttpClientConfigTest {

  @Mock private ExternalServicesConfig externalServicesConfig;

  @Mock private ExternalServicesConfig.CommonConfig commonConfig;

  private HttpClientConfig httpClientConfig;

  @BeforeEach
  void setUp() {
    httpClientConfig = new HttpClientConfig();
    when(externalServicesConfig.getCommon()).thenReturn(commonConfig);
  }

  @Test
  void restTemplate_ShouldCreateConfiguredInstance() {
    // Arrange
    Duration connectTimeout = Duration.ofSeconds(5);
    Duration readTimeout = Duration.ofSeconds(30);
    when(commonConfig.getConnectTimeout()).thenReturn(connectTimeout);
    when(commonConfig.getReadTimeout()).thenReturn(readTimeout);

    // Act
    RestTemplate result = httpClientConfig.restTemplate(externalServicesConfig);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isInstanceOf(RestTemplate.class);
  }

  @Test
  void restTemplate_ShouldHandleNullTimeouts() {
    // Arrange
    when(commonConfig.getConnectTimeout()).thenReturn(null);
    when(commonConfig.getReadTimeout()).thenReturn(null);

    // Act
    RestTemplate result = httpClientConfig.restTemplate(externalServicesConfig);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isInstanceOf(RestTemplate.class);
  }
}
