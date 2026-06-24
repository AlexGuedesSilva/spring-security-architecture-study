package com.alexguedes.spring_security_architecture_study.interfaces.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/demo")
public class SecurityDemoController {

    @GetMapping("/debug")
    public String debug() {

        Authentication auth =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        log.info("Authentication Type: {}",
                auth.getClass().getSimpleName());

        log.info("Principal: {}",
                auth.getName());

        log.info("Authorities: {}",
                auth.getAuthorities());

        return "OK";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userEndpoint() {
        return "USER";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "ADMIN";
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();

        return Map.of(
                "username", user.getUsername(),
                "roles", user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList(),
                "authenticated", authentication.isAuthenticated(),
                "authenticationType", authentication.getClass().getSimpleName()
        );
    }
}
