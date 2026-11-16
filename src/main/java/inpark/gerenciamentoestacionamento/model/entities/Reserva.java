package inpark.gerenciamentoestacionamento.model.entities;

import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.enums.OrigemReserva;
import inpark.gerenciamentoestacionamento.model.enums.StatusReserva;
import jakarta.persistence.*;
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

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "estacionamento_id")
    private Estacionamento estacionamento;

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Carro veiculo;

    @ManyToOne
    @JoinColumn(name = "assinatura_id")
    private Assinatura assinatura;

    @Column(name = "data_reserva")
    private Date dataDaReserva;

    @Column(name = "hora_reserva")
    private Time horaDaReserva;

    @Column(name = "inicio_em")
    private Instant inicioEm;

    @Column(name = "fim_em")
    private Instant fimEm;

    @Enumerated(EnumType.STRING)
    private OrigemReserva origem;

    @Column(name = "preco_confirmado", precision = 10, scale = 2)
    private BigDecimal precoConfirmado;

    @Builder.Default
    @Column(name = "criada_em")
    private Instant criadaEm = Instant.now();

    @Column(name = "cancelada_em")
    private Instant canceladaEm;

    @Column(name = "status_reserva")
    @Enumerated(EnumType.STRING)
    private StatusReserva statusReserva;

    private Boolean status;
}
