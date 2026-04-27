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

/**
 * Security Configuration Matrix.
 * Matriz de Configuración de Seguridad.
 * 
 * Establishes stateless HTTP security and manages JWT processing.
 * Establece la seguridad HTTP sin estado y gestiona el procesamiento de JWT.
 */
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
     * Conversor de Autenticación JWT Personalizado.
     *
     * The default Spring Security JWT converter reads authorities from standard
     * 'scope' or 'authorities' claims. However, Supabase stores the user role
     * inside the nested 'user_metadata.role' claim of the JWT payload.
     * 
     * El conversor JWT por defecto de Spring Security lee las autoridades de los
     * claims estándar 'scope' o 'authorities'. Sin embargo, Supabase almacena el rol
     * del usuario dentro del claim anidado 'user_metadata.role' del payload JWT.
     *
     * This converter bridges that gap by extracting the role from the nested
     * claim and converting it into a GrantedAuthority that Spring Security can
     * evaluate in authorization rules like hasAuthority("ADMIN").
     * 
     * Este conversor cierra esa brecha extrayendo el rol del claim anidado y
     * convirtiéndolo en un GrantedAuthority que Spring Security puede evaluar en
     * reglas de autorización como hasAuthority("ADMIN").
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Extract the nested user_metadata claim map
            // Extraer el mapa del claim anidado user_metadata
            Map<String, Object> userMetadata = jwt.getClaimAsMap("user_metadata");
            if (userMetadata == null) return List.of();

            // Read the role field from the metadata
            // Leer el campo de rol desde los metadatos
            Object role = userMetadata.get("role");
            if (role == null) return List.of();

            // Convert the role string into a GrantedAuthority
            // Convertir la cadena del rol en un GrantedAuthority
            return List.of(new SimpleGrantedAuthority(role.toString()));
        });
        return jwtConverter;
    }
}
