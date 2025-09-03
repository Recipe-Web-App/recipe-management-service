package com.recipe_manager.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.recipe_manager.config.ExternalServicesConfig;

/**
 * Test class for ServiceAuthClient.
 * Verifies that OAuth2 service-to-service token management works correctly.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ServiceAuthClientTest {

  @Mock private RestTemplate restTemplate;

  @Mock private ExternalServicesConfig externalServicesConfig;

  @Mock private ExternalServicesConfig.OAuth2ServiceConfig oauth2ServiceConfig;

  private ServiceAuthClient serviceAuthClient;

  @BeforeEach
  void setUp() {
    // Set up OAuth2 configuration mocks
    when(externalServicesConfig.getOauth2Service()).thenReturn(oauth2ServiceConfig);

    lenient().when(oauth2ServiceConfig.getBaseUrl()).thenReturn("http://auth-service.local");
    lenient().when(oauth2ServiceConfig.getTokenPath()).thenReturn("/api/v1/auth/oauth2/token");
    lenient().when(oauth2ServiceConfig.getClientId()).thenReturn("test-client-id");
    lenient().when(oauth2ServiceConfig.getClientSecret()).thenReturn("test-client-secret");
    lenient().when(oauth2ServiceConfig.getScopes()).thenReturn("read write");

    serviceAuthClient = new ServiceAuthClient(restTemplate, externalServicesConfig);
  }

  @Test
  @DisplayName("Should instantiate service auth client")
  void shouldInstantiateServiceAuthClient() {
    assertNotNull(serviceAuthClient);
  }

  @Test
  @DisplayName("Should acquire service token successfully")
  void shouldAcquireServiceTokenSuccessfully() throws ExecutionException, InterruptedException {
    // Given
    ServiceAuthClient.TokenResponse tokenResponse = createValidTokenResponse();
    ResponseEntity<ServiceAuthClient.TokenResponse> responseEntity = ResponseEntity.ok(tokenResponse);

    when(restTemplate.postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class)))
        .thenReturn(responseEntity);

    // When
    String accessToken = serviceAuthClient.getServiceAccessToken().get();

    // Then
    assertNotNull(accessToken);
    assertEquals("test-access-token", accessToken);

    verify(restTemplate).postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class));
  }

  @Test
  @DisplayName("Should use cached token when still valid")
  void shouldUseCachedTokenWhenStillValid() throws ExecutionException, InterruptedException {
    // Given
    ServiceAuthClient.TokenResponse tokenResponse = createValidTokenResponse();
    tokenResponse.setExpiresIn(3600); // 1 hour expiry
    ResponseEntity<ServiceAuthClient.TokenResponse> responseEntity = ResponseEntity.ok(tokenResponse);

    when(restTemplate.postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class)))
        .thenReturn(responseEntity);

    // When - First call
    String firstToken = serviceAuthClient.getServiceAccessToken().get();
    // When - Second call (should use cached)
    String secondToken = serviceAuthClient.getServiceAccessToken().get();

    // Then
    assertEquals(firstToken, secondToken);
    assertEquals("test-access-token", secondToken);

    // Verify REST call was made only once
    verify(restTemplate).postForEntity(
        eq("http://auth-service.local/api/v1/auth/oauth2/token"),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class));
  }

  @Test
  @DisplayName("Should refresh token when cached token is expired")
  void shouldRefreshTokenWhenCachedTokenExpired() throws ExecutionException, InterruptedException {
    // Given
    ServiceAuthClient.TokenResponse expiredTokenResponse = createValidTokenResponse();
    expiredTokenResponse.setExpiresIn(1); // 1 second expiry
    ResponseEntity<ServiceAuthClient.TokenResponse> expiredResponseEntity = ResponseEntity.ok(expiredTokenResponse);

    ServiceAuthClient.TokenResponse newTokenResponse = createValidTokenResponse();
    newTokenResponse.setAccessToken("new-access-token");
    ResponseEntity<ServiceAuthClient.TokenResponse> newResponseEntity = ResponseEntity.ok(newTokenResponse);

    when(restTemplate.postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class)))
        .thenReturn(expiredResponseEntity, newResponseEntity);

    // When - First call
    String firstToken = serviceAuthClient.getServiceAccessToken().get();

    // Wait for token to expire
    Thread.sleep(1100);

    // When - Second call (should refresh)
    String secondToken = serviceAuthClient.getServiceAccessToken().get();

    // Then
    assertEquals("test-access-token", firstToken);
    assertEquals("new-access-token", secondToken);
  }

  @Test
  @DisplayName("Should handle client credentials request with scopes")
  void shouldHandleClientCredentialsRequestWithScopes() throws ExecutionException, InterruptedException {
    // Given
    ServiceAuthClient.TokenResponse tokenResponse = createValidTokenResponse();
    ResponseEntity<ServiceAuthClient.TokenResponse> responseEntity = ResponseEntity.ok(tokenResponse);

    when(restTemplate.postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class)))
        .thenReturn(responseEntity);

    // When
    serviceAuthClient.getServiceAccessToken().get();

    // Then
    verify(restTemplate).postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class));
  }

  @Test
  @DisplayName("Should handle client credentials request without scopes")
  void shouldHandleClientCredentialsRequestWithoutScopes() throws ExecutionException, InterruptedException {
    // Given
    when(oauth2ServiceConfig.getScopes()).thenReturn(null);

    ServiceAuthClient.TokenResponse tokenResponse = createValidTokenResponse();
    ResponseEntity<ServiceAuthClient.TokenResponse> responseEntity = ResponseEntity.ok(tokenResponse);

    when(restTemplate.postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class)))
        .thenReturn(responseEntity);

    // When
    serviceAuthClient.getServiceAccessToken().get();

    // Then
    verify(restTemplate).postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class));
  }

  @Test
  @DisplayName("Should throw exception when token response is null")
  void shouldThrowExceptionWhenTokenResponseIsNull() {
    // Given
    ResponseEntity<ServiceAuthClient.TokenResponse> responseEntity = ResponseEntity.ok(null);

    when(restTemplate.postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class)))
        .thenReturn(responseEntity);

    // When & Then
    assertThrows(ExecutionException.class, () ->
        serviceAuthClient.getServiceAccessToken().get());
  }

  @Test
  @DisplayName("Should throw exception when REST call fails")
  void shouldThrowExceptionWhenRestCallFails() {
    // Given
    when(restTemplate.postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class)))
        .thenThrow(new RestClientException("Connection failed"));

    // When & Then
    assertThrows(ExecutionException.class, () ->
        serviceAuthClient.getServiceAccessToken().get());
  }

  @Test
  @DisplayName("Should handle empty scopes configuration")
  void shouldHandleEmptyScopesConfiguration() throws ExecutionException, InterruptedException {
    // Given
    when(oauth2ServiceConfig.getScopes()).thenReturn("");

    ServiceAuthClient.TokenResponse tokenResponse = createValidTokenResponse();
    ResponseEntity<ServiceAuthClient.TokenResponse> responseEntity = ResponseEntity.ok(tokenResponse);

    when(restTemplate.postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(ServiceAuthClient.TokenResponse.class)))
        .thenReturn(responseEntity);

    // When
    String accessToken = serviceAuthClient.getServiceAccessToken().get();

    // Then
    assertNotNull(accessToken);
    assertEquals("test-access-token", accessToken);
  }

  /**
   * Creates a valid token response for testing using the ServiceAuthClient internal class.
   */
  private ServiceAuthClient.TokenResponse createValidTokenResponse() {
    return ServiceAuthClient.TokenResponse.builder()
        .accessToken("test-access-token")
        .tokenType("Bearer")
        .expiresIn(900) // 15 minutes
        .scope("read write")
        .build();
  }

}
