package inpark.gerenciamentoestacionamento.application.services;

import inpark.gerenciamentoestacionamento.application.dto.requests.AvaliacaoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.AvaliacaoResponse;
import inpark.gerenciamentoestacionamento.application.mappers.AvaliacaoMapper;
import inpark.gerenciamentoestacionamento.dao.AvaliacaoDao;
import inpark.gerenciamentoestacionamento.dao.EstacionamentoDao;
import inpark.gerenciamentoestacionamento.dao.ClienteDao;
import inpark.gerenciamentoestacionamento.model.entities.Avaliacao;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import inpark.gerenciamentoestacionamento.model.service.ValidadorAvaliacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {
    private final AvaliacaoDao AvaliacaoDao;
    private final EstacionamentoDao EstacionamentoDao;
    private final ClienteDao ClienteDao;
    private final ValidadorAvaliacao validador;

    public AvaliacaoService(AvaliacaoDao AvaliacaoDao,
                            EstacionamentoDao EstacionamentoDao,
                            ClienteDao ClienteDao,
                            ValidadorAvaliacao validador) {
        this.AvaliacaoDao = AvaliacaoDao;
        this.EstacionamentoDao = EstacionamentoDao;
        this.ClienteDao = ClienteDao;
        this.validador = validador;
    }

    public List<AvaliacaoResponse> listarAvaliacoes() {
        return AvaliacaoDao.findByStatusTrue()
                .stream()
                .map(AvaliacaoMapper::fromEntity)
                .toList();
    }

    public List<AvaliacaoResponse> listarPorEstacionamento(Long estacionamentoId) {
        if (estacionamentoId == null || EstacionamentoDao.findById(estacionamentoId).isEmpty()) {
            throw new IdNaoCadastrado("Estacionamento não encontrado no sistema");
        }
        return AvaliacaoDao.findByEstacionamentoId(estacionamentoId)
                .stream()
                .map(AvaliacaoMapper::fromEntity)
                .toList();
    }

    public AvaliacaoResponse buscarPorId(Long id) {
        Optional<Avaliacao> optionalAvaliacao = AvaliacaoDao.findById(id);

        if (optionalAvaliacao.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return AvaliacaoMapper.fromEntity(optionalAvaliacao.get());
    }

    @Transactional
    public AvaliacaoResponse cadastrarAvaliacao(AvaliacaoRequest dto) {
        if (dto.clienteId() == null) {
            throw new IdNaoCadastrado("Cliente não encontrado no sistema");
        }
        Avaliacao avaliacao = AvaliacaoMapper.toEntity(dto);
        if (avaliacao.getDataDeAvaliacao() == null) {
            avaliacao.setDataDeAvaliacao(LocalDateTime.now());
        }

        Cliente cliente = ClienteDao.findById(dto.clienteId())
                .orElseThrow(() -> new IdNaoCadastrado("Cliente não encontrado no sistema"));

        if (dto.estacionamentoId() == null) {
            throw new IdNaoCadastrado("Estacionamento não encontrado no sistema");
        }
        Estacionamento estacionamento = EstacionamentoDao.findById(dto.estacionamentoId())
                .orElseThrow(() -> new IdNaoCadastrado("Estacionamento não encontrado no sistema"));

        avaliacao.setCliente(cliente);
        avaliacao.setEstacionamento(estacionamento);

        if (estacionamento.getAvaliacoes() == null) {
            estacionamento.setAvaliacoes(new ArrayList<>());
        }
        if (cliente.getAvaliacoes() == null) {
            cliente.setAvaliacoes(new ArrayList<>());
        }

        validador.validarAvaliacaoAposUso(avaliacao);
        validador.validarTamanhoDoComentario(avaliacao);

        avaliacao.setStatus(true);
        estacionamento.getAvaliacoes().add(avaliacao);
        cliente.getAvaliacoes().add(avaliacao);

        estacionamento.calcularNotaMedia();

        return AvaliacaoMapper.fromEntity(AvaliacaoDao.save(avaliacao));
    }

    @Transactional
    public AvaliacaoResponse atualizarAvaliacao(AvaliacaoRequest dto, Long id) {
        Avaliacao avaliacao = AvaliacaoDao.findById(id)
                .orElseThrow(() -> new IdNaoCadastrado("Id não encontrado!"));
        if (dto.clienteId() == null) {
            throw new IdNaoCadastrado("Cliente não cadastrado no sistema!");
        }
        Cliente cliente = ClienteDao.findById(dto.clienteId())
                .orElseThrow(() -> new IdNaoCadastrado("Cliente não cadastrado no sistema!"));
        if (dto.estacionamentoId() == null) {
            throw new IdNaoCadastrado("Estacionamento não encontrado no sistema!");
        }
        Estacionamento estacionamento = EstacionamentoDao.findById(dto.estacionamentoId())
                .orElseThrow(() -> new IdNaoCadastrado("Estacionamento não encontrado no sistema!"));

        validador.validarAvaliacaoAposUso(avaliacao);
        validador.validarTamanhoDoComentario(avaliacao);
        validador.validarTempoDeAvaliacao(avaliacao);

        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());
        if (dto.dataDeAvaliacao() != null) {
            avaliacao.setDataDeAvaliacao(dto.dataDeAvaliacao());
        }
        avaliacao.setCliente(cliente);
        avaliacao.setEstacionamento(estacionamento);

        estacionamento.calcularNotaMedia();

        return AvaliacaoMapper.fromEntity(AvaliacaoDao.save(avaliacao));
    }

    @Transactional
    public void deletarAvaliacao(Long id) {
        Optional<Avaliacao> optAvaliacao = AvaliacaoDao.findById(id);

        if (optAvaliacao.isEmpty()) {
            throw new IdNaoCadastrado("Não foi possivel encontrar a avalição buscada");
        }
        Avaliacao avaliacao = optAvaliacao.get();

        Optional<Estacionamento> optEstacio = EstacionamentoDao.findById(avaliacao.getEstacionamento().getId());
        Optional<Cliente> optCliente = ClienteDao.findById(avaliacao.getCliente().getId());

        if (optCliente.isEmpty() || optEstacio.isEmpty()) {
            throw new IdNaoCadastrado("Cliente ou estacionamento não encontrado no sistema");
        }

        Estacionamento estacionamento = optEstacio.get();
        Cliente cliente = optCliente.get();

        if (estacionamento.getAvaliacoes() != null) {
            estacionamento.getAvaliacoes().remove(avaliacao);
        }
        if (cliente.getAvaliacoes() != null) {
            cliente.getAvaliacoes().remove(avaliacao);
        }

        avaliacao.setStatus(false);
        estacionamento.calcularNotaMedia();
        AvaliacaoDao.save(avaliacao);
    }
}
