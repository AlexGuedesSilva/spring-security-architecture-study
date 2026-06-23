package com.alexguedes.spring_security_architecture_study.application.dto;

public record LoginRequest(
        String username,
        String password
) {
}
