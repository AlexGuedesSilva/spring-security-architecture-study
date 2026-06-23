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

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @Override
    public LoginResponse execute(LoginRequest request) {

        log.info("=======================================");
        log.info("AuthController -> Login started");
        log.info("Username received: {}", request.username());

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        UserDetails user =
                (UserDetails) authentication.getPrincipal();

        log.info("User authenticated: {}",
                user.getUsername());

        List<String> roles =
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

        log.info("Authorities found: {}", roles);

        String token =
                jwtService.generateToken(user);

        log.info("JWT generated successfully");

        log.info("Authentication class: {}",
                authentication.getClass().getSimpleName());

        log.info("Principal: {}",
                authentication.getPrincipal());

        log.info("Authorities: {}",
                authentication.getAuthorities());

        log.info("=======================================");

        return new LoginResponse(token, user.getUsername(), roles);
    }
}
