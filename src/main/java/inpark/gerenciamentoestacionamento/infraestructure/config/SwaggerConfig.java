package inpark.gerenciamentoestacionamento.infraestructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inpark")
                        .summary("Sistema de gerenciamento e reserva de estacionamentos")
                        .description("API REST do sistema Inpark para busca, reserva e gerenciamento de vagas de estacionamento.")
                        .contact(new Contact()
                                .name("Equipe Inpark")
                                .email("")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer authentication",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}
