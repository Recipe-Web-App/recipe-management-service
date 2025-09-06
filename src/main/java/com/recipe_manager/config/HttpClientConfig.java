package com.recipe_manager.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for HTTP client components.
 *
 * <p>This configuration provides REST template beans for making HTTP requests to external services.
 */
@Configuration
public class HttpClientConfig {

  /**
   * Creates a configured RestTemplate bean for external service communications.
   *
   * @param externalServicesConfig configuration properties for external services
   * @return configured RestTemplate instance
   */
  @Bean
  public RestTemplate restTemplate(final ExternalServicesConfig externalServicesConfig) {
    ExternalServicesConfig.CommonConfig commonConfig = externalServicesConfig.getCommon();

    return new RestTemplateBuilder()
        .setConnectTimeout(commonConfig.getConnectTimeout())
        .setReadTimeout(commonConfig.getReadTimeout())
        .build();
  }
}
