package inpark.gerenciamentoestacionamento.application.dto.requests.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record DonoRequest(
        @Schema(
                name = "nome",
                description = "Nome do usuario.",
                example = "Pedro"
        )
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 255, message = "O nome deve ter entre 2 e 255 caracteres")
        String nome,
        @Schema(
                name = "email",
                description = "E-mail do usuario",
                example = "pedro.nascimento@gmail.com"
        )
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,
        @Schema(
                name = "senha",
                description = "Senha de acesso do usuario, " +
                        "deve conter pelo menos uma letra maiuscula, simbolo e numero",
                example = "Estacio_2025"
        )
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha,
        @Schema(
                name = "dataNascimento",
                description = "Data de nascimento do usuario, deve ser maior de 18 anos",
                example = "2000-09-12"
        )
        @NotNull(message = "A data de nascimento é obrigatória")
        Date dataNascimento
) {
}
