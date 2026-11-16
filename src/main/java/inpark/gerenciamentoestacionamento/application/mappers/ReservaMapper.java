package inpark.gerenciamentoestacionamento.application.mappers;

import inpark.gerenciamentoestacionamento.application.dto.requests.ReservaRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.ReservaResponse;
import inpark.gerenciamentoestacionamento.application.dto.response.ReservaResponseResumo;
import inpark.gerenciamentoestacionamento.application.mappers.usuario.ClienteMapper;
import inpark.gerenciamentoestacionamento.model.entities.Reserva;

public class ReservaMapper {

    public static Reserva toEntity(ReservaRequest dto) {
        Reserva reserva = new Reserva();

        reserva.setDataDaReserva(dto.dataDaReserva());
        reserva.setHoraDaReserva(dto.horaDaReserva());
        reserva.setStatusReserva(dto.statusReserva());

        return reserva;
    }

    public static ReservaResponse fromEntity(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                ClienteMapper.fromEntity(reserva.getCliente()),
                EstacionamentoMapper.fromEntity(reserva.getEstacionamento()),
                reserva.getDataDaReserva(),
                reserva.getHoraDaReserva(),
                reserva.getStatusReserva(),
                reserva.getStatus()
        );
    }

    public static ReservaResponseResumo resumo(Reserva reserva) {
        return new ReservaResponseResumo(
                reserva.getId(),
                ClienteMapper.fromEntity(reserva.getCliente()),
                reserva.getDataDaReserva(),
                reserva.getHoraDaReserva(),
                reserva.getStatusReserva(),
                reserva.getStatus()
        );
    }
}
