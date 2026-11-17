package parkwise.gerenciamentoestacionamento.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ColaboradorEmpresa {
    private Long id;
    private Empresa empresa;
    private String nome;
    private String email;
    private String documentoHash;
    private Boolean autorizado;
}
