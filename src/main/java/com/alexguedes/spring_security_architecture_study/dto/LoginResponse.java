package com.alexguedes.spring_security_architecture_study.dto;

import java.util.List;

public record LoginResponse(
        String token,
        String username,
        List<String> roles
) {
}
