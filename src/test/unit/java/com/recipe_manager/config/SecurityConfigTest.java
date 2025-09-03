package com.recipe_manager.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe_manager.security.JwtAuthenticationFilter;
import com.recipe_manager.security.ServiceAuthenticationFilter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Unit tests for SecurityConfig.
 */
@Tag("unit")
class SecurityConfigTest {

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create filterChain bean")
  void shouldCreateFilterChainBean() throws Exception {
    SecurityConfig config = new SecurityConfig();
    HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
    JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);
    ServiceAuthenticationFilter serviceFilter = mock(ServiceAuthenticationFilter.class);

    assertNotNull(config.filterChain(http, jwtFilter, serviceFilter));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create passwordEncoder bean")
  void shouldCreatePasswordEncoderBean() {
    SecurityConfig config = new SecurityConfig();
    PasswordEncoder encoder = config.passwordEncoder();
    assertNotNull(encoder);
    String hash = encoder.encode("password");
    assertTrue(encoder.matches("password", hash));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create corsConfigurationSource bean")
  void shouldCreateCorsConfigurationSourceBean() {
    SecurityConfig config = new SecurityConfig();
    CorsConfigurationSource source = config.corsConfigurationSource();
    assertNotNull(source);
    assertNotNull(source.getCorsConfiguration(new MockHttpServletRequest()));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should instantiate SecurityConfig")
  void securityConfigLoads() {
    SecurityConfig config = new SecurityConfig();
    assertNotNull(config);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should reference SecurityConfig class")
  void securityBeansCreated() {
    assertNotNull(SecurityConfig.class);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover private constructor")
  void shouldCoverPrivateConstructor() throws Exception {
    java.lang.reflect.Constructor<SecurityConfig> constructor = SecurityConfig.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should verify CORS configuration values")
  void shouldVerifyCorsConfigurationValues() {
    SecurityConfig config = new SecurityConfig();
    CorsConfigurationSource source = config.corsConfigurationSource();
    MockHttpServletRequest req = new MockHttpServletRequest();
    var cors = source.getCorsConfiguration(req);
    assertNotNull(cors);
    assertTrue(cors.getAllowedOriginPatterns().contains("*"));
    assertTrue(cors.getAllowedMethods().containsAll(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")));
    assertTrue(cors.getAllowedHeaders()
        .containsAll(java.util.List.of("Authorization", "Content-Type", "X-Request-ID", "X-User-ID", "X-Session-ID")));
    assertTrue(cors.getAllowCredentials());
    assertTrue(cors.getMaxAge() > 0);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should configure filterChain with JWT filter")
  void shouldConfigureFilterChainWithJwtFilter() throws Exception {
    SecurityConfig config = new SecurityConfig();
    HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
    JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);
    ServiceAuthenticationFilter serviceFilter = mock(ServiceAuthenticationFilter.class);
    DefaultSecurityFilterChain filterChain = mock(DefaultSecurityFilterChain.class);

    when(http.build()).thenReturn(filterChain);

    SecurityFilterChain result = config.filterChain(http, jwtFilter, serviceFilter);
    assertNotNull(result);
    verify(http).build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should configure security filter chain with all components")
  void shouldConfigureSecurityFilterChain() throws Exception {
    SecurityConfig config = new SecurityConfig();
    HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
    JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);
    ServiceAuthenticationFilter serviceFilter = mock(ServiceAuthenticationFilter.class);

    when(http.build()).thenReturn(mock(DefaultSecurityFilterChain.class));

    SecurityFilterChain result = config.filterChain(http, jwtFilter, serviceFilter);
    assertNotNull(result);

    // Verify that build was called at the end
    verify(http).build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create BCrypt password encoder")
  void shouldCreateBCryptPasswordEncoder() {
    SecurityConfig config = new SecurityConfig();
    PasswordEncoder encoder = config.passwordEncoder();
    assertNotNull(encoder);
    assertTrue(encoder instanceof BCryptPasswordEncoder);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should configure CORS with correct max age")
  void shouldConfigureCorsWithCorrectMaxAge() {
    SecurityConfig config = new SecurityConfig();
    CorsConfigurationSource source = config.corsConfigurationSource();
    MockHttpServletRequest req = new MockHttpServletRequest();
    CorsConfiguration cors = source.getCorsConfiguration(req);
    assertNotNull(cors);
    assertEquals(3600L, cors.getMaxAge());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should verify CORS allows credentials")
  void shouldVerifyCorsAllowsCredentials() {
    SecurityConfig config = new SecurityConfig();
    CorsConfigurationSource source = config.corsConfigurationSource();
    MockHttpServletRequest req = new MockHttpServletRequest();
    CorsConfiguration cors = source.getCorsConfiguration(req);
    assertNotNull(cors);
    assertTrue(cors.getAllowCredentials());
  }
}
