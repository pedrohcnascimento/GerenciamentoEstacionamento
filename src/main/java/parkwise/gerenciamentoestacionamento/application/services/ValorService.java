package parkwise.gerenciamentoestacionamento.application.services;

import parkwise.gerenciamentoestacionamento.application.dto.requests.ValorRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.ValorResponse;
import parkwise.gerenciamentoestacionamento.application.mappers.ValorMapper;
import parkwise.gerenciamentoestacionamento.dao.EstacionamentoDao;
import parkwise.gerenciamentoestacionamento.dao.ValorDao;
import parkwise.gerenciamentoestacionamento.model.entities.Estacionamento;
import parkwise.gerenciamentoestacionamento.model.entities.Valor;
import parkwise.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ValorService {
    private final ValorDao ValorDao;
    private final EstacionamentoDao EstacionamentoDao;

    public ValorService(ValorDao ValorDao, EstacionamentoDao EstacionamentoDao) {
        this.ValorDao = ValorDao;
        this.EstacionamentoDao = EstacionamentoDao;
    }

    public List<ValorResponse> listarValor() {
        return ValorDao.findByStatusTrue()
                .stream()
                .map(ValorMapper::fromEntity)
                .toList();
    }

    public List<ValorResponse> listarPorEstacionamento(Long estacionamentoId) {
        if (estacionamentoId == null || EstacionamentoDao.findById(estacionamentoId).isEmpty()) {
            throw new IdNaoCadastrado("Id de estacionamento especificado não encontrado no sistema");
        }
        return ValorDao.findByEstacionamentoId(estacionamentoId)
                .stream()
                .map(ValorMapper::fromEntity)
                .toList();
    }

    public ValorResponse buscarPorId(Long id) {
        Optional<Valor> optionalValor = ValorDao.findById(id);

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
        Optional<Estacionamento> optEstacionamento = EstacionamentoDao.findById(dto.estacionamentoId());
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
        return ValorMapper.fromEntity(ValorDao.save(novoValor));
    }

    @Transactional
    public ValorResponse atualizarValor(ValorRequest dto, Long id) {
        if (dto.estacionamentoId() == null) {
            throw new IdNaoCadastrado("ID de valor ou estacionamento buscados não encontrados");
        }
        Optional<Valor> optValor = ValorDao.findById(id);
        Optional<Estacionamento> optEstacionamento = EstacionamentoDao.findById(dto.estacionamentoId());

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

        return ValorMapper.fromEntity(ValorDao.save(valor));
    }

    @Transactional
    public void deletarValor(Long id) {
        Optional<Valor> optionalValor = ValorDao.findById(id);

        if (optionalValor.isEmpty()) {
            throw new IdNaoCadastrado("Id de valor especificado não encontrado no sistema");
        }

        Valor valor = optionalValor.get();

        valor.setStatus(false);
        ValorDao.save(valor);
    }
}
