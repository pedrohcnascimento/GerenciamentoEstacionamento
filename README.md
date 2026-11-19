# ParkWise — Gerenciamento de Estacionamento (Backend)

API REST em **Java 21 + Spring Boot** para gerenciamento, busca e reserva de vagas de estacionamento da plataforma **ParkWise**, estruturada conforme a especificação **Global Solution 2026/2 · CitizenTech FIAP**.

---

## Integrantes do Grupo

| Nome | RM |
|---|---|
| Pedro Henrique Carvalho do Nascimento | 570492 |
| Gabriel Pereira de Oliveira | 572262 |
| Adalto Massashiro Nagabe | 572298 |
| Kauã Barros Ferreira | 571801 |
| Murillo Siviero Lopes | 572724 |

---

## Links da Entrega

- **API Publicada (Render):** https://gerenciamentoestacionamento.onrender.com
- **Swagger UI:** https://gerenciamentoestacionamento.onrender.com/swagger-ui/index.html
- **Documentação Técnica (PDF):** [docs/Documentacao_ParkWise_GS_2026-2.pdf](docs/Documentacao_ParkWise_GS_2026-2.pdf)
- **Vídeo de Demonstração:** _[Inserir link do vídeo antes da entrega]_

---

## Tecnologias Utilizadas

- **Java 21** e **Spring Boot** (WebMVC, JDBC, Bean Validation, Spring Security)
- **Oracle Database (`ojdbc11`)** com **JDBC puro** (`DataSource` via construtor + `PreparedStatement`) e **H2 (modo Oracle)** para testes automatizados
- **JWT (`jjwt 0.11.5`)** e **BCrypt** para autenticação e autorização (`ADMIN`, `DONO`, `GERENTE`, `CLIENTE`)
- **Springdoc OpenAPI / Swagger UI** (`/swagger-ui/index.html`, `/v3/api-docs`)
- **ModelMapper (`3.2.1`)** e **Lombok**
- **Docker** (multi-stage build) para deploy no **Render**
- **JUnit 5, Mockito & Spring MockMvc**

---

## Estrutura de Pacotes (`parkwise.gerenciamentoestacionamento`)

- `model/` — Entidades de domínio puras (`entities/`), enums (`enums/`), exceções de negócio (`exceptions/`) e validadores (`service/`)
- `dao/` — Camada de acesso a dados em JDBC puro (`AbstractDao` + `UsuarioDao`, `ClienteDao`, `DonoDao`, `GerenteDao`, `EstacionamentoDao`, `ValorDao`, `CarroDao`, `ReservaDao`, `AcessoDao`, `AvaliacaoDao`)
- `application/` — DTOs `record` de entrada/saída (`dto/`), mappers (`mappers/`) e serviços de negócio (`services/`)
- `infraestructure/` — Configurações (`CorsConfig`, `ModelMapperConfig`, `SwaggerConfig`), handler global (`GlobalExceptionHandler`) e segurança JWT (`security/`)
- `infraestructure_ui/controller/` — Controllers REST dos recursos e controllers dedicados às rotas 1:N (`ClienteRelacionamentoController`, `DonoRelacionamentoController`, `EstacionamentoRelacionamentoController`)

---

## Como Rodar o Projeto

### 1. Localmente com Oracle FIAP (via `.env`)
Copie `.env.example` para `.env` na raiz e preencha suas credenciais do Oracle da FIAP:
```env
DB_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
DB_USER=rmXXXXXX
DB_PASSWORD=sua_senha
DB_DRIVER=oracle.jdbc.OracleDriver
SQL_INIT_MODE=always
PORT=8080
```
Depois execute:
```powershell
.\gradlew.bat bootRun
```

### 2. Localmente sem `.env` (H2 em modo Oracle)
Sem arquivo `.env`, a aplicação sobe automaticamente usando H2 em memória no modo Oracle:
```powershell
.\gradlew.bat bootRun
```

### 3. Executar Testes Automatizados
```powershell
.\gradlew.bat test
```

### 4. Executar com Docker
```bash
docker build -t parkwise-api .
docker run -p 8080:8080 -e DB_URL=... -e DB_USER=... -e DB_PASSWORD=... -e DB_DRIVER=oracle.jdbc.OracleDriver -e SQL_INIT_MODE=never parkwise-api
```

---

## Script SQL (Oracle FIAP)

O script DDL com todas as tabelas (`tb_*`), chaves primárias (`cd_*`), chaves estrangeiras 1:N e sequences (`sq_tb_*`) encontra-se em:
- [src/main/resources/schema.sql](src/main/resources/schema.sql)
