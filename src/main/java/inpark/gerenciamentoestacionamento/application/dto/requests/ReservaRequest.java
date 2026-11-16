package inpark.gerenciamentoestacionamento.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import inpark.gerenciamentoestacionamento.model.enums.StatusReserva;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Time;
import java.util.Date;

public record ReservaRequest(
        @Schema(
                name = "clienteId",
                description = "Cliente que solicitou a reserva",
                example = "1"
        )
        Long clienteId,
        @Schema(
                name = "estacioId",
                description = "Estabelecimento cadastrado em que a reserva foi solicitada",
                example = "1"
        )
        @JsonAlias({"estacionamentoId", "estacioId"})
        Long estacioId,
        @Schema(
                name = "dataDaReserva",
                description = "Data em que a reserva foi feita, necessario ser futura",
                example = "2026-12-25"
        )
        Date dataDaReserva,
        @Schema(
                name = "horaDaReserva",
                description = "Hora em que a reserva foi feita, necessario estar dentro do horario de funcionamento do estacionamento",
                example = "14:30:00",
                type = "string",
                pattern = "HH:mm:ss"
        )
        Time horaDaReserva,
        @Schema(
                name = "statusReserva",
                description = "Status da reserva",
                example = "PENDENTE"
        )
        StatusReserva statusReserva
) {
}
