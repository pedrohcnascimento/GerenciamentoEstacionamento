package inpark.gerenciamentoestacionamento.application.mappers.usuario;

import inpark.gerenciamentoestacionamento.application.dto.requests.usuario.UsuarioRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.usuario.UsuarioResponse;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Gerente;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Usuario;
import inpark.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import inpark.gerenciamentoestacionamento.model.exceptions.TipoDeUsuarioInvalido;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequest dto) {
        Usuario usuario = switch (dto.tipoDeUsuario()) {
            case GERENTE -> new Gerente();
            case DONO -> new DonoEstacionamento();
            case CLIENTE -> new Cliente();
        };

        usuario.setNome(dto.nome());
        usuario.setSenha(dto.senha());
        usuario.setEmail(dto.email());
        usuario.setDataNascimento(dto.dataNascimento());

        return usuario;
    }

    public static UsuarioResponse fromEntity(Usuario user) {
        TipoDeUsuario tipo = switch (user) {
            case Gerente gerente -> TipoDeUsuario.GERENTE;
            case DonoEstacionamento dono -> TipoDeUsuario.DONO;
            case Cliente cliente -> TipoDeUsuario.CLIENTE;
            default -> throw new TipoDeUsuarioInvalido("O tipo de usuario da classe é invalido!");
        };

        return new UsuarioResponse(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getSenha(),
                user.getDataNascimento(),
                tipo,
                user.getStatus()
        );
    }
}
