package inpark.gerenciamentoestacionamento.model.entities;

import inpark.gerenciamentoestacionamento.model.enums.StatusAnalise;
import inpark.gerenciamentoestacionamento.model.enums.TipoDivergencia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DivergenciaFinanceira {
    private Long id;
    private RelatorioFinanceiro relatorio;
    private TipoDivergencia tipo;
    private String descricao;
    private BigDecimal valorEsperado;
    private BigDecimal valorIdentificado;
    private BigDecimal diferenca;
    private StatusAnalise status;
    private String observacaoAnalista;
}
