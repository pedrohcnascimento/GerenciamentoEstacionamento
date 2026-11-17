package inpark.gerenciamentoestacionamento.application.dto.requests.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioLoginRequest(
        @Schema(
                description = "E-mail do usuário",
                example = "usuario@exemplo.com"
        )
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,
        @Schema(
                description = "Senha do usuário. Deve conter letra maiúscula, símbolo e número.",
                example = "Estacio_2025"
        )
        @NotBlank(message = "A senha é obrigatória")
        String senha
) {
}
