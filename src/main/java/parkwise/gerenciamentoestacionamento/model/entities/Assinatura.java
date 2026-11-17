package parkwise.gerenciamentoestacionamento.model.entities;

import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import parkwise.gerenciamentoestacionamento.model.enums.StatusAssinatura;
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
public class Assinatura {
    private Long id;
    private Cliente cliente;
    private PlanoEstacionamento plano;
    private Instant inicioEm;
    private Instant fimEm;
    private Instant proximaCobrancaEm;
    private Boolean renovacaoAutomatica;
    private StatusAssinatura status;
}
