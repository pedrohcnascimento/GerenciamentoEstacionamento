package parkwise.gerenciamentoestacionamento.model.entities;

import parkwise.gerenciamentoestacionamento.model.enums.Cobranca;
import parkwise.gerenciamentoestacionamento.model.enums.Metodo;
import parkwise.gerenciamentoestacionamento.model.enums.Periodo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Valor {
    private Long id;
    private Cobranca tipoDeCobranca;
    private Metodo tipoDePagamento;
    private Double preco;
    private Periodo periodo;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private LocalDate vigenciaInicio;
    private LocalDate vigenciaFim;
    private String diasDaSemana;
    private Boolean status;
    private Estacionamento estacionamento;
}
