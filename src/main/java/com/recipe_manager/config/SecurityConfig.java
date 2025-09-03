package com.recipe_manager.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.recipe_manager.security.JwtAuthenticationFilter;
import com.recipe_manager.security.ServiceAuthenticationFilter;

/**
 * Security configuration for the Recipe Manager Service.
 *
 * <p>This configuration provides:
 *
 * <ul>
 *   <li>JWT-based authentication
 *   <li>Role-based authorization
 *   <li>CORS configuration
 *   <li>Security headers
 *   <li>Session management
 *   <li>Password encoding
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  /** Default token validity in seconds. */
  private static final long DEFAULT_TOKEN_VALIDITY_SECONDS = 3600L;

  /**
   * Configures the security filter chain with authentication and authorization rules.
   *
   * @param http The HttpSecurity object to configure
   * @param jwtAuthFilter The JWT authentication filter
   * @param serviceAuthFilter The service authentication filter
   * @return Configured SecurityFilterChain
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain filterChain(
      final HttpSecurity http,
      final JwtAuthenticationFilter jwtAuthFilter,
      final ServiceAuthenticationFilter serviceAuthFilter)
      throws Exception {
    http
        // Disable CSRF for API endpoints
        .csrf(AbstractHttpConfigurer::disable)

        // Configure CORS
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))

        // Configure session management
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Configure authorization rules
        .authorizeHttpRequests(
            authz ->
                authz
                    // Public endpoints
                    .requestMatchers("/actuator/**", "/health", "/info")
                    .permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()

                    // API endpoints require authentication
                    .requestMatchers("/api/v1/**")
                    .authenticated()

                    // Default deny all
                    .anyRequest()
                    .authenticated())

        // Add service auth filter before JWT filter
        .addFilterBefore(serviceAuthFilter, UsernamePasswordAuthenticationFilter.class)
        // Add JWT filter after service auth filter
        .addFilterAfter(jwtAuthFilter, ServiceAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Creates a password encoder for secure password hashing.
   *
   * @return BCryptPasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Creates CORS configuration for cross-origin requests.
   *
   * @return CorsConfigurationSource with CORS settings
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Allow specific origins
    configuration.setAllowedOriginPatterns(List.of("*"));

    // Allow specific HTTP methods
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // Allow specific headers
    configuration.setAllowedHeaders(
        Arrays.asList(
            "Authorization", "Content-Type", "X-Request-ID", "X-User-ID", "X-Session-ID"));

    // Allow credentials
    configuration.setAllowCredentials(true);

    // Set max age for preflight requests
    configuration.setMaxAge(DEFAULT_TOKEN_VALIDITY_SECONDS);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
