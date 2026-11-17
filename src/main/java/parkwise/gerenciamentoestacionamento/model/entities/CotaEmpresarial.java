package parkwise.gerenciamentoestacionamento.model.entities;

import parkwise.gerenciamentoestacionamento.model.enums.StatusCota;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CotaEmpresarial {
    private Long id;
    private ContratoEmpresarial contrato;
    private Integer quantidadeVagas;
    private String diasDaSemana;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private LocalDate vigenciaInicio;
    private LocalDate vigenciaFim;
    private StatusCota status;
}
