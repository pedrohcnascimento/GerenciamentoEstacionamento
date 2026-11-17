package inpark.gerenciamentoestacionamento.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CarroRequest(
        @Schema(
                name = "clienteId",
                example = "3",
                description = "ID do usuario proprietário do carro"
        )
        @NotNull(message = "O clienteId é obrigatório")
        Long clienteId,
        @Schema(
                name = "placa",
                example = "EUD8679",
                description = "Placa de identificação do carro"
        )
        @NotBlank(message = "A placa é obrigatória")
        @Size(min = 7, max = 10, message = "A placa deve ter entre 7 e 10 caracteres")
        String placa,
        @Schema(
                name = "modelo",
                example = "Corsa",
                description = "Modelo do carro"
        )
        @NotBlank(message = "O modelo é obrigatório")
        String modelo,
        @Schema(
                name = "cor",
                example = "Fuxia",
                description = "Cor do carro"
        )
        @NotBlank(message = "A cor é obrigatória")
        String cor
) {
}
