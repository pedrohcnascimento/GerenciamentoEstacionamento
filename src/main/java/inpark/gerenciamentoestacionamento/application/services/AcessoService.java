package inpark.gerenciamentoestacionamento.application.services;

import inpark.gerenciamentoestacionamento.application.dto.requests.AcessoRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.AcessoResponse;
import inpark.gerenciamentoestacionamento.application.mappers.AcessoMapper;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.AcessoRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.CarroRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.EstacionamentoRepository;
import inpark.gerenciamentoestacionamento.model.entities.Acesso;
import inpark.gerenciamentoestacionamento.model.entities.Carro;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.Valor;
import inpark.gerenciamentoestacionamento.model.enums.Cobranca;
import inpark.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AcessoService {
    private final AcessoRepository acessoRepository;
    private final EstacionamentoRepository estacionamentoRepository;
    private final CarroRepository carroRepository;

    public AcessoService(AcessoRepository acessoRepository,
                         EstacionamentoRepository estacionamentoRepository,
                         CarroRepository carroRepository) {
        this.acessoRepository = acessoRepository;
        this.estacionamentoRepository = estacionamentoRepository;
        this.carroRepository = carroRepository;
    }

    public List<AcessoResponse> listarAcessos() {
        return acessoRepository.findByStatusTrue()
                .stream()
                .map(AcessoMapper::fromEntity)
                .toList();
    }

    public AcessoResponse buscarPorId(Long id) {
        Optional<Acesso> optionalAcesso = acessoRepository.findById(id);

        if (optionalAcesso.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return AcessoMapper.fromEntity(optionalAcesso.get());
    }

    private void calcularValorAutomaticoSeAusente(Acesso acesso, Estacionamento estacionamento) {
        if (acesso.getValorAPagar() != null || estacionamento.getValores() == null) {
            return;
        }
        Optional<Valor> tarifaOpt = estacionamento.getValores().stream()
                .filter(v -> v != null && !Boolean.FALSE.equals(v.getStatus()) && v.getPreco() != null)
                .findFirst();
        if (tarifaOpt.isPresent()) {
            Valor tarifa = tarifaOpt.get();
            if (tarifa.getTipoDeCobranca() == Cobranca.PORHORA && acesso.getTotalHoras() != null) {
                acesso.setValorAPagar(tarifa.getPreco() * Math.max(1, acesso.getTotalHoras()));
            } else {
                acesso.setValorAPagar(tarifa.getPreco());
            }
        }
    }

    @Transactional
    public AcessoResponse cadastrarAcesso(AcessoRequest dto) {
        if (dto.estacioId() == null) {
            throw new IdNaoCadastrado("Id do estacionamento não encontrado no sistema");
        }
        Acesso acesso = AcessoMapper.toEntity(dto);
        if (acesso.getHoraDeEntrada() == null) {
            acesso.setHoraDeEntrada(LocalTime.now());
        }

        acesso.calcularHorasTotais();

        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(dto.estacioId());
        if (optEstacionamento.isEmpty()) {
            throw new IdNaoCadastrado("Id do estacionamento não encontrado no sistema");
        }

        Optional<Carro> optCarro = dto.placaDoCarro() != null
                ? carroRepository.findByPlaca(dto.placaDoCarro())
                : Optional.empty();
        if (optCarro.isPresent()) {
            acesso.setCarro(optCarro.get());
        } else if (dto.carroId() != null) {
            acesso.setCarro(carroRepository.findById(dto.carroId()).orElse(null));
        } else {
            acesso.setCarro(null);
        }

        Estacionamento estacionamento = optEstacionamento.get();
        if (estacionamento.getAcessos() == null) {
            estacionamento.setAcessos(new ArrayList<>());
        }

        calcularValorAutomaticoSeAusente(acesso, estacionamento);

        if (acesso.getValorAPagar() != null) {
            double atual = estacionamento.getValorArrecadadoDoDia() != null ? estacionamento.getValorArrecadadoDoDia() : 0.0;
            estacionamento.setValorArrecadadoDoDia(atual + acesso.getValorAPagar());
        }

        acesso.setEstacionamento(estacionamento);
        estacionamento.getAcessos().add(acesso);

        acesso.setStatus(true);
        return AcessoMapper.fromEntity(acessoRepository.save(acesso));
    }

    @Transactional
    public AcessoResponse atualizarAcesso(AcessoRequest dto, Long id) {
        if (dto.estacioId() == null) {
            throw new IdNaoCadastrado("O Acesso ou estacionamento buscado não existe no sistema");
        }
        Optional<Acesso> optAcesso = acessoRepository.findById(id);
        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(dto.estacioId());

        if (optAcesso.isEmpty() || optEstacionamento.isEmpty()) {
            throw new IdNaoCadastrado("O Acesso ou estacionamento buscado não existe no sistema");
        }

        Acesso acesso = optAcesso.get();
        Estacionamento estacionamento = optEstacionamento.get();
        if (estacionamento.getAcessos() == null) {
            estacionamento.setAcessos(new ArrayList<>());
        }

        acesso.setHoraDeEntrada(dto.horaDeEntrada());
        acesso.setHoraDeSaida(dto.horaDeSaida());
        acesso.calcularHorasTotais();
        acesso.setPlacaDoCarro(dto.placaDoCarro());
        acesso.setValorAPagar(dto.valorAPagar());
        calcularValorAutomaticoSeAusente(acesso, estacionamento);

        if (acesso.getEstacionamento() != null && !estacionamento.getId().equals(acesso.getEstacionamento().getId())) {
            if (acesso.getEstacionamento().getAcessos() != null) {
                acesso.getEstacionamento().getAcessos().remove(acesso);
            }
            estacionamento.getAcessos().add(acesso);
        }
        acesso.setEstacionamento(estacionamento);

        return AcessoMapper.fromEntity(acessoRepository.save(acesso));
    }

    @Transactional
    public void deletarAcesso(Long id) {
        Optional<Acesso> optAcesso = acessoRepository.findById(id);

        if (optAcesso.isEmpty()) {
            throw new IdNaoCadastrado("Acesso buscado não cadastrado no sistema");
        }

        Acesso acesso = optAcesso.get();
        Estacionamento estacionamento = acesso.getEstacionamento();

        if (estacionamento != null && estacionamento.getAcessos() != null) {
            estacionamento.getAcessos().remove(acesso);
        }

        acesso.setStatus(false);
        acessoRepository.save(acesso);
    }
}
