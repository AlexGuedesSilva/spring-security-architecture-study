package com.alexguedes.spring_security_architecture_study.security.provider;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.info("AuthenticationProvider -> Authentication attempt for user: {}", username);

        if ("alex".equals(username) && "123456".equals(password)) {

            log.info("AuthenticationProvider -> Credentials validated successfully");

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            password,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );

            log.info("AuthenticationProvider -> Authentication object created for user: {}", username);

            return auth;
        }

        log.warn("AuthenticationProvider -> Invalid credentials for user: {}", username);

        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}