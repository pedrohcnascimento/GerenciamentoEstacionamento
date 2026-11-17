# Inpark — Gerenciamento de Estacionamento (Backend)

API REST em **Java 21 + Spring Boot** para gerenciamento e reserva de vagas de estacionamento da plataforma **Inpark**, estruturada conforme a especificação **Global Solution 2026/2 · CitizenTech FIAP**.

---

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot** (WebMVC, Data JPA, Validation, Security)
- **JWT (`jjwt 0.11.5`)** e **BCrypt** para autenticação e controle de acesso baseado em papéis (`ADMIN`, `DONO`, `GERENTE`, `CLIENTE`)
- **Springdoc OpenAPI / Swagger UI** (`/swagger-ui/index.html`, `/v3/api-docs`)
- **MySQL** (produção/desenvolvimento) e **H2** (testes automatizados)
- **Lombok**
- **JUnit 5, Mockito & Spring MockMvc** (testes unitários e de integração)

---

## Estrutura de Pacotes

`inpark.gerenciamentoestacionamento`
- `application/`
  - `dto/requests/` — DTOs de entrada (`AcessoRequest`, `AvaliacaoRequest`, `CarroRequest`, `EstacionamentoRequest`, `ReservaRequest`, `ValorRequest`, `ClienteRequest`, `DonoRequest`, `GerenteRequest`, `UsuarioRequest`, `UsuarioLoginRequest`, `UsuarioLoginResponse`)
  - `dto/response/` — DTOs de saída (`AcessoResponse`, `AvaliacaoResponse`, `CarroResponse`, `EstacionamentoResponse`, `ReservaResponse`, `ReservaResponseResumo`, `ValorResponse`, `ClienteResponse`, `DonoResponse`, `GerenteResponse`, `UsuarioResponse`)
  - `mappers/` — Conversores entre entidades e DTOs
  - `services/` — Regras de aplicação (`AcessoService`, `AuthService`, `AvaliacaoService`, `CarroService`, `EstacionamentoService`, `ReservaService`, `ValorService`, `ClienteService`, `DonoService`, `GerenteService`)
- `infraestructure/`
  - `config/` — Configurações de CORS, Method Security e OpenAPI/Swagger
  - `handler/` — Tratamento global de exceções (`GlobalExceptionHandler`)
  - `repositories/` — Repositórios Spring Data JPA
  - `security/` — `SecurityConfig`, `JwtService`, `JwtAuthenticationFilter`, `UsuarioDetailService`
- `infraestructure_ui/`
  - `controller/` — Controllers REST (`/auth`, `/estacionamento`, `/valor`, `/reserva`, `/acesso`, `/avaliacao`, `/carro`, `/cliente`, `/dono`, `/gerente`)
  - `handlers/` — Handlers de integração IoT (`AssinanteHandler`, `PublicadorService`)
- `model/`
  - `entities/` — Entidades de domínio (`Usuario`, `Cliente`, `DonoEstacionamento`, `Gerente`, `Estacionamento`, `Carro`, `Reserva`, `Valor`, `Acesso`, `Avaliacao`)
  - `enums/` — Enums (`Role`, `TipoDeUsuario`, `StatusReserva`, `Cobranca`, `Periodo`, `Metodo`)
  - `exceptions/` — Exceções de domínio
  - `service/` — Validações de domínio (`ValidadorAvaliacao`)

---

## Endpoints Implementados

| Recurso | Rotas |
|---|---|
| Autenticação | `POST /auth/login`, `POST /auth/me`, `POST /auth/criar-admin` |
| Estacionamentos | `GET /estacionamento`, `GET /estacionamento/{id}`, `POST /estacionamento/{id}`, `PUT /estacionamento/{id}`, `DELETE /estacionamento/{id}` |
| Tarifas (`Valor`) | `GET /valor`, `GET /valor/{id}`, `POST /valor`, `PUT /valor/{id}`, `DELETE /valor/{id}` |
| Reservas | `GET /reserva`, `GET /reserva/{id}`, `POST /reserva`, `PUT /reserva/{id}`, `DELETE /reserva/{id}` |
| Acessos | `GET /acesso`, `GET /acesso/{id}`, `POST /acesso`, `PUT /acesso/{id}`, `DELETE /acesso/{id}` |
| Avaliações | `GET /avaliacao`, `GET /avaliacao/{id}`, `POST /avaliacao`, `PUT /avaliacao/{id}`, `DELETE /avaliacao/{id}` |
| Carros | `GET /carro`, `GET /carro/{id}`, `POST /carro`, `PUT /carro/{id}`, `DELETE /carro/{id}` |
| Clientes | `GET /cliente`, `GET /cliente/{id}`, `POST /cliente`, `PUT /cliente/{id}`, `DELETE /cliente/{id}` |
| Donos | `GET /dono`, `GET /dono/{id}`, `POST /dono`, `PUT /dono/{id}`, `DELETE /dono/{id}` |
| Gerentes | `GET /gerente`, `GET /gerente/{id}`, `POST /gerente`, `PUT /gerente/{id}`, `DELETE /gerente/{id}` |

---

## Executando Testes

```powershell
.\gradlew.bat test
```
