package inpark.gerenciamentoestacionamento.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import inpark.gerenciamentoestacionamento.model.enums.StatusReserva;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.sql.Time;
import java.util.Date;

public record ReservaRequest(
        @Schema(
                name = "clienteId",
                description = "Cliente que solicitou a reserva",
                example = "1"
        )
        @NotNull(message = "O clienteId é obrigatório")
        Long clienteId,
        @Schema(
                name = "estacioId",
                description = "Estabelecimento cadastrado em que a reserva foi solicitada",
                example = "1"
        )
        @NotNull(message = "O estacioId é obrigatório")
        @JsonAlias({"estacionamentoId", "estacioId"})
        Long estacioId,
        @Schema(
                name = "dataDaReserva",
                description = "Data em que a reserva foi feita, necessario ser futura",
                example = "2026-12-25"
        )
        @NotNull(message = "A data da reserva é obrigatória")
        Date dataDaReserva,
        @Schema(
                name = "horaDaReserva",
                description = "Hora em que a reserva foi feita, necessario estar dentro do horario de funcionamento do estacionamento",
                example = "14:30:00",
                type = "string",
                pattern = "HH:mm:ss"
        )
        @NotNull(message = "A hora da reserva é obrigatória")
        Time horaDaReserva,
        @Schema(
                name = "statusReserva",
                description = "Status da reserva",
                example = "PENDENTE"
        )
        StatusReserva statusReserva
) {
}
