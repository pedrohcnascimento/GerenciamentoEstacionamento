package inpark.gerenciamentoestacionamento.application.services;

import inpark.gerenciamentoestacionamento.application.dto.requests.ValorRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.ValorResponse;
import inpark.gerenciamentoestacionamento.application.mappers.ValorMapper;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.EstacionamentoRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.ValorRepository;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.Valor;
import inpark.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ValorService {
    private final ValorRepository valorRepository;
    private final EstacionamentoRepository estacionamentoRepository;

    public ValorService(ValorRepository valorRepository, EstacionamentoRepository estacionamentoRepository) {
        this.valorRepository = valorRepository;
        this.estacionamentoRepository = estacionamentoRepository;
    }

    public List<ValorResponse> listarValor() {
        return valorRepository.findByStatusTrue()
                .stream()
                .map(ValorMapper::fromEntity)
                .toList();
    }

    public ValorResponse buscarPorId(Long id) {
        Optional<Valor> optionalValor = valorRepository.findById(id);

        if (optionalValor.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return ValorMapper.fromEntity(optionalValor.get());
    }

    @Transactional
    public ValorResponse cadastrarValor(ValorRequest dto) {
        if (dto.estacionamentoId() == null) {
            throw new IdNaoCadastrado("Id de estacionamento especificado não encontrado no sistema");
        }
        Valor novoValor = ValorMapper.toEntity(dto);
        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(dto.estacionamentoId());
        if (optEstacionamento.isEmpty()) {
            throw new IdNaoCadastrado("Id de estacionamento especificado não encontrado no sistema");
        }
        Estacionamento estacionamento = optEstacionamento.get();

        if (estacionamento.getValores() == null) {
            estacionamento.setValores(new ArrayList<>());
        }

        novoValor.setEstacionamento(estacionamento);
        estacionamento.getValores().add(novoValor);

        novoValor.setStatus(true);
        return ValorMapper.fromEntity(valorRepository.save(novoValor));
    }

    @Transactional
    public ValorResponse atualizarValor(ValorRequest dto, Long id) {
        if (dto.estacionamentoId() == null) {
            throw new IdNaoCadastrado("ID de valor ou estacionamento buscados não encontrados");
        }
        Optional<Valor> optValor = valorRepository.findById(id);
        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(dto.estacionamentoId());

        if (optEstacionamento.isEmpty() || optValor.isEmpty()) {
            throw new IdNaoCadastrado("ID de valor ou estacionamento buscados não encontrados");
        }

        Estacionamento estacionamento = optEstacionamento.get();
        Valor valor = optValor.get();

        if (estacionamento.getValores() == null) {
            estacionamento.setValores(new ArrayList<>());
        }

        valor.setEstacionamento(estacionamento);
        valor.setPeriodo(dto.periodo());
        valor.setPreco(dto.preco());
        valor.setTipoDePagamento(dto.tipoDePagamento());
        valor.setTipoDeCobranca(dto.tipoDeCobranca());

        if (!estacionamento.getValores().contains(valor)) {
            estacionamento.getValores().add(valor);
        }

        return ValorMapper.fromEntity(valorRepository.save(valor));
    }

    @Transactional
    public void deletarValor(Long id) {
        Optional<Valor> optionalValor = valorRepository.findById(id);

        if (optionalValor.isEmpty()) {
            throw new IdNaoCadastrado("Id de valor especificado não encontrado no sistema");
        }

        Valor valor = optionalValor.get();

        valor.setStatus(false);
        valorRepository.save(valor);
    }
}
