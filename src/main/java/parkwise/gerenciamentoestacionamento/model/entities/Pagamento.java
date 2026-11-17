package parkwise.gerenciamentoestacionamento.model.entities;

import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import parkwise.gerenciamentoestacionamento.model.enums.Metodo;
import parkwise.gerenciamentoestacionamento.model.enums.StatusPagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Pagamento {
    private Long id;
    private Cliente pagador;
    private Reserva reserva;
    private Assinatura assinatura;
    private ContratoEmpresarial contrato;
    private BigDecimal valor;
    private Metodo metodo;
    private String provedor;
    private String transacaoExternaId;
    private StatusPagamento status;
    private Instant criadoEm;
    private Instant confirmadoEm;
}
