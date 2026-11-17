package parkwise.gerenciamentoestacionamento.application.services.usuario;

import parkwise.gerenciamentoestacionamento.application.dto.requests.usuario.DonoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.usuario.DonoResponse;
import parkwise.gerenciamentoestacionamento.application.mappers.usuario.DonoMapper;
import parkwise.gerenciamentoestacionamento.dao.DonoDao;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import parkwise.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import parkwise.gerenciamentoestacionamento.model.exceptions.ConflitoDeRegraDeNegocioException;
import parkwise.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DonoService {
    private final DonoDao DonoDao;
    private final PasswordEncoder passwordEncoder;

    public List<DonoResponse> listarDonos() {
        return DonoDao.findByStatusTrue()
                .stream()
                .map(DonoMapper::fromEntity)
                .toList();
    }

    public DonoResponse buscarPorId(Long id) {
        Optional<DonoEstacionamento> optionalDono = DonoDao.findById(id);

        if (optionalDono.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return DonoMapper.fromEntity(optionalDono.get());
    }

    @Transactional
    public DonoResponse cadastrarDono(DonoRequest dto) {
        if (dto.email() != null && DonoDao.findByEmail(dto.email()).isPresent()) {
            throw new ConflitoDeRegraDeNegocioException("Já existe um proprietário cadastrado com este e-mail.");
        }
        DonoEstacionamento dono = DonoMapper.toEntity(dto);
        dono.setSenha(passwordEncoder.encode(dto.senha()));
        dono.setStatus(true);
        dono.setTipoDeUsuario(TipoDeUsuario.DONO);
        DonoEstacionamento salvo = DonoDao.save(dono);

        return DonoMapper.fromEntity(salvo);
    }

    @Transactional
    public DonoResponse atualizarDono(DonoRequest dto, Long id) {
        Optional<DonoEstacionamento> optDono = DonoDao.findById(id);

        if (optDono.isEmpty()) {
            throw new IdNaoCadastrado("O Id não foi encontrado no sistema!");
        }

        DonoEstacionamento dono = optDono.get();

        if (dto.senha() != null) {
            dono.setSenha(passwordEncoder.encode(dto.senha()));
        }

        dono.setNome(dto.nome());
        dono.setEmail(dto.email());
        dono.setDataNascimento(dto.dataNascimento());

        return DonoMapper.fromEntity(DonoDao.save(dono));
    }

    @Transactional
    public void deletarDono(Long id) {
        Optional<DonoEstacionamento> optDono = DonoDao.findById(id);

        if (optDono.isEmpty()) {
            throw new IdNaoCadastrado("Dono buscado não cadastrado no sistema");
        }

        DonoEstacionamento dono = optDono.get();

        dono.setStatus(false);
        DonoDao.save(dono);
    }
}
