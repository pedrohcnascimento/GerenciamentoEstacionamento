package parkwise.gerenciamentoestacionamento.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalTime;

public record AcessoRequest(
        @Schema(
                name = "carroId",
                description = "Id do carro que acessou o estacionamento",
                example = "1"
        )
        Long carroId,
        @Schema(
                name = "placaDoCarro",
                description = "Placa do carro que acessou o estacionamento",
                example = "EUD8679"
        )
        @NotBlank(message = "A placa do carro é obrigatória")
        String placaDoCarro,
        @Schema(
                name = "horaDeEntrada",
                description = "Hora em que o carro entrou",
                example = "17:50:00",
                type = "string",
                pattern = "HH:mm:ss"
        )
        LocalTime horaDeEntrada,
        @Schema(
                name = "horaDeSaida",
                description = "Hora em que o carro saiu",
                example = "19:30:00",
                type = "string",
                pattern = "HH:mm:ss"
        )
        LocalTime horaDeSaida,
        @Schema(
                name = "valorAPagar",
                description = "Valor total a pagar pelo tempo de uso do estacionamento",
                example = "15.00"
        )
        @PositiveOrZero(message = "O valor a pagar não pode ser negativo")
        Double valorAPagar,
        @Schema(
                name = "estacioId",
                description = "Id do estacionamento em que o veiculo entrou",
                example = "1"
        )
        @NotNull(message = "O estacioId é obrigatório")
        @JsonAlias({"estacionamentoId", "estacioId"})
        Long estacioId
) {
}
