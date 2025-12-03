package com.recipe_manager.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.recipe_manager.config.ExternalServicesConfig;
import com.recipe_manager.exception.ExternalServiceException;

/**
 * Unit tests for OAuth2Client.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class OAuth2ClientTest {

  @Mock private ExternalServicesConfig externalServicesConfig;

  @Mock private ExternalServicesConfig.OAuth2ServiceConfig oauth2ServiceConfig;

  @Mock private RestTemplate restTemplate;

  private OAuth2Client oauth2Client;

  @BeforeEach
  void setUp() {
    when(externalServicesConfig.getOauth2Service()).thenReturn(oauth2ServiceConfig);
    lenient().when(oauth2ServiceConfig.getBaseUrl()).thenReturn("http://localhost:8080");
    lenient().when(oauth2ServiceConfig.getTokenPath()).thenReturn("/api/v1/auth/oauth2/token");
    lenient().when(oauth2ServiceConfig.getIntrospectionPath()).thenReturn("/api/v1/auth/oauth2/introspect");
    lenient().when(oauth2ServiceConfig.getUserInfoPath()).thenReturn("/api/v1/auth/oauth2/userinfo");
    lenient().when(oauth2ServiceConfig.getClientId()).thenReturn("test-client-id");
    lenient().when(oauth2ServiceConfig.getClientSecret()).thenReturn("test-client-secret");
    lenient().when(oauth2ServiceConfig.getScopes()).thenReturn("read write");

    oauth2Client = new OAuth2Client(externalServicesConfig, restTemplate);
  }

  @Test
  void getServiceAccessToken_ShouldReturnTokenSuccessfully() {
    // Arrange
    OAuth2Client.TokenResponse tokenResponse = new OAuth2Client.TokenResponse();
    tokenResponse.setAccessToken("test-access-token");
    tokenResponse.setTokenType("Bearer");
    tokenResponse.setExpiresIn(3600L);

    when(restTemplate.postForEntity(
            eq("http://localhost:8080/api/v1/auth/oauth2/token"),
            any(HttpEntity.class),
            eq(OAuth2Client.TokenResponse.class)))
        .thenReturn(new ResponseEntity<>(tokenResponse, HttpStatus.OK));

    // Act
    CompletableFuture<String> result = oauth2Client.getServiceAccessToken();
    String accessToken = result.join();

    // Assert
    assertEquals("test-access-token", accessToken);
    verify(restTemplate).postForEntity(
        eq("http://localhost:8080/api/v1/auth/oauth2/token"),
        any(HttpEntity.class),
        eq(OAuth2Client.TokenResponse.class));
  }

  @Test
  void getServiceAccessToken_ShouldUseCachedToken() {
    // Arrange
    OAuth2Client.TokenResponse tokenResponse = new OAuth2Client.TokenResponse();
    tokenResponse.setAccessToken("test-access-token");
    tokenResponse.setTokenType("Bearer");
    tokenResponse.setExpiresIn(3600L);

    when(restTemplate.postForEntity(
            eq("http://localhost:8080/api/v1/auth/oauth2/token"),
            any(HttpEntity.class),
            eq(OAuth2Client.TokenResponse.class)))
        .thenReturn(new ResponseEntity<>(tokenResponse, HttpStatus.OK));

    // Act - First call
    String firstToken = oauth2Client.getServiceAccessToken().join();

    // Act - Second call (should use cached token)
    String secondToken = oauth2Client.getServiceAccessToken().join();

    // Assert
    assertEquals("test-access-token", firstToken);
    assertEquals("test-access-token", secondToken);
    // Should only call REST template once due to caching
    verify(restTemplate, times(1))
        .postForEntity(
            eq("http://localhost:8080/api/v1/auth/oauth2/token"),
            any(HttpEntity.class),
            eq(OAuth2Client.TokenResponse.class));
  }

  @Test
  void getServiceAccessToken_ShouldThrowExceptionOnRestClientError() {
    // Arrange
    when(restTemplate.postForEntity(
            eq("http://localhost:8080/api/v1/auth/oauth2/token"),
            any(HttpEntity.class),
            eq(OAuth2Client.TokenResponse.class)))
        .thenThrow(new RestClientException("Connection failed"));

    // Act & Assert
    CompletableFuture<String> result = oauth2Client.getServiceAccessToken();
    CompletionException thrown = assertThrows(CompletionException.class, result::join);
    assertTrue(thrown.getCause() instanceof ExternalServiceException);
  }

  @Test
  void introspectToken_ShouldReturnIntrospectionResponse() {
    // Arrange
    String testToken = "test-token";

    // Mock introspection response
    OAuth2Client.TokenIntrospectionResponse introspectionResponse =
        new OAuth2Client.TokenIntrospectionResponse();
    introspectionResponse.setActive(true);
    introspectionResponse.setClientId("test-client");
    introspectionResponse.setUsername("test-user");

    when(restTemplate.postForEntity(
            eq("http://localhost:8080/api/v1/auth/oauth2/introspect"),
            any(HttpEntity.class),
            eq(OAuth2Client.TokenIntrospectionResponse.class)))
        .thenReturn(new ResponseEntity<>(introspectionResponse, HttpStatus.OK));

    // Act
    CompletableFuture<OAuth2Client.TokenIntrospectionResponse> result =
        oauth2Client.introspectToken(testToken);
    OAuth2Client.TokenIntrospectionResponse response = result.join();

    // Assert
    assertNotNull(response);
    assertTrue(response.getActive());
    assertEquals("test-client", response.getClientId());
    assertEquals("test-user", response.getUsername());
  }

  @Test
  void introspectToken_ShouldThrowExceptionOnRestClientError() {
    // Arrange
    String testToken = "test-token";

    when(restTemplate.postForEntity(
            eq("http://localhost:8080/api/v1/auth/oauth2/introspect"),
            any(HttpEntity.class),
            eq(OAuth2Client.TokenIntrospectionResponse.class)))
        .thenThrow(new RestClientException("Introspection failed"));

    // Act & Assert
    CompletableFuture<OAuth2Client.TokenIntrospectionResponse> result =
        oauth2Client.introspectToken(testToken);
    CompletionException thrown = assertThrows(CompletionException.class, result::join);
    assertTrue(thrown.getCause() instanceof ExternalServiceException);
  }

  @Test
  void getUserInfo_ShouldReturnUserInfoResponse() {
    // Arrange
    String accessToken = "user-access-token";

    OAuth2Client.UserInfoResponse userInfoResponse = new OAuth2Client.UserInfoResponse();
    userInfoResponse.setSub("user123");
    userInfoResponse.setName("John Doe");
    userInfoResponse.setEmail("john@example.com");

    when(restTemplate.exchange(
            eq("http://localhost:8080/api/v1/auth/oauth2/userinfo"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(OAuth2Client.UserInfoResponse.class)))
        .thenReturn(new ResponseEntity<>(userInfoResponse, HttpStatus.OK));

    // Act
    CompletableFuture<OAuth2Client.UserInfoResponse> result =
        oauth2Client.getUserInfo(accessToken);
    OAuth2Client.UserInfoResponse response = result.join();

    // Assert
    assertNotNull(response);
    assertEquals("user123", response.getSub());
    assertEquals("John Doe", response.getName());
    assertEquals("john@example.com", response.getEmail());
  }

  @Test
  void getUserInfo_ShouldThrowExceptionOnRestClientError() {
    // Arrange
    String accessToken = "user-access-token";

    when(restTemplate.exchange(
            eq("http://localhost:8080/api/v1/auth/oauth2/userinfo"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(OAuth2Client.UserInfoResponse.class)))
        .thenThrow(new RestClientException("User info failed"));

    // Act & Assert
    CompletableFuture<OAuth2Client.UserInfoResponse> result = oauth2Client.getUserInfo(accessToken);
    CompletionException thrown = assertThrows(CompletionException.class, result::join);
    assertTrue(thrown.getCause() instanceof ExternalServiceException);
  }

  @Test
  void getServiceAccessToken_ShouldHandleEmptyResponse() {
    // Arrange
    when(restTemplate.postForEntity(
            eq("http://localhost:8080/api/v1/auth/oauth2/token"),
            any(HttpEntity.class),
            eq(OAuth2Client.TokenResponse.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

    // Act & Assert
    CompletableFuture<String> result = oauth2Client.getServiceAccessToken();
    CompletionException thrown = assertThrows(CompletionException.class, result::join);
    assertTrue(thrown.getCause() instanceof ExternalServiceException);
  }

  @Test
  void introspectToken_ShouldHandleEmptyResponse() {
    // Arrange
    String testToken = "test-token";

    when(restTemplate.postForEntity(
            eq("http://localhost:8080/api/v1/auth/oauth2/introspect"),
            any(HttpEntity.class),
            eq(OAuth2Client.TokenIntrospectionResponse.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

    // Act & Assert
    CompletableFuture<OAuth2Client.TokenIntrospectionResponse> result =
        oauth2Client.introspectToken(testToken);
    CompletionException thrown = assertThrows(CompletionException.class, result::join);
    assertTrue(thrown.getCause() instanceof ExternalServiceException);
  }

  @Test
  void getUserInfo_ShouldHandleEmptyResponse() {
    // Arrange
    String accessToken = "user-access-token";

    when(restTemplate.exchange(
            eq("http://localhost:8080/api/v1/auth/oauth2/userinfo"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(OAuth2Client.UserInfoResponse.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

    // Act & Assert
    CompletableFuture<OAuth2Client.UserInfoResponse> result = oauth2Client.getUserInfo(accessToken);
    CompletionException thrown = assertThrows(CompletionException.class, result::join);
    assertTrue(thrown.getCause() instanceof ExternalServiceException);
  }
}
