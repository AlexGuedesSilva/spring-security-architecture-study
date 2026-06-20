package com.alexguedes.spring_security_architecture_study.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "Private";
    }

}
