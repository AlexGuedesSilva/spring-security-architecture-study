package com.alexguedes.spring_security_architecture_study.infraestructure.security.service;

import com.alexguedes.spring_security_architecture_study.domain.model.Role;
import com.alexguedes.spring_security_architecture_study.domain.model.User;
import com.alexguedes.spring_security_architecture_study.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) {

        User user =
                repository.findByUsername(username)
                        .orElseThrow();

        String[] roles =
                user.getRoles()
                        .stream()
                        .map(Role::name)
                        .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .roles(roles)
                .build();
    }
}