package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByEstacionamentoId(Long estacionamentoId);
    List<Avaliacao> findByClienteId(Long clienteId);
    List<Avaliacao> findByStatusTrue();
}
