package parkwise.gerenciamentoestacionamento.model.entities;

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
public class UtilizacaoCota {
    private Long id;
    private CotaEmpresarial cota;
    private ColaboradorEmpresa colaborador;
    private Carro veiculo;
    private Instant entradaEm;
    private Instant saidaEm;
    private Boolean status;
}
