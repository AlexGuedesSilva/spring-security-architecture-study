package com.alexguedes.spring_security_architecture_study.infraestructure.security.config;

import com.alexguedes.spring_security_architecture_study.infraestructure.security.filter.JwtAuthenticationFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Log4j2
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private static final String LOG_PREFIX = "[SECURITY-BOOT]";

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**"
    };

    private static final String[] AUTHENTICATED_ENDPOINTS = {
            "/demo/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {

        log.info("{} Starting SecurityFilterChain configuration", LOG_PREFIX);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS ).permitAll()
                        .requestMatchers("/demo/public/**").permitAll()
                        .requestMatchers(AUTHENTICATED_ENDPOINTS).authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("{} CSRF disabled for stateless JWT-based authentication", LOG_PREFIX);
        log.info("{} Session creation policy configured as STATELESS", LOG_PREFIX);
        log.info("{} Public endpoints configured: {}", LOG_PREFIX, String.join(", ", PUBLIC_ENDPOINTS));
        log.info("{} Authenticated endpoints configured: {} + any other request",
                LOG_PREFIX,
                String.join(", ", AUTHENTICATED_ENDPOINTS));
        log.info("{} JWT authentication filter registered before {}",
                LOG_PREFIX,
                UsernamePasswordAuthenticationFilter.class.getSimpleName());

        SecurityFilterChain filterChain = http.build();

        log.info("{} SecurityFilterChain built successfully", LOG_PREFIX);

        return filterChain;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        log.info("{} Resolving AuthenticationManager from AuthenticationConfiguration", LOG_PREFIX);

        AuthenticationManager authenticationManager = configuration.getAuthenticationManager();

        log.info("{} AuthenticationManager resolved successfully", LOG_PREFIX);

        return authenticationManager;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        log.info("{} Initializing PasswordEncoder with implementation: {}",
                LOG_PREFIX,
                BCryptPasswordEncoder.class.getSimpleName());

        return new BCryptPasswordEncoder();
    }
}