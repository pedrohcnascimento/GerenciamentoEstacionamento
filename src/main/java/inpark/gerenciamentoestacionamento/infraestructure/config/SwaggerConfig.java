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
                        new Tag().name("1. Autenticação").description("Login JWT, consulta do usuário autenticado e geração de admin de teste"),
                        new Tag().name("2. Clientes").description("CRUD de clientes e rotas aninhadas 1:N (carros e reservas do cliente)"),
                        new Tag().name("3. Donos de Estacionamento").description("CRUD de proprietários e rota aninhada 1:N (estacionamentos do proprietário)"),
                        new Tag().name("4. Gerentes").description("CRUD de gerentes vinculados a estacionamentos"),
                        new Tag().name("5. Estacionamentos").description("CRUD de estacionamentos e rotas aninhadas 1:N (reservas, tarifas, acessos e avaliações)"),
                        new Tag().name("6. Carros").description("CRUD direto de veículos"),
                        new Tag().name("7. Tarifas e Valores").description("CRUD direto de tabelas de preços e cobranças"),
                        new Tag().name("8. Reservas").description("CRUD direto de reservas de vagas"),
                        new Tag().name("9. Acessos (Entrada e Saída)").description("CRUD direto de registros de entrada e saída de veículos"),
                        new Tag().name("10. Avaliações").description("CRUD direto de avaliações de estacionamentos")
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
