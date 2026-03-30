package com.barbapp.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.http.SessionCreationPolicy;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                // Configure session management to be stateless.
                // Configurar la gestión de sesiones para que sea sin estado.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Permit access to Swagger documentation and root path.
                        // Permitir el acceso a la documentación de Swagger y a la ruta raíz.
                        .requestMatchers("/", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/appointments/admin/**").hasAuthority("ADMIN")
                        // Require authentication for any other request.
                        // Requerir autenticación para cualquier otra petición.
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Add apikey and X-Client-Info headers required by Supabase client.
        // Añadir cabeceras apikey y X-Client-Info requeridas por el cliente de Supabase.
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "apikey", "X-Client-Info"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    /**
     * Custom JWT Authentication Converter.
     *
     * The default Spring Security JWT converter reads authorities from standard
     * 'scope' or 'authorities' claims. However, Supabase stores the user role
     * inside the nested 'user_metadata.role' claim of the JWT payload.
     *
     * This converter bridges that gap by extracting the role from the nested
     * claim and converting it into a GrantedAuthority that Spring Security can
     * evaluate in authorization rules like hasAuthority("ADMIN").
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Extract the nested user_metadata claim map
            Map<String, Object> userMetadata = jwt.getClaimAsMap("user_metadata");
            if (userMetadata == null) return List.of();

            // Read the role field from the metadata
            Object role = userMetadata.get("role");
            if (role == null) return List.of();

            // Convert the role string into a GrantedAuthority
            return List.of(new SimpleGrantedAuthority(role.toString()));
        });
        return jwtConverter;
    }
}
