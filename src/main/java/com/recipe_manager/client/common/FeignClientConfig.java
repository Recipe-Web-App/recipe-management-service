package com.recipe_manager.client.common;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.recipe_manager.config.ExternalServicesConfig;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;

/**
 * Configuration for Feign clients. Provides centralized configuration for all external service
 * clients including timeouts, retry policies, and error handling.
 */
@Configuration
public class FeignClientConfig {

  /** Configuration for external services. */
  @Autowired private ExternalServicesConfig externalServicesConfig;

  /**
   * Configures request options for Feign clients.
   *
   * @return configured request options
   */
  @Bean
  public Request.Options requestOptions() {
    return new Request.Options(
        externalServicesConfig.getCommon().getConnectTimeout().toMillis(),
        TimeUnit.MILLISECONDS,
        externalServicesConfig.getCommon().getReadTimeout().toMillis(),
        TimeUnit.MILLISECONDS,
        true);
  }

  /**
   * Configures retry behavior for Feign clients.
   *
   * @return configured retryer
   */
  @Bean
  public Retryer retryer() {
    return Retryer.NEVER_RETRY; // We'll handle retries via Resilience4j
  }

  /**
   * Configures logging level for Feign clients.
   *
   * @return logging level
   */
  @Bean
  public Logger.Level feignLoggerLevel() {
    return externalServicesConfig.getCommon().getLogRequests()
        ? Logger.Level.FULL
        : Logger.Level.NONE;
  }

  /**
   * Custom error decoder for external service responses.
   *
   * @return error decoder
   */
  @Bean
  public ErrorDecoder errorDecoder() {
    return new ExternalServiceErrorDecoder();
  }
}
