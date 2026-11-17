package parkwise.gerenciamentoestacionamento.model.entities;

import parkwise.gerenciamentoestacionamento.model.enums.StatusBloqueio;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BloqueioCapacidade {
    private Long id;
    private Estacionamento estacionamento;
    private Integer quantidadeVagas;
    private Instant inicioEm;
    private Instant fimEm;
    private String motivo;
    private StatusBloqueio status;
}
