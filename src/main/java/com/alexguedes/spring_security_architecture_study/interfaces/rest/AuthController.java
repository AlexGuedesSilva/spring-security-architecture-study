package com.alexguedes.spring_security_architecture_study.interfaces.rest;

import com.alexguedes.spring_security_architecture_study.application.dto.LoginRequest;
import com.alexguedes.spring_security_architecture_study.application.dto.LoginResponse;
import com.alexguedes.spring_security_architecture_study.application.usecase.AuthenticationUserCase;
import com.alexguedes.spring_security_architecture_study.infraestructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final AuthenticationUserCase  authenticationUserCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        return ResponseEntity.ok(authenticationUserCase.execute(request));
    }
}