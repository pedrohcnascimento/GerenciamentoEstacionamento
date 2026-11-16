package inpark.gerenciamentoestacionamento.model.entities;

import inpark.gerenciamentoestacionamento.model.enums.StatusAnalise;
import inpark.gerenciamentoestacionamento.model.enums.TipoDivergencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "divergencia_financeira")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DivergenciaFinanceira {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioFinanceiro relatorio;

    @Enumerated(EnumType.STRING)
    private TipoDivergencia tipo;

    private String descricao;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorEsperado;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorIdentificado;

    @Column(precision = 10, scale = 2)
    private BigDecimal diferenca;

    @Enumerated(EnumType.STRING)
    private StatusAnalise status;

    private String observacaoAnalista;
}
