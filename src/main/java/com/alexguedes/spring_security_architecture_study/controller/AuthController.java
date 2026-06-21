package com.alexguedes.spring_security_architecture_study.controller;

import com.alexguedes.spring_security_architecture_study.dto.LoginRequest;
import com.alexguedes.spring_security_architecture_study.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>  login(
            @RequestBody LoginRequest request
    ) {

        log.info("Login request received for user: {}",
                request.username());

        return ResponseEntity.ok(
                new LoginResponse("TOKEN_AQUI")
        );
    }


    
}
