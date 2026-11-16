package inpark.gerenciamentoestacionamento.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record AvaliacaoRequest(
        @Schema(
                name = "clienteId",
                description = "ID do cliente que fez a avaliação",
                example = "1"
        )
        Long clienteId,
        @Schema(
                name = "estacionamentoId",
                description = "ID do estacionamento que foi avaliado",
                example = "1"
        )
        Long estacionamentoId,
        @Schema(
                name = "nota",
                description = "Nota dada pelo cliente ao estacionamento, de 0 a 5",
                example = "4"
        )
        Short nota,
        @Schema(
                name = "comentario",
                description = "Comentario feito pelo cliente sobre o estacionamento",
                example = "Estacionamento bom, porem um pouco caro"
        )
        String comentario,
        @Schema(
                name = "dataDeAvaliacao",
                description = "Data em que a avaliação foi feita",
                example = "2026-11-16T15:30:00"
        )
        LocalDateTime dataDeAvaliacao
) {
}
