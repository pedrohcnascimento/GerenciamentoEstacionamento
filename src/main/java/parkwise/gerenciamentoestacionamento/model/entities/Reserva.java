package parkwise.gerenciamentoestacionamento.model.entities;

import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import parkwise.gerenciamentoestacionamento.model.enums.OrigemReserva;
import parkwise.gerenciamentoestacionamento.model.enums.StatusReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.Instant;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Reserva {
    private Long id;
    private Cliente cliente;
    private Estacionamento estacionamento;
    private Carro veiculo;
    private Assinatura assinatura;
    private Date dataDaReserva;
    private Time horaDaReserva;
    private Instant inicioEm;
    private Instant fimEm;
    private OrigemReserva origem;
    private BigDecimal precoConfirmado;

    @Builder.Default
    private Instant criadaEm = Instant.now();

    private Instant canceladaEm;
    private StatusReserva statusReserva;
    private Boolean status;
}
