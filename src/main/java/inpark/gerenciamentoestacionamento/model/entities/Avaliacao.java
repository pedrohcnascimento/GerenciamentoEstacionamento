package inpark.gerenciamentoestacionamento.model.entities;

import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class Avaliacao {
    private Long id;
    private Cliente cliente;
    private Estacionamento estacionamento;
    private Short nota;
    private String comentario;
    private LocalDateTime dataDeAvaliacao;
    private Boolean status;
}
