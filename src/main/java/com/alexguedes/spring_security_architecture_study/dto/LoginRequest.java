package com.alexguedes.spring_security_architecture_study.dto;

public record LoginRequest(
        String username,
        String password
) {
}
