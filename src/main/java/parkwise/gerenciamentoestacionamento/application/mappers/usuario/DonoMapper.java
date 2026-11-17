package parkwise.gerenciamentoestacionamento.application.mappers.usuario;

import parkwise.gerenciamentoestacionamento.application.dto.requests.usuario.DonoRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.usuario.DonoResponse;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import parkwise.gerenciamentoestacionamento.model.enums.Role;

public class DonoMapper {

    public static DonoEstacionamento toEntity(DonoRequest dto) {
        DonoEstacionamento dono = new DonoEstacionamento();

        dono.setNome(dto.nome());
        dono.setEmail(dto.email());
        dono.setSenha(dto.senha());
        dono.setDataNascimento(dto.dataNascimento());
        dono.setRole(Role.DONO);

        return dono;
    }

    public static DonoResponse fromEntity(DonoEstacionamento dono) {
        return new DonoResponse(
                dono.getId(),
                dono.getNome(),
                dono.getEmail(),
                dono.getSenha(),
                dono.getDataNascimento(),
                dono.getStatus()
        );
    }
}
