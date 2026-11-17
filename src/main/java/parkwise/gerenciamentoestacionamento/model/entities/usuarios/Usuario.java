package parkwise.gerenciamentoestacionamento.model.entities.usuarios;

import parkwise.gerenciamentoestacionamento.model.enums.Role;
import parkwise.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private Date dataNascimento;
    private TipoDeUsuario tipoDeUsuario;
    private Boolean status;
    protected Role role;

    @Builder.Default
    private Instant criadoEm = Instant.now();
}
