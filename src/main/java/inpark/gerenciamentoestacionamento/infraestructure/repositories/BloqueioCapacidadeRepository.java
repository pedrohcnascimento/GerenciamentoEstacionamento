package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.BloqueioCapacidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloqueioCapacidadeRepository extends JpaRepository<BloqueioCapacidade, Long> {
    List<BloqueioCapacidade> findByEstacionamentoId(Long estacionamentoId);
}
