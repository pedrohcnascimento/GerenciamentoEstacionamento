package inpark.gerenciamentoestacionamento.infraestructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inpark API — CitizenTech")
                        .version("1.0.0")
                        .summary("API REST de gerenciamento, busca e reserva de estacionamentos")
                        .description("""
                                API RESTful para busca de estacionamentos, reserva de vagas, controle de acessos, tarifação e avaliações.

                                ### Como autenticar no Swagger
                                1. Utilize **POST /auth/login** (ou **POST /auth/criar-admin** para gerar um usuário de teste) e copie o `token` retornado.
                                2. Clique no botão **Authorize** acima e informe o token JWT.
                                3. Endpoints com cadeado aberto são públicos; endpoints com cadeado fechado exigem token JWT e perfil compatível (`CLIENTE`, `DONO`, `GERENTE` ou `ADMIN`).
                                """)
                        .contact(new Contact()
                                .name("Equipe Inpark")
                        )
                )
                .tags(List.of(
                        new Tag().name("01. Autenticação").description("Login JWT, consulta do usuário autenticado e geração de admin de teste"),
                        new Tag().name("02. Clientes").description("Cadastro, consulta, atualização e remoção de clientes"),
                        new Tag().name("03. Donos de Estacionamento").description("Cadastro, consulta, atualização e remoção de proprietários"),
                        new Tag().name("04. Gerentes").description("Cadastro, consulta, atualização e remoção de gerentes"),
                        new Tag().name("05. Estacionamentos").description("Consulta, atualização e remoção de estacionamentos"),
                        new Tag().name("06. Carros").description("Consulta, atualização e remoção de veículos"),
                        new Tag().name("07. Tarifas e Valores").description("Consulta, atualização e remoção de tarifas"),
                        new Tag().name("08. Reservas").description("Consulta, atualização e remoção de reservas"),
                        new Tag().name("09. Acessos (Entrada e Saída)").description("Consulta, atualização e remoção de acessos"),
                        new Tag().name("10. Avaliações").description("Consulta, atualização e remoção de avaliações"),
                        new Tag().name("11. Rotas 1:N — Cliente").description("Operações aninhadas vinculadas a um Cliente (carros e reservas)"),
                        new Tag().name("12. Rotas 1:N — Dono").description("Operações aninhadas vinculadas a um Dono (estacionamentos)"),
                        new Tag().name("13. Rotas 1:N — Estacionamento").description("Operações aninhadas vinculadas a um Estacionamento (reservas, tarifas, acessos e avaliações)")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer authentication",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Informe o token JWT obtido em POST /auth/login")
                        ));
    }
}
