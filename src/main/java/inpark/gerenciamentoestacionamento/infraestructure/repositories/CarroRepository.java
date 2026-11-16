package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.Carro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarroRepository extends JpaRepository<Carro, Long> {
    List<Carro> findByClienteId(Long clienteId);
    List<Carro> findByStatusTrue();
    Optional<Carro> findByPlaca(String placa);
}
