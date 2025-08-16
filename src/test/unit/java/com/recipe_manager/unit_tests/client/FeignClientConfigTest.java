package com.recipe_manager.unit_tests.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.time.Duration;

import com.recipe_manager.client.common.FeignClientConfig;
import com.recipe_manager.config.ExternalServicesConfig;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class FeignClientConfigTest {

  @Mock
  private ExternalServicesConfig externalServicesConfig;

  @Mock
  private ExternalServicesConfig.CommonConfig commonConfig;

  private FeignClientConfig feignConfig;

  @BeforeEach
  void setUp() {
    feignConfig = new FeignClientConfig();
    ReflectionTestUtils.setField(feignConfig, "externalServicesConfig", externalServicesConfig);
    lenient().when(externalServicesConfig.getCommon()).thenReturn(commonConfig);
  }

  @Test
  @DisplayName("Should create error decoder")
  void shouldCreateErrorDecoder() {
    ErrorDecoder errorDecoder = feignConfig.errorDecoder();

    assertThat(errorDecoder).isNotNull();
  }

  @Test
  @DisplayName("Should create feign logger level when logging enabled")
  void shouldCreateFeignLoggerLevelWhenLoggingEnabled() {
    when(commonConfig.getLogRequests()).thenReturn(true);

    Logger.Level loggerLevel = feignConfig.feignLoggerLevel();

    assertThat(loggerLevel).isEqualTo(Logger.Level.FULL);
  }

  @Test
  @DisplayName("Should create feign logger level when logging disabled")
  void shouldCreateFeignLoggerLevelWhenLoggingDisabled() {
    when(commonConfig.getLogRequests()).thenReturn(false);

    Logger.Level loggerLevel = feignConfig.feignLoggerLevel();

    assertThat(loggerLevel).isEqualTo(Logger.Level.NONE);
  }

  @Test
  @DisplayName("Should create request options")
  void shouldCreateRequestOptions() {
    when(commonConfig.getConnectTimeout()).thenReturn(Duration.ofSeconds(3));
    when(commonConfig.getReadTimeout()).thenReturn(Duration.ofSeconds(10));

    Request.Options options = feignConfig.requestOptions();

    assertThat(options).isNotNull();
  }

  @Test
  @DisplayName("Should create retryer")
  void shouldCreateRetryer() {
    Retryer retryer = feignConfig.retryer();

    assertThat(retryer).isEqualTo(Retryer.NEVER_RETRY);
  }
}
