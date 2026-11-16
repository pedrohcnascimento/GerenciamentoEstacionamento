package inpark.gerenciamentoestacionamento.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import inpark.gerenciamentoestacionamento.model.enums.Cobranca;
import inpark.gerenciamentoestacionamento.model.enums.Metodo;
import inpark.gerenciamentoestacionamento.model.enums.Periodo;
import io.swagger.v3.oas.annotations.media.Schema;

public record ValorRequest(
        @Schema(
                name = "tipoDeCobranca",
                description = "Tipo de cobrança do estacionamento, definido por enum Cobranca",
                example = "PORHORA"
        )
        Cobranca tipoDeCobranca,
        @Schema(
                name = "tipoDePagamento",
                description = "Tipo de pagamento do estacionamento, definido por enum Metodo",
                example = "DINHEIRO"
        )
        Metodo tipoDePagamento,
        @Schema(
                name = "preco",
                description = "Preço cobrado pelo estacionamento",
                example = "10.00"
        )
        Double preco,
        @Schema(
                name = "periodo",
                description = "Período de cobrança do estacionamento, definido por enum Periodo",
                example = "FINALDESEMANA"
        )
        Periodo periodo,
        @Schema(
                name = "estacionamentoId",
                description = "ID do estacionamento associado a esse valor",
                example = "1"
        )
        @JsonAlias({"estacioId", "estacionamentoId"})
        Long estacionamentoId,
        @Schema(hidden = true)
        Long estacioId
) {
    public ValorRequest {
        if (estacionamentoId == null && estacioId != null) {
            estacionamentoId = estacioId;
        }
    }

    public ValorRequest(
            Cobranca tipoDeCobranca,
            Metodo tipoDePagamento,
            Double preco,
            Periodo periodo,
            Long estacionamentoId
    ) {
        this(tipoDeCobranca, tipoDePagamento, preco, periodo, estacionamentoId, estacionamentoId);
    }
}
