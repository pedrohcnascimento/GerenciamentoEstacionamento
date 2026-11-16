package inpark.gerenciamentoestacionamento.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;

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
                description = "Placa do carro que acessou o estacionamento, caso erro," +
                        " resulta em Null e necessita preenchimento manual posteriormente.",
                example = "EUD8679"
        )
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
        Double valorAPagar,
        @Schema(
                name = "estacioId",
                description = "Id do estacionamento em que o veiculo entrou",
                example = "1"
        )
        @JsonAlias({"estacionamentoId", "estacioId"})
        Long estacioId
) {
}
