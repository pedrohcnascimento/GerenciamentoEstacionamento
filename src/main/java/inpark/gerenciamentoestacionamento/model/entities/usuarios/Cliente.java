package inpark.gerenciamentoestacionamento.model.entities.usuarios;

import inpark.gerenciamentoestacionamento.model.entities.Avaliacao;
import inpark.gerenciamentoestacionamento.model.entities.Carro;
import inpark.gerenciamentoestacionamento.model.entities.Reserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
@SuperBuilder
public class Cliente extends Usuario {
    @Builder.Default
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Carro> Carros = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Reserva> reservas = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Avaliacao> avaliacoes = new ArrayList<>();
}
