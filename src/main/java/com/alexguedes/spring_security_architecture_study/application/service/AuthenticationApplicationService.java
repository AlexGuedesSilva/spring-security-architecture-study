package com.alexguedes.spring_security_architecture_study.application.service;

import com.alexguedes.spring_security_architecture_study.application.dto.LoginRequest;
import com.alexguedes.spring_security_architecture_study.application.dto.LoginResponse;
import com.alexguedes.spring_security_architecture_study.application.usecase.AuthenticationUserCase;
import com.alexguedes.spring_security_architecture_study.infraestructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationApplicationService implements AuthenticationUserCase {

    private static final String LOG_PREFIX = "[AUTH-LOGIN]";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public LoginResponse execute(LoginRequest request) {
        final String username = request.username();

        log.info("{} Login use case started", LOG_PREFIX);
        log.debug("{} Username received: {}", LOG_PREFIX, username);
        log.debug("{} Delegating authentication request to AuthenticationManager", LOG_PREFIX);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            log.info("{} Authentication completed successfully", LOG_PREFIX);
            log.debug("{} Authentication implementation: {}",
                    LOG_PREFIX,
                    authentication.getClass().getSimpleName());

            UserDetails user = (UserDetails) authentication.getPrincipal();
            String principalUsername = user.getUsername();

            List<String> roles = user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            log.debug("{} Authenticated principal resolved: {}", LOG_PREFIX, principalUsername);
            log.debug("{} Granted authorities resolved: {}", LOG_PREFIX, roles);
            log.debug("{} Generating JWT token for authenticated principal: {}", LOG_PREFIX, principalUsername);

            String token = jwtService.generateToken(user);

            log.info("{} JWT generated successfully for principal: {}", LOG_PREFIX, principalUsername);
            log.info("{} Login flow completed successfully", LOG_PREFIX);

            return new LoginResponse(token, principalUsername, roles);

        } catch (BadCredentialsException ex) {
            log.warn("{} Authentication failed: invalid credentials for username={}", LOG_PREFIX, username);
            throw ex;

        } catch (LockedException ex) {
            log.warn("{} Authentication failed: user account is locked for username={}", LOG_PREFIX, username);
            throw ex;

        } catch (DisabledException ex) {
            log.warn("{} Authentication failed: user account is disabled for username={}", LOG_PREFIX, username);
            throw ex;

        } catch (AuthenticationException ex) {
            log.warn("{} Authentication failed for username={}. Cause: {}",
                    LOG_PREFIX, username, ex.getClass().getSimpleName());
            throw ex;

        } catch (Exception ex) {
            log.error("{} Unexpected error during login flow for username={}: {}",
                    LOG_PREFIX, username, ex.getMessage(), ex);
            throw ex;
        }
    }
}