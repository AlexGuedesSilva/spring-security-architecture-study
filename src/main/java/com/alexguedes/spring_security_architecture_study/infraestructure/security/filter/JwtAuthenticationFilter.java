package com.alexguedes.spring_security_architecture_study.infraestructure.security.filter;

import com.alexguedes.spring_security_architecture_study.infraestructure.security.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String LOG_PREFIX = "[JWT-FILTER]";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String method = request.getMethod();
        final String uri = request.getRequestURI();

        log.debug("{} Request intercepted: {} {}", LOG_PREFIX, method, uri);

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.debug("{} No Bearer token found for {} {}. Request will continue without authentication.",
                    LOG_PREFIX, method, uri);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(BEARER_PREFIX.length());
            log.debug("{} Bearer token detected for {} {}", LOG_PREFIX, method, uri);

            String username = jwtService.extractUsername(token);
            log.debug("{} Subject extracted from token: {}", LOG_PREFIX, username);

            if (username == null) {
                log.warn("{} Token subject could not be extracted for request {} {}. Request will continue without authentication.",
                        LOG_PREFIX, method, uri);
                filterChain.doFilter(request, response);
                return;
            }

            Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();

            if (currentAuthentication != null) {
                log.debug("{} SecurityContext already contains authentication for principal: {} on request {} {}. JWT validation will be skipped.",
                        LOG_PREFIX, currentAuthentication.getName(), method, uri);
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.debug("{} UserDetails loaded successfully for subject: {}", LOG_PREFIX, userDetails.getUsername());

            if (!jwtService.isTokenValid(token, username)) {
                log.warn("{} Token validation failed for subject: {} on request {} {}. Request will continue without authentication.",
                        LOG_PREFIX, username, method, uri);
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.info("{} SecurityContext populated successfully for subject: {} on request {} {}",
                    LOG_PREFIX, username, method, uri);

        } catch (SignatureException ex) {
            log.warn("{} Invalid JWT signature for request {} {}. Request will continue without authentication.",
                    LOG_PREFIX, method, uri);

        } catch (ExpiredJwtException ex) {
            log.warn("{} Expired JWT token for request {} {}. Request will continue without authentication.",
                    LOG_PREFIX, method, uri);

        } catch (MalformedJwtException ex) {
            log.warn("{} Malformed JWT token for request {} {}. Request will continue without authentication.",
                    LOG_PREFIX, method, uri);

        } catch (JwtException ex) {
            log.warn("{} Invalid JWT token for request {} {}. Request will continue without authentication.",
                    LOG_PREFIX, method, uri);
            log.debug("{} JWT exception details: {}", LOG_PREFIX, ex.getMessage(), ex);

        } catch (Exception ex) {
            log.error("{} Unexpected error while processing JWT for request {} {}: {}",
                    LOG_PREFIX, method, uri, ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
        log.debug("{} Request processing completed: {} {}", LOG_PREFIX, method, uri);
    }
}