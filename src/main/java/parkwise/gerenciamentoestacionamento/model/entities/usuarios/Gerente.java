package parkwise.gerenciamentoestacionamento.model.entities.usuarios;

import parkwise.gerenciamentoestacionamento.model.entities.Estacionamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@SuperBuilder
public class Gerente extends Usuario {
    private String cpfOuCnpj;
    private Estacionamento estacionamento;
}
