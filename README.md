# Spring Security Architecture Study (JWT + Custom Authentication Flow)

This project is a hands-on study of Spring Security internals, focusing on how authentication and authorization work under the hood.
It demonstrates the evolution from default security configuration to a fully custom JWT-based authentication system.

## 🚀 Project Goals

* Understand Spring Security filter chain
* Implement custom authentication logic
* Replace default authentication with JWT
* Explore AuthenticationProvider strategy pattern
* Learn how SecurityContextHolder manages authenticated users
* Visualize the complete authentication flow using logs

---

## 🧠 What This Project Covers

### 1. Spring Security Basics
   * Default Spring Security configuration
   * Automatic authentication setup
   * Introduction to SecurityFilterChain

---

### 2. Custom Security Configuration
   * Manual SecurityFilterChain configuration
   * Endpoint authorization rules
   * Public vs protected routes
   * HTTP Basic authentication

---

### 3. Authentication Architecture
   * Custom UserDetailsService
   * In-memory authentication
   * Password encryption using BCryptPasswordEncoder

---

### 4. Custom Authentication Provider
   * Implementation of AuthenticationProvider
   * Strategy Pattern in Spring Security:
      * DaoAuthenticationProvider
      * JwtAuthenticationProvider
      * CustomAuthenticationProvider

---

### 5. Security Context Understanding
   * How Spring stores authenticated users
   * SecurityContextHolder usage
   * ThreadLocal-based request authentication
   * /me endpoint to expose current user

---

### 6. JWT Authentication (Stateless Security)
   * JWT token generation and validation
   * JwtService implementation
   * JwtAuthenticationFilter using OncePerRequestFilter
   * Stateless authentication replacing HTTP sessions
   * Bearer token-based requests

---

### 7. Security Flow Logging (Observability)
   * Added logs to trace authentication pipeline
   * Visual understanding of Spring Security execution flow:

Request Received
↓
Security Filter Chain
↓
AuthenticationManager
↓
AuthenticationProvider
↓
UserDetailsService
↓
PasswordEncoder
↓
Authentication Success
↓
SecurityContextHolder
↓
Controller

---

## 🔐 Authentication Flow (JWT)

POST /login
↓
AuthenticationManager
↓
AuthenticationProvider
↓
JWT Generated (JwtService)
↓
Client receives token

Request /api/**
↓
Authorization: Bearer <token>
↓
JwtAuthenticationFilter
↓
SecurityContextHolder
↓
Controller

---

## Tecnologias

* Java 21
* Spring Boot 3
* Spring Security 6
* Maven
* JUnit 5
* MockMvc
* JWT
* OAuth2 Resource Server

---

## 📌 Concepts Demonstrated

### Security Filter Chain

RResponsible for intercepting all HTTP requests and applying configured security filters.

### AuthenticationManager

Coordinates the authentication process by delegating to a compatible AuthenticationProvider.

### AuthenticationProvider

Performs credential validation.

Examples:

* Username and password
* JWT tokens
* OAuth2
* API Keys

### UserDetailsService

Responsible for loading user data.

Examples:

* Database
* LDAP
* External API

### SecurityContextHolder

Stores the current authentication for the request.

It allows access to:

* Authentication user
* Roles
* Authorities
* Claims

---

## ⚙️ Project Features

* Login with username and password
* JWT authentication
* Protected endpoints
* Roles and authorities
* Integration tests
* Logs demonstrating each step of the security flow
* Explanatory diagrams

---

## 🧠 Key Learnings

* Spring Security is based on a filter chain architecture
* Authentication is handled via AuthenticationManager + Providers
* AuthenticationProvider follows the Strategy Pattern
* SecurityContextHolder stores authentication per request (ThreadLocal)
* JWT enables stateless authentication
* Filters are the core mechanism behind request security

---

## 👨‍💻 Author

Alex Guedes

Study project focused on mastering Spring Security architecture and JWT-based authentication flows.
