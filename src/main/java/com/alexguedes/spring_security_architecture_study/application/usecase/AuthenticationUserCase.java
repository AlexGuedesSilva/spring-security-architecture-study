package com.alexguedes.spring_security_architecture_study.application.usecase;

import com.alexguedes.spring_security_architecture_study.application.dto.LoginRequest;
import com.alexguedes.spring_security_architecture_study.application.dto.LoginResponse;

public interface AuthenticationUserCase {

    LoginResponse execute(LoginRequest request);
}
