package inpark.gerenciamentoestacionamento.model.entities.usuarios;

import inpark.gerenciamentoestacionamento.model.enums.Role;
import inpark.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import jakarta.persistence.*;
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
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    private String telefone;

    @Column(name = "data_nascimento", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    @Column(name = "tipo_usuario")
    @Enumerated(EnumType.STRING)
    private TipoDeUsuario tipoDeUsuario;

    private Boolean status;

    @Column
    @Enumerated(EnumType.STRING)
    protected Role role;

    @Builder.Default
    @Column(name = "criado_em")
    private Instant criadoEm = Instant.now();
}
