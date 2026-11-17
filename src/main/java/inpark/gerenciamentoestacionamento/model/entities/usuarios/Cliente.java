package inpark.gerenciamentoestacionamento.model.entities.usuarios;

import inpark.gerenciamentoestacionamento.model.entities.Avaliacao;
import inpark.gerenciamentoestacionamento.model.entities.Carro;
import inpark.gerenciamentoestacionamento.model.entities.Reserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Cliente extends Usuario {
    @Builder.Default
    private List<Carro> Carros = new ArrayList<>();

    @Builder.Default
    private List<Reserva> reservas = new ArrayList<>();

    @Builder.Default
    private List<Avaliacao> avaliacoes = new ArrayList<>();
}
