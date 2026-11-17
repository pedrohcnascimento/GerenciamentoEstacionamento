package inpark.gerenciamentoestacionamento.application.services.usuario;

import inpark.gerenciamentoestacionamento.application.dto.requests.usuario.GerenteRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.usuario.GerenteResponse;
import inpark.gerenciamentoestacionamento.application.mappers.usuario.GerenteMapper;
import inpark.gerenciamentoestacionamento.dao.EstacionamentoDao;
import inpark.gerenciamentoestacionamento.dao.GerenteDao;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Gerente;
import inpark.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import inpark.gerenciamentoestacionamento.model.exceptions.ConflitoDeRegraDeNegocioException;
import inpark.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GerenteService {
    private final GerenteDao GerenteDao;
    private final PasswordEncoder passwordEncoder;
    private final EstacionamentoDao EstacionamentoDao;

    public List<GerenteResponse> listarGerentes() {
        return GerenteDao.findByStatusTrue()
                .stream()
                .map(GerenteMapper::fromEntity)
                .toList();
    }

    public GerenteResponse buscarPorId(Long id) {
        Optional<Gerente> optionalGerente = GerenteDao.findById(id);

        if (optionalGerente.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return GerenteMapper.fromEntity(optionalGerente.get());
    }

    public GerenteResponse cadastrarGerente(GerenteRequest dto) {
        if (dto.estacionamentoId() == null) {
            throw new IdNaoCadastrado("Id do estacionamento não encontrado no sistema");
        }
        if (dto.email() != null && GerenteDao.findByEmail(dto.email()).isPresent()) {
            throw new ConflitoDeRegraDeNegocioException("Já existe um gerente cadastrado com este e-mail.");
        }
        Gerente gerente = GerenteMapper.toEntity(dto);
        gerente.setSenha(passwordEncoder.encode(dto.senha()));
        Estacionamento estacionamento = EstacionamentoDao.findById(dto.estacionamentoId())
                .orElseThrow(() -> new IdNaoCadastrado("Id do estacionamento não encontrado no sistema"));

        if (estacionamento.getGerentes() == null) {
            estacionamento.setGerentes(new ArrayList<>());
        }
        estacionamento.getGerentes().add(gerente);
        gerente.setEstacionamento(estacionamento);
        gerente.setStatus(true);
        gerente.setTipoDeUsuario(TipoDeUsuario.GERENTE);
        Gerente salvo = GerenteDao.save(gerente);

        return GerenteMapper.fromEntity(salvo);
    }

    public GerenteResponse atualizarGerente(GerenteRequest dto, Long id) {
        var optGerente = GerenteDao.findById(id);

        if (optGerente.isEmpty()) {
            throw new IdNaoCadastrado("Id do gerente buscado não encontrado no sistema!");
        }

        Gerente gerente = optGerente.get();

        gerente.setNome(dto.nome());
        gerente.setEmail(dto.email());
        if (dto.senha() != null && !dto.senha().isBlank()) {
            gerente.setSenha(passwordEncoder.encode(dto.senha()));
        }
        gerente.setDataNascimento(dto.dataNascimento());
        if (dto.cpfOuCnpj() != null) {
            gerente.setCpfOuCnpj(dto.cpfOuCnpj());
        }

        Long targetEstId = gerente.getEstacionamento() != null
                ? gerente.getEstacionamento().getId()
                : dto.estacionamentoId();
        if (targetEstId == null) {
            throw new IdNaoCadastrado("Estacionamento desejado a adicionar não cadastrado no sistema!");
        }
        Optional<Estacionamento> optEstacionamento = EstacionamentoDao.findById(targetEstId);

        if (optEstacionamento.isEmpty()) {
            throw new IdNaoCadastrado("Estacionamento desejado a adicionar não cadastrado no sistema!");
        }

        Estacionamento estacionamento = optEstacionamento.get();
        if (estacionamento.getGerentes() == null) {
            estacionamento.setGerentes(new ArrayList<>());
        }

        gerente.setEstacionamento(estacionamento);
        if (!estacionamento.getGerentes().contains(gerente)) {
            estacionamento.getGerentes().add(gerente);
        }

        return GerenteMapper.fromEntity(GerenteDao.save(gerente));
    }

    public void deletarGerente(Long id) {
        Optional<Gerente> optGerente = GerenteDao.findById(id);

        if (optGerente.isEmpty()) {
            throw new IdNaoCadastrado("Id do gerente buscado não encontrado no sistema!");
        }

        Gerente gerente = optGerente.get();
        if (gerente.getEstacionamento() != null && gerente.getEstacionamento().getGerentes() != null) {
            gerente.getEstacionamento().getGerentes().remove(gerente);
        }
        gerente.setStatus(false);
        GerenteDao.save(gerente);
    }
}
