package parkwise.gerenciamentoestacionamento.application.mappers;

import parkwise.gerenciamentoestacionamento.application.dto.requests.AvaliacaoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.AvaliacaoResponse;
import parkwise.gerenciamentoestacionamento.application.mappers.usuario.ClienteMapper;
import parkwise.gerenciamentoestacionamento.model.entities.Avaliacao;

public class AvaliacaoMapper {

    public static Avaliacao toEntity(AvaliacaoRequest dto) {
        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setDataDeAvaliacao(dto.dataDeAvaliacao());
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());

        return avaliacao;
    }

    public static AvaliacaoResponse fromEntity(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getId(),
                ClienteMapper.fromEntity(avaliacao.getCliente()),
                avaliacao.getEstacionamento().getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataDeAvaliacao(),
                avaliacao.getStatus()
        );
    }
}
