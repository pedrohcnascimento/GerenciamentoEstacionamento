package parkwise.gerenciamentoestacionamento.application.mappers;

import parkwise.gerenciamentoestacionamento.application.dto.requests.ValorRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.ValorResponse;
import parkwise.gerenciamentoestacionamento.model.entities.Valor;

public class ValorMapper {

    public static Valor toEntity(ValorRequest dto) {
        Valor valor = new Valor();

        valor.setPreco(dto.preco());
        valor.setPeriodo(dto.periodo());
        valor.setTipoDeCobranca(dto.tipoDeCobranca());
        valor.setTipoDePagamento(dto.tipoDePagamento());

        return valor;
    }

    public static ValorResponse fromEntity(Valor valor) {
        return new ValorResponse(
                valor.getId(),
                valor.getTipoDeCobranca(),
                valor.getTipoDePagamento(),
                valor.getPreco(),
                valor.getPeriodo(),
                valor.getEstacionamento().getId(),
                valor.getStatus()
        );
    }
}
