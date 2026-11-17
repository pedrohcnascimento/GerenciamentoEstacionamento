package parkwise.gerenciamentoestacionamento.application.mappers.usuario;

import parkwise.gerenciamentoestacionamento.application.dto.requests.usuario.ClienteRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.usuario.ClienteResponse;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import parkwise.gerenciamentoestacionamento.model.enums.Role;

public class ClienteMapper {

    public static Cliente toEntity(ClienteRequest dto) {
        Cliente cliente = new Cliente();

        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setDataNascimento(dto.dataNascimento());
        cliente.setSenha(dto.senha());
        cliente.setRole(Role.CLIENTE);

        return cliente;
    }

    public static ClienteResponse fromEntity(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getSenha(),
                cliente.getDataNascimento(),
                cliente.getStatus()
        );
    }
}
