package inpark.gerenciamentoestacionamento.model.entities;

import inpark.gerenciamentoestacionamento.model.enums.StatusCota;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "cota_empresarial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CotaEmpresarial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private ContratoEmpresarial contrato;

    @Column(nullable = false)
    private Integer quantidadeVagas;

    private String diasDaSemana;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private LocalDate vigenciaInicio;
    private LocalDate vigenciaFim;

    @Enumerated(EnumType.STRING)
    private StatusCota status;
}
