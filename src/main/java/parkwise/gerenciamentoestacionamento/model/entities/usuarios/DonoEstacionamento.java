package parkwise.gerenciamentoestacionamento.model.entities.usuarios;

import parkwise.gerenciamentoestacionamento.model.entities.Estacionamento;
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
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DonoEstacionamento extends Usuario {
    @Builder.Default
    private List<Estacionamento> estacionamentos = new ArrayList<>();
}
