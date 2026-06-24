package com.alexguedes.spring_security_architecture_study.application.service;

import com.alexguedes.spring_security_architecture_study.application.dto.LoginRequest;
import com.alexguedes.spring_security_architecture_study.application.dto.LoginResponse;
import com.alexguedes.spring_security_architecture_study.application.usecase.AuthenticationUserCase;
import com.alexguedes.spring_security_architecture_study.infraestructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

        log.info("{} Login use case started", LOG_PREFIX);
        log.info("{} Username received: {}", LOG_PREFIX, request.username());
        log.info("{} Delegating authentication request to AuthenticationManager", LOG_PREFIX);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        log.info("{} Authentication completed successfully", LOG_PREFIX);
        log.info("{} Authentication implementation: {}",
                LOG_PREFIX,
                authentication.getClass().getSimpleName());

        UserDetails user = (UserDetails) authentication.getPrincipal();

        log.info("{} Authenticated principal resolved: {}", LOG_PREFIX, user.getUsername());

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        log.info("{} Granted authorities resolved: {}", LOG_PREFIX, roles);
        log.info("{} Generating JWT token for authenticated principal: {}", LOG_PREFIX, user.getUsername());

        String token = jwtService.generateToken(user);

        log.info("{} JWT generated successfully for principal: {}", LOG_PREFIX, user.getUsername());
        log.info("{} Login flow completed successfully", LOG_PREFIX);

        return new LoginResponse(token, user.getUsername(), roles);
    }
}
