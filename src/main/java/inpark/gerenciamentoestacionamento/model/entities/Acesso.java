package inpark.gerenciamentoestacionamento.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class Acesso {
    private Long id;
    private Estacionamento estacionamento;
    private Carro carro;
    private Reserva reserva;
    private String placaDoCarro;
    private LocalTime horaDeEntrada;
    private LocalTime horaDeSaida;
    private Integer totalHoras;
    private Double valorAPagar;
    private Boolean status;

    public void calcularHorasTotais() {
        if (horaDeEntrada != null && horaDeSaida != null) {
            Duration duracao = Duration.between(horaDeEntrada, horaDeSaida);
            if (duracao.isNegative()) {
                duracao = duracao.plusHours(24);
            }
            this.totalHoras = (int) duracao.toHours();
        }
    }
}
