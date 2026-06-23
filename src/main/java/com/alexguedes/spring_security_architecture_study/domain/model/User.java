package com.alexguedes.spring_security_architecture_study.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class User {

    private String username;
    private String password;
    private Set<Role> roles;

}
