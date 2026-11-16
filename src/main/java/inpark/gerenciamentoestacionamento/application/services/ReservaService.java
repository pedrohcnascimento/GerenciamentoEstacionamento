package inpark.gerenciamentoestacionamento.application.services;

import inpark.gerenciamentoestacionamento.application.dto.requests.ReservaRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.ReservaResponse;
import inpark.gerenciamentoestacionamento.application.mappers.ReservaMapper;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.EstacionamentoRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.ReservaRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.ClienteRepository;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.Reserva;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.enums.OrigemReserva;
import inpark.gerenciamentoestacionamento.model.enums.StatusReserva;
import inpark.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final EstacionamentoRepository estacionamentoRepository;
    private final ClienteRepository clienteRepository;

    public ReservaService(EstacionamentoRepository estacionamentoRepository,
                          ClienteRepository clienteRepository,
                          ReservaRepository reservaRepository) {
        this.estacionamentoRepository = estacionamentoRepository;
        this.clienteRepository = clienteRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<ReservaResponse> listarReservas() {
        return reservaRepository.findByStatusTrue()
                .stream()
                .map(ReservaMapper::fromEntity)
                .toList();
    }

    public ReservaResponse buscarPorId(Long id) {
        Optional<Reserva> optionalReserva = reservaRepository.findById(id);

        if (optionalReserva.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return ReservaMapper.fromEntity(optionalReserva.get());
    }

    @Transactional
    public ReservaResponse cadastrarReserva(ReservaRequest dto) {
        if (dto.clienteId() == null || dto.estacioId() == null) {
            throw new IdNaoCadastrado("Cliente ou estacionameno não encontrado no sistema!");
        }
        Reserva novaReserva = ReservaMapper.toEntity(dto);
        if (novaReserva.getStatusReserva() == null) {
            novaReserva.setStatusReserva(StatusReserva.PENDENTE);
        }
        if (novaReserva.getOrigem() == null) {
            novaReserva.setOrigem(OrigemReserva.AVULSA);
        }
        if (novaReserva.getCriadaEm() == null) {
            novaReserva.setCriadaEm(Instant.now());
        }

        Optional<Cliente> optionalCliente = clienteRepository.findById(dto.clienteId());
        Optional<Estacionamento> optionalEstacionamento = estacionamentoRepository.findById(dto.estacioId());

        if (optionalCliente.isEmpty() || optionalEstacionamento.isEmpty()) {
            throw new IdNaoCadastrado("Cliente ou estacionameno não encontrado no sistema!");
        }

        Cliente cliente = optionalCliente.get();
        Estacionamento estacionamento = optionalEstacionamento.get();

        novaReserva.setCliente(cliente);
        novaReserva.setEstacionamento(estacionamento);

        if (estacionamento.getReservas() == null) {
            estacionamento.setReservas(new ArrayList<>());
        }
        if (cliente.getReservas() == null) {
            cliente.setReservas(new ArrayList<>());
        }

        if (novaReserva.getStatusReserva() == StatusReserva.ACEITA
                && estacionamento.getVagasDisponiveis() != null
                && estacionamento.getVagasDisponiveis() > 0) {
            estacionamento.setVagasDisponiveis(estacionamento.getVagasDisponiveis() - 1);
        }

        estacionamento.getReservas().add(novaReserva);
        cliente.getReservas().add(novaReserva);

        novaReserva.setStatus(true);
        return ReservaMapper.fromEntity(reservaRepository.save(novaReserva));
    }

    @Transactional
    public ReservaResponse atualizarReserva(ReservaRequest dto, Long id) {
        Optional<Reserva> optionalReserva = reservaRepository.findById(id);

        if (optionalReserva.isEmpty()) {
            throw new IdNaoCadastrado("ID de reserva buscado não encontrado no sistema!");
        }

        Reserva reserva = optionalReserva.get();
        StatusReserva statusAnterior = reserva.getStatusReserva();

        reserva.setDataDaReserva(dto.dataDaReserva());
        reserva.setHoraDaReserva(dto.horaDaReserva());
        if (dto.statusReserva() != null) {
            reserva.setStatusReserva(dto.statusReserva());
        }

        Estacionamento estacionamento = reserva.getEstacionamento();
        if (estacionamento != null && estacionamento.getVagasDisponiveis() != null) {
            if (statusAnterior != StatusReserva.ACEITA && reserva.getStatusReserva() == StatusReserva.ACEITA) {
                if (estacionamento.getVagasDisponiveis() > 0) {
                    estacionamento.setVagasDisponiveis(estacionamento.getVagasDisponiveis() - 1);
                }
            } else if (statusAnterior == StatusReserva.ACEITA
                    && (reserva.getStatusReserva() == StatusReserva.RECUSADA || reserva.getStatusReserva() == StatusReserva.ENCERRADA)) {
                int max = estacionamento.getMaxVagas() != null ? estacionamento.getMaxVagas() : estacionamento.getVagasDisponiveis() + 1;
                estacionamento.setVagasDisponiveis(Math.min(max, estacionamento.getVagasDisponiveis() + 1));
                if (reserva.getStatusReserva() == StatusReserva.RECUSADA) {
                    reserva.setCanceladaEm(Instant.now());
                }
            }
        }

        return ReservaMapper.fromEntity(reservaRepository.save(reserva));
    }

    @Transactional
    public void deletarReserva(Long id) {
        Optional<Reserva> optionalReserva = reservaRepository.findById(id);

        if (optionalReserva.isEmpty()) {
            throw new IdNaoCadastrado("ID de reserva buscado não encontrado no sistema!");
        }

        Reserva reserva = optionalReserva.get();

        if (reserva.getCliente() != null && reserva.getCliente().getReservas() != null) {
            reserva.getCliente().getReservas().remove(reserva);
        }
        Estacionamento estacionamento = reserva.getEstacionamento();
        if (estacionamento != null) {
            if (estacionamento.getReservas() != null) {
                estacionamento.getReservas().remove(reserva);
            }
            if (reserva.getStatusReserva() == StatusReserva.ACEITA && estacionamento.getVagasDisponiveis() != null) {
                int max = estacionamento.getMaxVagas() != null ? estacionamento.getMaxVagas() : estacionamento.getVagasDisponiveis() + 1;
                estacionamento.setVagasDisponiveis(Math.min(max, estacionamento.getVagasDisponiveis() + 1));
            }
        }

        reserva.setCanceladaEm(Instant.now());
        reserva.setStatus(false);
        reservaRepository.save(reserva);
    }
}
