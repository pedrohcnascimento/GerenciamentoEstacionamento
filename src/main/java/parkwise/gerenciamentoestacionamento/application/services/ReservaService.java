package parkwise.gerenciamentoestacionamento.application.services;

import parkwise.gerenciamentoestacionamento.application.dto.requests.ReservaRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.ReservaResponse;
import parkwise.gerenciamentoestacionamento.application.mappers.ReservaMapper;
import parkwise.gerenciamentoestacionamento.dao.EstacionamentoDao;
import parkwise.gerenciamentoestacionamento.dao.ReservaDao;
import parkwise.gerenciamentoestacionamento.dao.ClienteDao;
import parkwise.gerenciamentoestacionamento.model.entities.Estacionamento;
import parkwise.gerenciamentoestacionamento.model.entities.Reserva;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import parkwise.gerenciamentoestacionamento.model.enums.OrigemReserva;
import parkwise.gerenciamentoestacionamento.model.enums.StatusReserva;
import parkwise.gerenciamentoestacionamento.model.exceptions.ConflitoDeRegraDeNegocioException;
import parkwise.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    private final ReservaDao ReservaDao;
    private final EstacionamentoDao EstacionamentoDao;
    private final ClienteDao ClienteDao;

    public ReservaService(EstacionamentoDao EstacionamentoDao,
                          ClienteDao ClienteDao,
                          ReservaDao ReservaDao) {
        this.EstacionamentoDao = EstacionamentoDao;
        this.ClienteDao = ClienteDao;
        this.ReservaDao = ReservaDao;
    }

    public List<ReservaResponse> listarReservas() {
        return ReservaDao.findByStatusTrue()
                .stream()
                .map(ReservaMapper::fromEntity)
                .toList();
    }

    public List<ReservaResponse> listarPorEstacionamento(Long estacionamentoId) {
        if (estacionamentoId == null || EstacionamentoDao.findById(estacionamentoId).isEmpty()) {
            throw new IdNaoCadastrado("Estacionamento não encontrado no sistema!");
        }
        return ReservaDao.findByEstacionamentoId(estacionamentoId)
                .stream()
                .map(ReservaMapper::fromEntity)
                .toList();
    }

    public List<ReservaResponse> listarPorCliente(Long clienteId) {
        if (clienteId == null || ClienteDao.findById(clienteId).isEmpty()) {
            throw new IdNaoCadastrado("Cliente não encontrado no sistema!");
        }
        return ReservaDao.findByClienteId(clienteId)
                .stream()
                .map(ReservaMapper::fromEntity)
                .toList();
    }

    public ReservaResponse buscarPorId(Long id) {
        Optional<Reserva> optionalReserva = ReservaDao.findById(id);

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

        Optional<Cliente> optionalCliente = ClienteDao.findById(dto.clienteId());
        Optional<Estacionamento> optionalEstacionamento = EstacionamentoDao.findById(dto.estacioId());

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

        if (novaReserva.getStatusReserva() == StatusReserva.ACEITA) {
            if (estacionamento.getVagasDisponiveis() != null && estacionamento.getVagasDisponiveis() <= 0) {
                throw new ConflitoDeRegraDeNegocioException("Não há vagas disponíveis neste estacionamento para confirmar a reserva.");
            }
            if (estacionamento.getVagasDisponiveis() != null) {
                estacionamento.setVagasDisponiveis(estacionamento.getVagasDisponiveis() - 1);
                EstacionamentoDao.save(estacionamento);
            }
        }

        estacionamento.getReservas().add(novaReserva);
        cliente.getReservas().add(novaReserva);

        novaReserva.setStatus(true);
        return ReservaMapper.fromEntity(ReservaDao.save(novaReserva));
    }

    @Transactional
    public ReservaResponse atualizarReserva(ReservaRequest dto, Long id) {
        Optional<Reserva> optionalReserva = ReservaDao.findById(id);

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
                if (estacionamento.getVagasDisponiveis() <= 0) {
                    throw new ConflitoDeRegraDeNegocioException("Não há vagas disponíveis neste estacionamento para confirmar a reserva.");
                }
                estacionamento.setVagasDisponiveis(estacionamento.getVagasDisponiveis() - 1);
                EstacionamentoDao.save(estacionamento);
            } else if (statusAnterior == StatusReserva.ACEITA
                    && (reserva.getStatusReserva() == StatusReserva.RECUSADA || reserva.getStatusReserva() == StatusReserva.ENCERRADA)) {
                int max = estacionamento.getMaxVagas() != null ? estacionamento.getMaxVagas() : estacionamento.getVagasDisponiveis() + 1;
                estacionamento.setVagasDisponiveis(Math.min(max, estacionamento.getVagasDisponiveis() + 1));
                EstacionamentoDao.save(estacionamento);
                if (reserva.getStatusReserva() == StatusReserva.RECUSADA) {
                    reserva.setCanceladaEm(Instant.now());
                }
            }
        }

        return ReservaMapper.fromEntity(ReservaDao.save(reserva));
    }

    @Transactional
    public void deletarReserva(Long id) {
        Optional<Reserva> optionalReserva = ReservaDao.findById(id);

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
        ReservaDao.save(reserva);
    }
}
