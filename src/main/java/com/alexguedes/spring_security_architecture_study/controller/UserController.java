package com.alexguedes.spring_security_architecture_study.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/me")
    public Object me() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return Map.of(
                "username",
                auth.getName(),
                "authorities",
                auth.getAuthorities()
        );
    }
}
