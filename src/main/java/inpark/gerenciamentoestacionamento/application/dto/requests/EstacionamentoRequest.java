package inpark.gerenciamentoestacionamento.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public record EstacionamentoRequest(
        @Schema(
                name = "nome",
                description = "Nome do estacionamento criado",
                example = "EstacioPlay"
        )
        String nome,
        @Schema(
                name = "endereco",
                description = "Endereço do estacionamento.",
                example = "Rua das Flores"
        )
        String endereco,
        @Schema(
                name = "CEP",
                description = "CEP do estacionamento.",
                example = "12345-678"
        )
        String CEP,
        @Schema(
                name = "numero",
                description = "Número do estacionamento.",
                example = "123"
        )
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
        String numeroAlvaraDeFuncionamento,
        @Schema(
                name = "horaFechamento",
                description = "Hora de fechamento do estacionamento.",
                type = "string",
                pattern = "HH:mm:ss",
                example = "22:00:00"
        )
        LocalTime horaFechamento,
        @Schema(
                name = "horaAbertura",
                description = "Hora de abertura do estacionamento.",
                type = "string",
                pattern = "HH:mm:ss",
                example = "08:00:00"
        )
        LocalTime horaAbertura,
        @Schema(
                name = "vagasPreferenciais",
                description = "Número de vagas preferenciais do estacionamento.",
                example = "10"
        )
        Integer vagasPreferenciais,
        @Schema(
                name = "maximoDeVagas",
                description = "Número máximo de vagas do estacionamento.",
                example = "100"
        )
        Integer maximoDeVagas,
        @Schema(
                name = "numeroDeEscrituraImovel",
                description = "Número de escritura do imóvel do estacionamento.",
                example = "987654321"
        )
        String numeroDeEscrituraImovel
) {
}
