package com.quickcommerce.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        var keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        var key = new SecretKeySpec(keyBytes, "HmacSHA256");
        var decoder = NimbusReactiveJwtDecoder.withSecretKey(key).build();

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeExchange(ex -> ex
                .pathMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(decoder)))
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200", "http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
