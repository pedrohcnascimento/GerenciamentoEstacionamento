package inpark.gerenciamentoestacionamento.model.entities;

import inpark.gerenciamentoestacionamento.model.enums.StatusContrato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ContratoEmpresarial {
    private Long id;
    private Empresa empresa;
    private Estacionamento estacionamento;
    private LocalDate inicioVigencia;
    private LocalDate fimVigencia;
    private BigDecimal valorMensal;
    private Integer prazoPagamentoDias;
    private StatusContrato status;

    @Builder.Default
    private List<CotaEmpresarial> cotas = new ArrayList<>();
}
