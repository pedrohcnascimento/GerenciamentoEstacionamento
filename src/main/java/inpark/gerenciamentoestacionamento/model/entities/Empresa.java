package inpark.gerenciamentoestacionamento.model.entities;

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
public class Empresa {
    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String emailContato;
    private String telefoneContato;
    private Boolean status;
}
