# postman-tests.md

# Spring Security Architecture Study — Postman Test Guide

Este documento descreve o roteiro de testes manuais da API para validar o fluxo de autenticação com **Spring Security**, **JWT** e **FilterChain**.

---

# 1. Objetivo

Este projeto foi construído com foco didático para estudar:

* funcionamento do **Spring Security**
* autenticação com **AuthenticationManager**
* geração e validação de **JWT**
* interceptação de requisições com **OncePerRequestFilter**
* preenchimento do **SecurityContext**
* comportamento de endpoints **públicos** e **protegidos**
* diferença entre:

    * requisição sem token
    * token inválido
    * token expirado
    * token válido

---

# 2. Base URL

```http
http://localhost:8080
```

---

# 3. Usuário de teste

Exemplo de usuário utilizado nos testes:

```json
{
  "username": "admin",
  "password": "123456"
}
```

> Ajuste conforme o usuário configurado no projeto.

---

# 4. Endpoints utilizados nos testes

## Auth

* `POST /api/auth/login`

## Demo / Security

* `GET /demo/admin`
* `GET /demo/user`
* `GET /demo/me`
* `GET /demo/debug`

> Os endpoints podem variar de acordo com a versão atual do controller. Atualize este documento se necessário.

---

# 5. Estrutura sugerida da coleção no Postman

## Collection

**spring-security-architecture**

### Requests

1. `01 - Login success`
2. `02 - Login invalid password`
3. `03 - Login invalid username`
4. `04 - Access /demo/admin without token`
5. `05 - Access /demo/admin with invalid token`
6. `06 - Access /demo/admin with valid token`
7. `07 - Access /demo/user with valid token`
8. `08 - Access /demo/me with valid token`
9. `09 - Access /demo/debug with valid token`

---

# 6. Variáveis de Collection recomendadas

No Postman, crie estas variáveis na collection:

| Variável   | Exemplo                 | Descrição                    |
| ---------- | ----------------------- | ---------------------------- |
| `baseUrl`  | `http://localhost:8080` | URL base da API              |
| `jwtToken` | vazio inicialmente      | token JWT retornado no login |

---

# 7. Testes passo a passo

---

# 7.1 Login com sucesso

## Request

**POST** `{{baseUrl}}/api/auth/login`

## Headers

```http
Content-Type: application/json
```

## Body

```json
{
  "username": "admin",
  "password": "123456"
}
```

## Resultado esperado

* Status `200 OK`
* Corpo contendo:

    * `token`
    * `username`
    * `roles`

## Exemplo de resposta esperada

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9....",
  "username": "admin",
  "roles": [
    "ROLE_ADMIN"
  ]
}
```

## Test script do Postman

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response contains JWT token", function () {
    const json = pm.response.json();
    pm.expect(json.token).to.exist;
    pm.expect(json.token).to.be.a("string");
    pm.expect(json.token.length).to.be.greaterThan(20);
});

pm.test("Response contains username", function () {
    const json = pm.response.json();
    pm.expect(json.username).to.eql("admin");
});

pm.test("Response contains roles", function () {
    const json = pm.response.json();
    pm.expect(json.roles).to.be.an("array");
});

const json = pm.response.json();
pm.collectionVariables.set("jwtToken", json.token);
console.log("JWT salvo na variável jwtToken");
```

## O que validar no log

### JwtAuthenticationFilter

Como o endpoint `/api/auth/login` é público e normalmente não recebe token:

```text
[JWT-FILTER] Request intercepted: POST /api/auth/login
[JWT-FILTER] No Bearer token found for POST /api/auth/login. Request will continue without authentication.
```

### AuthenticationApplicationService

```text
[AUTH-LOGIN] Login use case started
[AUTH-LOGIN] Authentication completed successfully
[AUTH-LOGIN] Authenticated principal resolved: admin
[AUTH-LOGIN] Granted authorities resolved: [ROLE_ADMIN]
[AUTH-LOGIN] JWT generated successfully for principal: admin
[AUTH-LOGIN] Login flow completed successfully
```

---

# 7.2 Login com senha inválida

## Request

**POST** `{{baseUrl}}/api/auth/login`

## Body

```json
{
  "username": "admin",
  "password": "senha-errada"
}
```

## Resultado esperado

* Status `401` ou o status configurado pelo seu tratamento global
* autenticação rejeitada

## Test script do Postman

```javascript
pm.test("Status code is 401 or 403", function () {
    pm.expect(pm.response.code).to.be.oneOf([401, 403]);
});
```

## O que validar no log

```text
[AUTH-LOGIN] Login use case started
[AUTH-LOGIN] Authentication failed: invalid credentials for username=admin
```

---

# 7.3 Login com username inexistente

## Request

**POST** `{{baseUrl}}/api/auth/login`

## Body

```json
{
  "username": "usuario-inexistente",
  "password": "123456"
}
```

## Resultado esperado

* Status `401` ou conforme tratamento global
* autenticação rejeitada

## Test script do Postman

```javascript
pm.test("Status code is 401 or 403", function () {
    pm.expect(pm.response.code).to.be.oneOf([401, 403]);
});
```

## O que validar no log

Pode ocorrer algo como:

```text
[AUTH-LOGIN] Authentication failed for username=usuario-inexistente. Cause: InternalAuthenticationServiceException
```

ou outra `AuthenticationException`, dependendo da implementação do `UserDetailsService`.

---

# 7.4 Acesso ao endpoint protegido sem token

## Request

**GET** `{{baseUrl}}/demo/admin`

## Headers

Nenhum header `Authorization`

## Resultado esperado

* Status `403` ou `401`, conforme sua configuração atual
* requisição não autenticada

## Test script do Postman

```javascript
pm.test("Status code is 401 or 403", function () {
    pm.expect(pm.response.code).to.be.oneOf([401, 403]);
});
```

## O que validar no log

```text
[JWT-FILTER] Request intercepted: GET /demo/admin
[JWT-FILTER] No Bearer token found for GET /demo/admin. Request will continue without authentication.
```

---

# 7.5 Acesso ao endpoint protegido com token inválido

## Request

**GET** `{{baseUrl}}/demo/admin`

## Headers

```http
Authorization: Bearer token-invalido-aqui
```

## Resultado esperado

* Status `401` ou `403`
* token rejeitado
* `SecurityContext` não deve ser preenchido

## Test script do Postman

```javascript
pm.test("Status code is 401 or 403", function () {
    pm.expect(pm.response.code).to.be.oneOf([401, 403]);
});
```

## O que validar no log

Exemplo:

```text
[JWT-FILTER] Request intercepted: GET /demo/admin
[JWT-FILTER] Bearer token detected for GET /demo/admin
[JWT-FILTER] Invalid JWT signature for request GET /demo/admin. Request will continue without authentication.
```

ou:

```text
[JWT-FILTER] Invalid JWT token for request GET /demo/admin. Request will continue without authentication.
```

---

# 7.6 Acesso ao endpoint protegido com token válido

## Pré-requisito

Executar antes o request **01 - Login success** para preencher a variável `{{jwtToken}}`.

## Request

**GET** `{{baseUrl}}/demo/admin`

## Headers

```http
Authorization: Bearer {{jwtToken}}
```

## Resultado esperado

* Status `200 OK`
* acesso permitido
* filtro JWT deve preencher o `SecurityContext`

## Test script do Postman

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});
```

## O que validar no log

```text
[JWT-FILTER] Request intercepted: GET /demo/admin
[JWT-FILTER] Bearer token detected for GET /demo/admin
[JWT-FILTER] Subject extracted from token: admin
[JWT-FILTER] UserDetails loaded successfully for subject: admin
[JWT-FILTER] SecurityContext populated successfully for subject: admin on request GET /demo/admin
```

---

# 7.7 Acesso ao endpoint /demo/user com token válido

## Request

**GET** `{{baseUrl}}/demo/user`

## Headers

```http
Authorization: Bearer {{jwtToken}}
```

## Resultado esperado

* Status `200 OK`
* requisição autenticada com sucesso

## Test script do Postman

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});
```

## O que validar no log

Mesmo padrão do `/demo/admin`, mudando apenas a URI:

```text
[JWT-FILTER] Request intercepted: GET /demo/user
[JWT-FILTER] Bearer token detected for GET /demo/user
[JWT-FILTER] Subject extracted from token: admin
[JWT-FILTER] UserDetails loaded successfully for subject: admin
[JWT-FILTER] SecurityContext populated successfully for subject: admin on request GET /demo/user
```

---

# 7.8 Acesso ao endpoint /demo/me com token válido

## Request

**GET** `{{baseUrl}}/demo/me`

## Headers

```http
Authorization: Bearer {{jwtToken}}
```

## Resultado esperado

* Status `200 OK`
* retorno com os dados do `Authentication` presente no `SecurityContext`

## Exemplo de resposta

```json
{
  "authorities": [
    {
      "authority": "ROLE_ADMIN"
    }
  ],
  "details": {
    "remoteAddress": "0:0:0:0:0:0:0:1",
    "sessionId": null
  },
  "authenticated": true,
  "principal": {
    "username": "admin",
    "authorities": [
      {
        "authority": "ROLE_ADMIN"
      }
    ],
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "enabled": true
  },
  "credentials": null,
  "name": "admin"
}
```

## Test script do Postman

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Authentication name should be admin", function () {
    const json = pm.response.json();
    pm.expect(json.name).to.eql("admin");
});

pm.test("Authentication must be authenticated", function () {
    const json = pm.response.json();
    pm.expect(json.authenticated).to.eql(true);
});
```

## O que esse endpoint prova

Esse endpoint é excelente para demonstrar que o `JwtAuthenticationFilter`:

1. extrai o token do header
2. valida o token
3. carrega o `UserDetails`
4. cria o `UsernamePasswordAuthenticationToken`
5. preenche o `SecurityContextHolder`
6. disponibiliza o usuário autenticado para o restante da aplicação

---

# 7.9 Acesso ao endpoint /demo/debug com token válido

## Request

**GET** `{{baseUrl}}/demo/debug`

## Headers

```http
Authorization: Bearer {{jwtToken}}
```

## Resultado esperado

* Status `200 OK`
* logs no controller mostrando o conteúdo do `Authentication`

## O que validar no log

Exemplo:

```text
Authentication Type: UsernamePasswordAuthenticationToken
Principal: admin
Authorities: [ROLE_ADMIN]
```

Esse endpoint é útil para enxergar o resultado final do processo de autenticação já dentro da camada web/controller.

---

# 8. Fluxo esperado da autenticação JWT no projeto

## 1. Login

Ao chamar `POST /api/auth/login`:

* o request chega na aplicação
* o `JwtAuthenticationFilter` intercepta a requisição
* como não há token, ele apenas libera a continuação da requisição
* o `AuthenticationApplicationService` delega a autenticação para o `AuthenticationManager`
* se as credenciais forem válidas, a aplicação gera o JWT e devolve no response

## 2. Requisição protegida com token

Ao chamar um endpoint protegido com `Authorization: Bearer <token>`:

* o `JwtAuthenticationFilter` intercepta a requisição
* extrai o token
* extrai o subject/username do token
* busca o usuário via `UserDetailsService`
* valida o token
* cria um `UsernamePasswordAuthenticationToken`
* popula o `SecurityContextHolder`
* a requisição segue autenticada até o controller

---

# 9. Casos de teste recomendados para demonstrar no README

Se quiser mostrar maturidade no repositório, vale listar explicitamente estes cenários:

## Casos positivos

* [x] login com credenciais válidas
* [x] geração de JWT com sucesso
* [x] acesso a endpoint protegido com token válido
* [x] `SecurityContext` preenchido corretamente
* [x] recuperação do usuário autenticado via endpoint `/demo/me`

## Casos negativos

* [x] login com senha inválida
* [x] login com usuário inexistente
* [x] acesso a endpoint protegido sem token
* [x] acesso a endpoint protegido com token inválido
* [x] acesso a endpoint protegido com assinatura JWT inválida
* [x] acesso negado quando não há autenticação válida

---

# 10. Sugestão de ordem de execução no Postman

Ordem ideal para demonstrar o fluxo:

1. `POST /api/auth/login` com senha errada
2. `POST /api/auth/login` com usuário inexistente
3. `POST /api/auth/login` com sucesso
4. `GET /demo/admin` sem token
5. `GET /demo/admin` com token inválido
6. `GET /demo/admin` com token válido
7. `GET /demo/user` com token válido
8. `GET /demo/debug` com token válido
9. `GET /demo/me` com token válido

Essa sequência mostra de forma didática:

* falha de autenticação
* sucesso de autenticação
* emissão de token
* falha de autorização sem token
* falha com token inválido
* sucesso com token válido
* leitura do `SecurityContext`

---

# 11. Observações importantes

## Sobre status HTTP

Dependendo da configuração do Spring Security, do `AuthenticationEntryPoint` e do tratamento de exceções, os cenários de falha podem retornar:

* `401 Unauthorized`
* `403 Forbidden`

Por isso, nos testes manuais iniciais, o mais importante é validar:

* se o comportamento está coerente com a configuração da aplicação
* se os logs mostram claramente o motivo da falha
* se o `SecurityContext` só é preenchido quando o token é válido

## Sobre logs

Os logs foram organizados em dois blocos principais:

### AuthenticationApplicationService

Responsável por logar o fluxo de login:

* início do caso de uso
* falhas de autenticação
* sucesso da autenticação
* geração do token

### JwtAuthenticationFilter

Responsável por logar o fluxo do token:

* interceptação da requisição
* ausência/presença de Bearer token
* extração do subject
* validação do token
* preenchimento do `SecurityContext`
* falhas por assinatura inválida, token malformado ou expirado

---

# 12. Sugestão de estrutura no repositório

```text
docs/
  postman-tests.md
postman/
  spring-security-architecture.postman_collection.json
README.md
```

---

# 13. Próximos testes que podem ser adicionados

Evoluções interessantes para esse laboratório:

* teste com **token expirado**
* teste com **roles diferentes** (`ROLE_ADMIN`, `ROLE_USER`)
* endpoint protegido com `@PreAuthorize`
* tratamento global de exceções com resposta padronizada
* `AuthenticationEntryPoint` customizado
* `AccessDeniedHandler` customizado
* testes automatizados com:

    * `MockMvc`
    * `Spring Security Test`
    * `JUnit 5`

---

# 14. Resumo final

Este roteiro valida o ciclo completo de autenticação baseado em JWT:

* login
* geração do token
* uso do token em endpoints protegidos
* preenchimento do `SecurityContext`
* tratamento de cenários inválidos

O objetivo não é apenas testar a API, mas também **entender visualmente o fluxo do Spring Security através dos logs e do comportamento dos endpoints**.
