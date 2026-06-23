package com.alexguedes.spring_security_architecture_study.infraestructure.security.persistence;

import com.alexguedes.spring_security_architecture_study.domain.model.Role;
import com.alexguedes.spring_security_architecture_study.domain.model.User;
import com.alexguedes.spring_security_architecture_study.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class InMemoryUserRepository
        implements UserRepository {

    private final Map<String, User> users =
            Map.of(

                    "admin",
                    new User(
                            "admin",
                            "password",
                            Set.of(Role.ADMIN)
                    ),

                    "user",
                    new User(
                            "user",
                            "password",
                            Set.of(Role.USER)
                    )
            );

    @Override
    public Optional<User> findByUsername(
            String username
    ) {
        return Optional.ofNullable(users.get(username));
    }
}
