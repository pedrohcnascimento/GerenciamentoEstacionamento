package parkwise.gerenciamentoestacionamento.application.dto.requests.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record GerenteRequest(
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
        Date dataNascimento,
        @Schema(
                name = "cpfOuCnpj",
                description = "Documento oficial necessário para contratar um gerente",
                example = "123.456.789-10"
        )
        @NotBlank(message = "O CPF ou CNPJ é obrigatório")
        @Size(min = 11, max = 18, message = "O CPF ou CNPJ deve ter entre 11 e 18 caracteres")
        String cpfOuCnpj,
        @Schema(
                name = "estacionamentoId",
                description = "ID do estacionamento que o gerente irá gerenciar",
                example = "1"
        )
        @NotNull(message = "O estacionamentoId não pode ser nulo")
        Long estacionamentoId
) {
}
