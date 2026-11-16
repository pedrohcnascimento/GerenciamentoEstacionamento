package inpark.gerenciamentoestacionamento.model.entities.usuarios;

import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
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
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name = "id")
public class DonoEstacionamento extends Usuario {
    @Builder.Default
    @OneToMany(mappedBy = "dono")
    private List<Estacionamento> estacionamentos = new ArrayList<>();
}
