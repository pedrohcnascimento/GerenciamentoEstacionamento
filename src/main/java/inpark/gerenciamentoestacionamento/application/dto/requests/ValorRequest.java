package inpark.gerenciamentoestacionamento.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import inpark.gerenciamentoestacionamento.model.enums.Cobranca;
import inpark.gerenciamentoestacionamento.model.enums.Metodo;
import inpark.gerenciamentoestacionamento.model.enums.Periodo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ValorRequest(
        @Schema(
                name = "tipoDeCobranca",
                description = "Tipo de cobrança do estacionamento, definido por enum Cobranca",
                example = "PORHORA"
        )
        @NotNull(message = "O tipo de cobrança é obrigatório")
        Cobranca tipoDeCobranca,
        @Schema(
                name = "tipoDePagamento",
                description = "Tipo de pagamento do estacionamento, definido por enum Metodo",
                example = "DINHEIRO"
        )
        @NotNull(message = "O tipo de pagamento é obrigatório")
        Metodo tipoDePagamento,
        @Schema(
                name = "preco",
                description = "Preço cobrado pelo estacionamento",
                example = "10.00"
        )
        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        Double preco,
        @Schema(
                name = "periodo",
                description = "Período de cobrança do estacionamento, definido por enum Periodo",
                example = "FINALDESEMANA"
        )
        @NotNull(message = "O período é obrigatório")
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
