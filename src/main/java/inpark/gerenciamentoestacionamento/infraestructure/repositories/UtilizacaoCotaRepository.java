package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.UtilizacaoCota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UtilizacaoCotaRepository extends JpaRepository<UtilizacaoCota, Long> {
    List<UtilizacaoCota> findByCotaId(Long cotaId);
}
