package com.alexguedes.spring_security_architecture_study.infraestructure.security.filter;

import com.alexguedes.spring_security_architecture_study.infraestructure.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("JWT Filter -> Request intercepted: {}", request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        log.info("JWT Filter -> Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("JWT Filter -> No Bearer token found, skipping authentication");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        log.info("JWT Filter -> Token received ({} chars)", token.length());

        String username = jwtService.extractUsername(token);
        log.info("JWT Filter -> Username extracted from token: {}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            log.info("JWT Filter -> No existing authentication, validating token");

            UserDetails user = userDetailsService.loadUserByUsername(username);
            log.info("JWT Filter -> User loaded: {}", user.getUsername());

            if (jwtService.isTokenValid(token, username)) {

                log.info("JWT Filter -> Token valid, setting SecurityContext");

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Current Principal: {}", authToken.getName());
                log.info("Current Authorities: {}", authToken.getAuthorities());

                log.info("JWT Filter -> Authentication stored successfully for user: {}", username);

            } else {
                log.warn("JWT Filter -> Invalid token for user: {}", username);
            }
        } else {
            log.info("JWT Filter -> Authentication already exists or username is null");
        }

        filterChain.doFilter(request, response);

        log.info("JWT Filter -> Request processing completed");
    }
}
