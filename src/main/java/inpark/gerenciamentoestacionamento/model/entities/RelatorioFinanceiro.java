package inpark.gerenciamentoestacionamento.model.entities;

import inpark.gerenciamentoestacionamento.model.enums.StatusImportacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "relatorio_financeiro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RelatorioFinanceiro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacionamento_id", nullable = false)
    private Estacionamento estacionamento;

    private String nomeArquivo;
    private String formato;
    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private Instant importadoEm;

    @Enumerated(EnumType.STRING)
    private StatusImportacao status;

    @Builder.Default
    @OneToMany(mappedBy = "relatorio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DivergenciaFinanceira> divergencias = new ArrayList<>();
}
