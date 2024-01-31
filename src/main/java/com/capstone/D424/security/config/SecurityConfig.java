package com.capstone.D424.security.config;


import com.capstone.D424.security.filter.JWTTokenValidatorFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JWTTokenValidatorFilter tokenValidatorFilter;
    private final LogoutHandler logoutHandler;
    private final Environment env;
    private String[] adminPatterns = {"/api/v1/admin/**"};
    private String[] userPatterns = {"/api/v1/profile", "/api/v1/profile/**", "/api/v1/search/**", "/api/v1/user", "/api/v1/report/extended/**"};
    private String[] freeUserPatterns = {"/free/**", "/demo/**"};

    @Bean
    public CorsConfigurationSource corsConfigSource() {
        // Fetch the dynamic origin from the environment
        String dynamicOrigin = env.getProperty("spring.client");

        // Create a list of origins including the dynamic one
        List<String> origins = new ArrayList<>(List.of("http://localhost:3000"));
        if (dynamicOrigin != null && !dynamicOrigin.isEmpty()) {
            origins.add(dynamicOrigin);
        }
        log.info("SECURITY CONFIG: CORS CONFIG DYNAMIC ORIGIN ALLOWED:" + dynamicOrigin);
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(origins);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Origin", "Accept", "x-requested-with", "access-control-allow-credentials", "access-control-allow-methods", "access-control-allow-origin"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(tokenValidatorFilter, BasicAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(freeUserPatterns).hasAnyAuthority("ROLE_USER_FREE", "ROLE_USER_PAID", "ROLE_ADMIN", "ROLE_ANONYMOUS")
                        .requestMatchers(userPatterns).hasAnyAuthority("ROLE_USER_PAID", "ROLE_ADMIN")
                        .requestMatchers(adminPatterns).hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/v1/auth/**", "/api/v1/public/**", "/error").permitAll())
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()));
        return http.build();
    }

}
