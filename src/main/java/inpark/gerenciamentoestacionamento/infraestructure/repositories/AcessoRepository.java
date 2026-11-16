package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.Acesso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcessoRepository extends JpaRepository<Acesso, Long> {
    List<Acesso> findByPlacaDoCarro(String placaDoCarro);
    List<Acesso> findByEstacionamentoId(Long estacionamentoId);
    List<Acesso> findByStatusTrue();
}
