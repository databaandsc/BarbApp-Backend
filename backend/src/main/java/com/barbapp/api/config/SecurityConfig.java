package com.barbapp.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Global Security Configuration for the API.
 * Defines access rules, CORS policies, and configures the application
 * to act as an OAuth2 Resource Server validating JWTs from Supabase.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the main security filter chain.
     *
     * @param http The HttpSecurity builder provided by Spring.
     * @return SecurityFilterChain The configured security chain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Cross-Site Request Forgery) protection
                // Standard practice for REST APIs where the browser isn't managing sessions
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Enable CORS (Cross-Origin Resource Sharing)
                // Allows our React Native frontend to communicate with this backend
                .cors(Customizer.withDefaults())

                // 3. Define Authorization Rules
                .authorizeHttpRequests(authz -> authz
                        // All endpoints under /api/ explicitly require authentication
                        .requestMatchers("/api/**").authenticated()
                        // Any other request must also be authenticated by default
                        .anyRequest().authenticated()
                )

                // 4. Configure as OAuth2 Resource Server
                // Tells Spring to look for a Bearer token in the 'Authorization' header
                // and validate it as a JWT against the Issuer URI defined in application.yml
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }

    /**
     * Configures global CORS policies.
     * This setup allows requests from any origin, which is suitable for development
     * or mobile apps (React Native) that don't have a fixed origin domain.
     *
     * @return CorsConfigurationSource defining the allowed origins, methods, and headers.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow all origins (standard for mobile apps)
        configuration.setAllowedOrigins(List.of("*"));

        // Allow common HTTP methods used in REST
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow standard headers required for JWT and JSON communication
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
