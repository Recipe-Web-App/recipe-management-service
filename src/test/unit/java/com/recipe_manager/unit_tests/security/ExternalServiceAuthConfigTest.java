package com.recipe_manager.unit_tests.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.recipe_manager.config.ExternalServicesConfig;
import com.recipe_manager.security.ExternalServiceAuthConfig;

import feign.RequestInterceptor;
import feign.RequestTemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ExternalServiceAuthConfigTest {

  @Mock
  private ExternalServicesConfig externalServicesConfig;

  @Mock
  private ExternalServicesConfig.RecipeScraperConfig recipeScraperConfig;

  private ExternalServiceAuthConfig authConfig;

  @BeforeEach
  void setUp() {
    authConfig = new ExternalServiceAuthConfig();
    ReflectionTestUtils.setField(authConfig, "externalServicesConfig", externalServicesConfig);
  }

  @Test
  @DisplayName("Should create recipe scraper auth interceptor and add API key header")
  void shouldCreateRecipeScraperAuthInterceptorAndAddApiKeyHeader() {
    when(externalServicesConfig.getRecipeScraper()).thenReturn(recipeScraperConfig);
    when(recipeScraperConfig.getApiKey()).thenReturn("test-api-key");

    RequestInterceptor interceptor = authConfig.recipeScraperAuthInterceptor();
    RequestTemplate template = new RequestTemplate();

    interceptor.apply(template);

    assertThat(interceptor).isNotNull();
    assertThat(template.headers()).containsKey("X-API-Key");
    assertThat(template.headers().get("X-API-Key")).contains("test-api-key");
    assertThat(template.headers()).containsKey("X-Correlation-ID");
    assertThat(template.headers()).containsKey("X-Service-Name");
    assertThat(template.headers().get("X-Service-Name")).contains("recipe-manager-service");
  }

  @Test
  @DisplayName("Should handle null API key and skip API key header")
  void shouldHandleNullApiKeyAndSkipApiKeyHeader() {
    when(externalServicesConfig.getRecipeScraper()).thenReturn(recipeScraperConfig);
    when(recipeScraperConfig.getApiKey()).thenReturn(null);

    RequestInterceptor interceptor = authConfig.recipeScraperAuthInterceptor();
    RequestTemplate template = new RequestTemplate();

    interceptor.apply(template);

    assertThat(interceptor).isNotNull();
    assertThat(template.headers()).doesNotContainKey("X-API-Key");
    assertThat(template.headers()).containsKey("X-Correlation-ID");
    assertThat(template.headers()).containsKey("X-Service-Name");
  }

  @Test
  @DisplayName("Should handle empty API key and skip API key header")
  void shouldHandleEmptyApiKeyAndSkipApiKeyHeader() {
    when(externalServicesConfig.getRecipeScraper()).thenReturn(recipeScraperConfig);
    when(recipeScraperConfig.getApiKey()).thenReturn("");

    RequestInterceptor interceptor = authConfig.recipeScraperAuthInterceptor();
    RequestTemplate template = new RequestTemplate();

    interceptor.apply(template);

    assertThat(interceptor).isNotNull();
    assertThat(template.headers()).doesNotContainKey("X-API-Key");
    assertThat(template.headers()).containsKey("X-Correlation-ID");
    assertThat(template.headers()).containsKey("X-Service-Name");
  }

  @Test
  @DisplayName("Should handle whitespace-only API key and skip API key header")
  void shouldHandleWhitespaceOnlyApiKeyAndSkipApiKeyHeader() {
    when(externalServicesConfig.getRecipeScraper()).thenReturn(recipeScraperConfig);
    when(recipeScraperConfig.getApiKey()).thenReturn("   ");

    RequestInterceptor interceptor = authConfig.recipeScraperAuthInterceptor();
    RequestTemplate template = new RequestTemplate();

    interceptor.apply(template);

    assertThat(interceptor).isNotNull();
    assertThat(template.headers()).doesNotContainKey("X-API-Key");
    assertThat(template.headers()).containsKey("X-Correlation-ID");
    assertThat(template.headers()).containsKey("X-Service-Name");
  }
}
