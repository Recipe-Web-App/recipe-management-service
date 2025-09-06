package com.recipe_manager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.recipe_manager.config.ExternalServicesConfig;
import com.recipe_manager.util.SecurityUtils;

import feign.RequestInterceptor;

/**
 * Configuration for external service authentication. Provides request interceptors for adding
 * authentication headers to external service calls.
 */
@Configuration
public class ExternalServiceAuthConfig {

  /** Configuration for external services. */
  @Autowired private ExternalServicesConfig externalServicesConfig;

  /**
   * Request interceptor for recipe scraper service authentication. Adds API key header if
   * configured.
   *
   * @return request interceptor for recipe scraper service
   */
  @Bean
  public RequestInterceptor recipeScraperAuthInterceptor() {
    return requestTemplate -> {
      String apiKey = externalServicesConfig.getRecipeScraper().getApiKey();
      if (StringUtils.hasText(apiKey)) {
        requestTemplate.header("X-API-Key", apiKey);
      }

      // Add correlation ID for tracing
      String correlationId = SecurityUtils.generateCorrelationId();
      requestTemplate.header("X-Correlation-ID", correlationId);

      // Add service identification
      requestTemplate.header("X-Service-Name", "recipe-management-service");
    };
  }
}
