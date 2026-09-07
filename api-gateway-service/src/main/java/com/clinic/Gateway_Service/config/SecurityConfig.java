package com.clinic.Gateway_Service.config;

import com.clinic.Gateway_Service.filter.RateLimiterFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${VERCEL_FRONTEND_URL:https://omnihis-frontend.vercel.app}")
    private String vercelFrontendUrl;

    @Autowired
    private RateLimiterFilter rateLimiterFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(rateLimiterFilter, BearerTokenAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/actuator/**", 
                    "/fallback/**",
                    "/api/v1/webhook/**", 
                    "/api/webhook/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/*/v3/api-docs/**",
                    "/*/swagger-ui/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("ALERTA DE SEGURIDAD: Intento de acceso no autorizado bloqueado por el Gateway. Metodo: {}, URI: {}, IP: {}, Causa: {}",
                            request.getMethod(), request.getRequestURI(), extractClientIp(request), authException.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("""
                        {
                            "status": 401,
                            "error": "Unauthorized",
                            "message": "ALERTA DE SEGURIDAD: Intento de acceso no autorizado bloqueado por el Gateway",
                            "path": "%s"
                        }
                    """.formatted(request.getRequestURI()));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("ALERTA DE SEGURIDAD: Intento de acceso no autorizado bloqueado por el Gateway. Metodo: {}, URI: {}, IP: {}, Causa: {}",
                            request.getMethod(), request.getRequestURI(), extractClientIp(request), accessDeniedException.getMessage());
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("""
                        {
                            "status": 403,
                            "error": "Forbidden",
                            "message": "ALERTA DE SEGURIDAD: Intento de acceso no autorizado bloqueado por el Gateway",
                            "path": "%s"
                        }
                    """.formatted(request.getRequestURI()));
                })
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("ALERTA DE SEGURIDAD: Intento de acceso no autorizado bloqueado por el Gateway. Metodo: {}, URI: {}, IP: {}, Causa: {}",
                            request.getMethod(), request.getRequestURI(), extractClientIp(request), authException.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("""
                        {
                            "status": 401,
                            "error": "Unauthorized",
                            "message": "ALERTA DE SEGURIDAD: Intento de acceso no autorizado bloqueado por el Gateway",
                            "path": "%s"
                        }
                    """.formatted(request.getRequestURI()));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("ALERTA DE SEGURIDAD: Intento de acceso no autorizado bloqueado por el Gateway. Metodo: {}, URI: {}, IP: {}, Causa: {}",
                            request.getMethod(), request.getRequestURI(), extractClientIp(request), accessDeniedException.getMessage());
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("""
                        {
                            "status": 403,
                            "error": "Forbidden",
                            "message": "ALERTA DE SEGURIDAD: Intento de acceso no autorizado bloqueado por el Gateway",
                            "path": "%s"
                        }
                    """.formatted(request.getRequestURI()));
                })
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        List<String> allowedOrigins = new ArrayList<>(List.of(
            "http://localhost:5173",
            "http://127.0.0.1:5173",
            "http://localhost:3000"
        ));
        
        if (vercelFrontendUrl != null && !vercelFrontendUrl.isBlank() && !allowedOrigins.contains(vercelFrontendUrl)) {
            allowedOrigins.add(vercelFrontendUrl);
        }
        
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(Arrays.asList("X-RateLimit-Limit", "X-RateLimit-Remaining", "Retry-After"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        String remoteAddr = request.getRemoteAddr();
        return (remoteAddr != null) ? remoteAddr : "unknown";
    }
}
