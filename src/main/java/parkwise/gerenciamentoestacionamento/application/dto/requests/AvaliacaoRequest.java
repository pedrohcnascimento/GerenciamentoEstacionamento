package parkwise.gerenciamentoestacionamento.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AvaliacaoRequest(
        @Schema(
                name = "clienteId",
                description = "ID do cliente que fez a avaliação",
                example = "1"
        )
        @NotNull(message = "O clienteId é obrigatório")
        Long clienteId,
        @Schema(
                name = "estacionamentoId",
                description = "ID do estacionamento que foi avaliado",
                example = "1"
        )
        @NotNull(message = "O estacionamentoId é obrigatório")
        Long estacionamentoId,
        @Schema(
                name = "nota",
                description = "Nota dada pelo cliente ao estacionamento, de 0 a 5",
                example = "4"
        )
        @NotNull(message = "A nota é obrigatória")
        @Min(value = 0, message = "A nota mínima é 0")
        @Max(value = 5, message = "A nota máxima é 5")
        Short nota,
        @Schema(
                name = "comentario",
                description = "Comentario feito pelo cliente sobre o estacionamento",
                example = "Estacionamento bom, porem um pouco caro"
        )
        @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
        String comentario,
        @Schema(
                name = "dataDeAvaliacao",
                description = "Data em que a avaliação foi feita",
                example = "2026-11-16T15:30:00"
        )
        LocalDateTime dataDeAvaliacao
) {
}
