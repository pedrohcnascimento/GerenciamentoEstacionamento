package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.PlanoEstacionamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanoEstacionamentoRepository extends JpaRepository<PlanoEstacionamento, Long> {
    List<PlanoEstacionamento> findByEstacionamentoIdAndAtivoTrue(Long estacionamentoId);
}
