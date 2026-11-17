package parkwise.gerenciamentoestacionamento.application.services;

import parkwise.gerenciamentoestacionamento.application.dto.requests.EstacionamentoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.EstacionamentoResponse;
import parkwise.gerenciamentoestacionamento.application.mappers.EstacionamentoMapper;
import parkwise.gerenciamentoestacionamento.dao.EstacionamentoDao;
import parkwise.gerenciamentoestacionamento.dao.DonoDao;
import parkwise.gerenciamentoestacionamento.model.entities.Estacionamento;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import parkwise.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EstacionamentoService {
    private final EstacionamentoDao EstacionamentoDao;
    private final DonoDao DonoDao;

    public EstacionamentoService(EstacionamentoDao EstacionamentoDao, DonoDao DonoDao) {
        this.EstacionamentoDao = EstacionamentoDao;
        this.DonoDao = DonoDao;
    }

    public List<EstacionamentoResponse> listarTodosOsEstacionamentos() {
        return EstacionamentoDao.findByStatusTrue()
                .stream()
                .map(EstacionamentoMapper::fromEntity)
                .toList();
    }

    public List<EstacionamentoResponse> listarPorDono(Long donoId) {
        if (donoId == null || DonoDao.findById(donoId).isEmpty()) {
            throw new IdNaoCadastrado("O Id do dono fornecido não foi encontrado no sistema!");
        }
        return EstacionamentoDao.findByDono_Id(donoId)
                .stream()
                .map(EstacionamentoMapper::fromEntity)
                .toList();
    }

    public EstacionamentoResponse buscarPorId(Long id) {
        Estacionamento estacionamento = EstacionamentoDao.findById(id)
                .orElseThrow(() -> new IdNaoCadastrado("ID buscado não foi encontrado no sistema!"));

        return EstacionamentoMapper.fromEntity(estacionamento);
    }

    @Transactional
    public EstacionamentoResponse cadastrarEstacionamento(EstacionamentoRequest dto, Long id) {
        if (id == null) {
            throw new IdNaoCadastrado("O Id do dono fornecido não foi encontrado no sistema!");
        }
        DonoEstacionamento dono = DonoDao.findById(id)
                .orElseThrow(() -> new IdNaoCadastrado("O Id do dono fornecido não foi encontrado no sistema!"));

        Estacionamento novoEst = EstacionamentoMapper.toEntity(dto);

        if (dono.getEstacionamentos() == null) {
            dono.setEstacionamentos(new ArrayList<>());
        }

        novoEst.setDono(dono);
        dono.getEstacionamentos().add(novoEst);
        novoEst.setFuncionamento(true);
        novoEst.setStatus(true);
        novoEst.setDiaAtual(LocalDate.now());
        if (novoEst.getValorArrecadadoDoDia() == null) {
            novoEst.setValorArrecadadoDoDia(0.0);
        }
        if (novoEst.getNotaMedia() == null) {
            novoEst.setNotaMedia(0.0);
        }
        if (novoEst.getQuantidadeDeAvaliacoes() == null) {
            novoEst.setQuantidadeDeAvaliacoes(0);
        }
        if (novoEst.getVagasDisponiveis() == null && novoEst.getMaxVagas() != null) {
            novoEst.setVagasDisponiveis(novoEst.calcularCapacidadeOperacional());
        }

        return EstacionamentoMapper.fromEntity(EstacionamentoDao.save(novoEst));
    }

    @Transactional
    public EstacionamentoResponse atualizarEstacionamento(EstacionamentoRequest dto, Long id) {
        Estacionamento estacionamento = EstacionamentoDao.findById(id)
                .orElseThrow(() -> new IdNaoCadastrado("O Id do estacionamento fornecido não foi encontrado no sistema!"));

        if (dto.nome() != null) {
            estacionamento.setNome(dto.nome());
        }
        if (dto.endereco() != null) {
            estacionamento.setEndereco(dto.endereco());
        }
        if (dto.CEP() != null) {
            estacionamento.setCEP(dto.CEP());
        }
        if (dto.numero() != null) {
            estacionamento.setNumero(dto.numero());
        }
        estacionamento.setFotoUrl(dto.foto());
        if (dto.numeroAlvaraDeFuncionamento() != null) {
            estacionamento.setNumeroAlvaraDeFuncionamento(dto.numeroAlvaraDeFuncionamento());
        }
        if (dto.horaAbertura() != null) {
            estacionamento.setHoraAbertura(dto.horaAbertura());
        }
        if (dto.horaFechamento() != null) {
            estacionamento.setHoraFechamento(dto.horaFechamento());
        }
        if (dto.vagasPreferenciais() != null) {
            estacionamento.setVagasPreferenciais(dto.vagasPreferenciais());
        }
        if (dto.maximoDeVagas() != null) {
            int diferenca = dto.maximoDeVagas() - (estacionamento.getMaxVagas() != null ? estacionamento.getMaxVagas() : 0);
            estacionamento.setMaxVagas(dto.maximoDeVagas());
            if (estacionamento.getVagasDisponiveis() != null) {
                estacionamento.setVagasDisponiveis(Math.max(0, estacionamento.getVagasDisponiveis() + diferenca));
            } else {
                estacionamento.setVagasDisponiveis(dto.maximoDeVagas());
            }
        }
        if (dto.numeroDeEscrituraImovel() != null) {
            estacionamento.setNumeroDeEscrituraImovel(dto.numeroDeEscrituraImovel());
        }

        return EstacionamentoMapper.fromEntity(EstacionamentoDao.save(estacionamento));
    }

    @Transactional
    public void desativarEstacionamento(Long id) {
        Estacionamento estacionamento = EstacionamentoDao.findById(id)
                .orElseThrow(() -> new IdNaoCadastrado("O Id do estacionamento fornecido não foi encontrado no sistema!"));

        if (estacionamento.getDono() != null) {
            if (estacionamento.getDono().getEstacionamentos() == null) {
                estacionamento.getDono().setEstacionamentos(new ArrayList<>());
            }
            estacionamento.getDono().getEstacionamentos().remove(estacionamento);
        }
        estacionamento.setStatus(false);
        EstacionamentoDao.save(estacionamento);
    }
}
