package inpark.gerenciamentoestacionamento.model.entities;

import inpark.gerenciamentoestacionamento.model.enums.StatusImportacao;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RelatorioFinanceiro {
    private Long id;
    private Estacionamento estacionamento;
    private String nomeArquivo;
    private String formato;
    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private Instant importadoEm;
    private StatusImportacao status;

    @Builder.Default
    private List<DivergenciaFinanceira> divergencias = new ArrayList<>();
}
