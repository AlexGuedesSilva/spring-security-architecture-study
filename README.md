# Spring Security Architecture Study

Projeto educacional criado para demonstrar o funcionamento interno do Spring Security de forma prática e visual.

## Objetivo

O objetivo deste projeto é explicar como o Spring Security processa uma autenticação internamente, desde a chegada da requisição até o armazenamento do usuário autenticado no SecurityContext.

Muitos desenvolvedores utilizam o framework apenas por configuração, sem compreender os componentes responsáveis pela autenticação e autorização.

Este repositório busca preencher essa lacuna.

---

## Fluxo da Autenticação

Request
↓
Security Filter Chain
↓
AuthenticationManager
↓
AuthenticationProvider
↓
UserDetailsService
↓
SecurityContextHolder
↓
Authenticated User

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

## Assuntos Demonstrados

### Security Filter Chain

Responsável por interceptar todas as requisições HTTP e aplicar os filtros de segurança configurados.

### AuthenticationManager

Coordena o processo de autenticação delegando para um AuthenticationProvider compatível.

### AuthenticationProvider

Executa a validação das credenciais recebidas.

Exemplo:

* Username e senha
* JWT
* OAuth2
* API Keys

### UserDetailsService

Responsável por carregar os dados do usuário.

Exemplo:

* Banco de dados
* LDAP
* API externa

### SecurityContextHolder

Armazena a autenticação atual da requisição.

Permite acessar:

* Usuário autenticado
* Roles
* Authorities
* Claims

---

## Funcionalidades do Projeto

* Login com usuário e senha
* Autenticação JWT
* Endpoints protegidos
* Roles e Authorities
* Testes de integração
* Logs demonstrando cada etapa do fluxo
* Diagramas explicativos

---

## O que você aprenderá

Ao finalizar este projeto você será capaz de:

* Entender a arquitetura interna do Spring Security
* Implementar autenticação baseada em JWT
* Customizar filtros de segurança
* Criar AuthenticationProviders personalizados
* Trabalhar com UserDetailsService
* Compreender OAuth2 Resource Server
* Depurar problemas de autenticação com mais facilidade

---

Projeto criado para fins educacionais e portfólio.
