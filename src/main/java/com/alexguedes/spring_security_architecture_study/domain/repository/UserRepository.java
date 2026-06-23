package com.alexguedes.spring_security_architecture_study.domain.repository;

import com.alexguedes.spring_security_architecture_study.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

}