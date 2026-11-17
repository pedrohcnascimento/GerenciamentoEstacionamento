package parkwise.gerenciamentoestacionamento.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalTime;

public record EstacionamentoRequest(
        @Schema(
                name = "nome",
                description = "Nome do estacionamento criado",
                example = "EstacioPlay"
        )
        @NotBlank(message = "O nome do estacionamento é obrigatório")
        String nome,
        @Schema(
                name = "endereco",
                description = "Endereço do estacionamento.",
                example = "Rua das Flores"
        )
        @NotBlank(message = "O endereço é obrigatório")
        String endereco,
        @Schema(
                name = "CEP",
                description = "CEP do estacionamento.",
                example = "12345-678"
        )
        @NotBlank(message = "O CEP é obrigatório")
        String CEP,
        @Schema(
                name = "numero",
                description = "Número do estacionamento.",
                example = "123"
        )
        @NotBlank(message = "O número é obrigatório")
        String numero,
        @Schema(
                name = "foto",
                description = "Foto do estacionamento.",
                example = "foto.jpg"
        )
        String foto,
        @Schema(
                name = "numeroAlvaraDeFuncionamento",
                description = "Número do alvará de funcionamento do estacionamento.",
                example = "123456789"
        )
        @NotBlank(message = "O número do alvará de funcionamento é obrigatório")
        String numeroAlvaraDeFuncionamento,
        @Schema(
                name = "horaFechamento",
                description = "Hora de fechamento do estacionamento.",
                type = "string",
                pattern = "HH:mm:ss",
                example = "22:00:00"
        )
        @NotNull(message = "A hora de fechamento é obrigatória")
        LocalTime horaFechamento,
        @Schema(
                name = "horaAbertura",
                description = "Hora de abertura do estacionamento.",
                type = "string",
                pattern = "HH:mm:ss",
                example = "08:00:00"
        )
        @NotNull(message = "A hora de abertura é obrigatória")
        LocalTime horaAbertura,
        @Schema(
                name = "vagasPreferenciais",
                description = "Número de vagas preferenciais do estacionamento.",
                example = "10"
        )
        @PositiveOrZero(message = "Vagas preferenciais não pode ser negativo")
        Integer vagasPreferenciais,
        @Schema(
                name = "maximoDeVagas",
                description = "Número máximo de vagas do estacionamento.",
                example = "100"
        )
        @NotNull(message = "O número máximo de vagas é obrigatório")
        @Positive(message = "O número máximo de vagas deve ser positivo")
        Integer maximoDeVagas,
        @Schema(
                name = "numeroDeEscrituraImovel",
                description = "Número de escritura do imóvel do estacionamento.",
                example = "987654321"
        )
        String numeroDeEscrituraImovel
) {
}
