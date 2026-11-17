package parkwise.gerenciamentoestacionamento.model.entities;

import parkwise.gerenciamentoestacionamento.model.enums.TipoPlano;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PlanoEstacionamento {
    private Long id;
    private Estacionamento estacionamento;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private TipoPlano modalidade;
    private LocalDate vigenciaInicio;
    private LocalDate vigenciaFim;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private String diasDaSemana;
    private Boolean ativo;
}
