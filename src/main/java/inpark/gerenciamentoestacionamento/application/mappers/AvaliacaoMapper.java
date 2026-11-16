package inpark.gerenciamentoestacionamento.application.mappers;

import inpark.gerenciamentoestacionamento.application.dto.requests.AvaliacaoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.AvaliacaoResponse;
import inpark.gerenciamentoestacionamento.application.mappers.usuario.ClienteMapper;
import inpark.gerenciamentoestacionamento.model.entities.Avaliacao;

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
