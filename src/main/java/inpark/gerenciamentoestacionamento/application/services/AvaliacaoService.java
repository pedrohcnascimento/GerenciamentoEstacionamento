package inpark.gerenciamentoestacionamento.application.services;

import inpark.gerenciamentoestacionamento.application.dto.requests.AvaliacaoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.AvaliacaoResponse;
import inpark.gerenciamentoestacionamento.application.mappers.AvaliacaoMapper;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.AvaliacaoRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.EstacionamentoRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.ClienteRepository;
import inpark.gerenciamentoestacionamento.model.entities.Avaliacao;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import inpark.gerenciamentoestacionamento.model.service.ValidadorAvaliacao;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {
    private final AvaliacaoRepository avaliacaoRepository;
    private final EstacionamentoRepository estacionamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ValidadorAvaliacao validador;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            EstacionamentoRepository estacionamentoRepository,
                            ClienteRepository clienteRepository,
                            ValidadorAvaliacao validador) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.estacionamentoRepository = estacionamentoRepository;
        this.clienteRepository = clienteRepository;
        this.validador = validador;
    }

    public List<AvaliacaoResponse> listarAvaliacoes() {
        return avaliacaoRepository.findByStatusTrue()
                .stream()
                .map(AvaliacaoMapper::fromEntity)
                .toList();
    }

    public AvaliacaoResponse buscarPorId(Long id) {
        Optional<Avaliacao> optionalAvaliacao = avaliacaoRepository.findById(id);

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

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new IdNaoCadastrado("Cliente não encontrado no sistema"));

        if (dto.estacionamentoId() == null) {
            throw new IdNaoCadastrado("Estacionamento não encontrado no sistema");
        }
        Estacionamento estacionamento = estacionamentoRepository.findById(dto.estacionamentoId())
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

        return AvaliacaoMapper.fromEntity(avaliacaoRepository.save(avaliacao));
    }

    @Transactional
    public AvaliacaoResponse atualizarAvaliacao(AvaliacaoRequest dto, Long id) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new IdNaoCadastrado("Id não encontrado!"));
        if (dto.clienteId() == null) {
            throw new IdNaoCadastrado("Cliente não cadastrado no sistema!");
        }
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new IdNaoCadastrado("Cliente não cadastrado no sistema!"));
        if (dto.estacionamentoId() == null) {
            throw new IdNaoCadastrado("Estacionamento não encontrado no sistema!");
        }
        Estacionamento estacionamento = estacionamentoRepository.findById(dto.estacionamentoId())
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

        return AvaliacaoMapper.fromEntity(avaliacaoRepository.save(avaliacao));
    }

    @Transactional
    public void deletarAvaliacao(Long id) {
        Optional<Avaliacao> optAvaliacao = avaliacaoRepository.findById(id);

        if (optAvaliacao.isEmpty()) {
            throw new IdNaoCadastrado("Não foi possivel encontrar a avalição buscada");
        }
        Avaliacao avaliacao = optAvaliacao.get();

        Optional<Estacionamento> optEstacio = estacionamentoRepository.findById(avaliacao.getEstacionamento().getId());
        Optional<Cliente> optCliente = clienteRepository.findById(avaliacao.getCliente().getId());

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
        avaliacaoRepository.save(avaliacao);
    }
}
