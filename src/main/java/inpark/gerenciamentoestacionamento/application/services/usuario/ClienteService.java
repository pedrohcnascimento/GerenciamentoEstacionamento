package inpark.gerenciamentoestacionamento.application.services.usuario;

import inpark.gerenciamentoestacionamento.application.dto.requests.usuario.ClienteRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.usuario.ClienteResponse;
import inpark.gerenciamentoestacionamento.application.mappers.usuario.ClienteMapper;
import inpark.gerenciamentoestacionamento.dao.ClienteDao;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import inpark.gerenciamentoestacionamento.model.exceptions.ConflitoDeRegraDeNegocioException;
import inpark.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteDao ClienteDao;
    private final PasswordEncoder passwordEncoder;

    public List<ClienteResponse> listarClientes() {
        return ClienteDao.findByStatusTrue()
                .stream()
                .map(ClienteMapper::fromEntity)
                .toList();
    }

    public ClienteResponse buscarPorId(Long id) {
        Optional<Cliente> optionalCliente = ClienteDao.findById(id);

        if (optionalCliente.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return ClienteMapper.fromEntity(optionalCliente.get());
    }

    public ClienteResponse cadastrarCliente(ClienteRequest dto) {
        if (dto.email() != null && ClienteDao.findByEmail(dto.email()).isPresent()) {
            throw new ConflitoDeRegraDeNegocioException("Já existe um cliente cadastrado com este e-mail.");
        }
        Cliente cliente = ClienteMapper.toEntity(dto);
        cliente.setSenha(passwordEncoder.encode(dto.senha()));
        cliente.setStatus(true);
        cliente.setTipoDeUsuario(TipoDeUsuario.CLIENTE);

        Cliente salvo = ClienteDao.save(cliente);
        return ClienteMapper.fromEntity(salvo);
    }

    public ClienteResponse atualizarCliente(ClienteRequest dto, Long id) {
        Cliente cliente = ClienteDao.findById(id)
                .orElseThrow(() -> new IdNaoCadastrado("Id buscado não foi encontrado"));

        if (dto.senha() != null) {
            cliente.setSenha(passwordEncoder.encode(dto.senha()));
        }

        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setDataNascimento(dto.dataNascimento());

        Cliente salvo = ClienteDao.save(cliente);
        return ClienteMapper.fromEntity(salvo);
    }

    public void deletarCliente(Long id) {
        Optional<Cliente> optCliente = ClienteDao.findById(id);

        if (optCliente.isEmpty()) {
            throw new IdNaoCadastrado("Cliente buscado não cadastrado no sistema");
        }

        Cliente cliente = optCliente.get();

        cliente.setStatus(false);
        ClienteDao.save(cliente);
    }
}
