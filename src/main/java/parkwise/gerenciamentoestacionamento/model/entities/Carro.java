package parkwise.gerenciamentoestacionamento.model.entities;

import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
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
public class Carro {
    private Long id;
    private Cliente cliente;

    @Builder.Default
    private List<Acesso> acessos = new ArrayList<>();

    private String placa;
    private String modelo;
    private String cor;
    private Boolean status;
}
