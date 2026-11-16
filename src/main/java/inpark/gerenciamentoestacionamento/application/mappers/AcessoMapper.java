package inpark.gerenciamentoestacionamento.application.mappers;

import inpark.gerenciamentoestacionamento.application.dto.requests.AcessoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.AcessoResponse;
import inpark.gerenciamentoestacionamento.model.entities.Acesso;
import inpark.gerenciamentoestacionamento.model.entities.Carro;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;

public class AcessoMapper {

    public static Acesso toEntity(AcessoRequest dto) {
        if (dto == null) return null;

        Acesso acesso = new Acesso();

        acesso.setPlacaDoCarro(dto.placaDoCarro());
        acesso.setHoraDeEntrada(dto.horaDeEntrada());
        acesso.setHoraDeSaida(dto.horaDeSaida());
        acesso.setValorAPagar(dto.valorAPagar());
        acesso.setStatus(true);
        if (dto.carroId() != null) {
            Carro carro = new Carro();
            carro.setId(dto.carroId());
            if (dto.placaDoCarro() != null) carro.setPlaca(dto.placaDoCarro());
            acesso.setCarro(carro);
        }

        if (dto.estacioId() != null) {
            Estacionamento e = new Estacionamento();
            e.setId(dto.estacioId());
            acesso.setEstacionamento(e);
        }

        return acesso;
    }

    public static AcessoResponse fromEntity(Acesso acesso) {
        if (acesso == null) return null;

        Long carroId = null;
        String placa = acesso.getPlacaDoCarro();

        if (acesso.getCarro() != null) {
            carroId = acesso.getCarro().getId();
            if (acesso.getCarro().getPlaca() != null) placa = acesso.getCarro().getPlaca();
        }

        Long estacionamentoId = acesso.getEstacionamento() != null ? acesso.getEstacionamento().getId() : null;

        return new AcessoResponse(
                acesso.getId(),
                carroId,
                placa,
                acesso.getHoraDeEntrada(),
                acesso.getHoraDeSaida(),
                acesso.getTotalHoras(),
                acesso.getValorAPagar(),
                estacionamentoId,
                acesso.getStatus()
        );
    }
}
