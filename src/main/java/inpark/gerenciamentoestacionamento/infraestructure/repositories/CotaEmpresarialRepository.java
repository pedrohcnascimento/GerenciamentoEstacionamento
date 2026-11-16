package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.CotaEmpresarial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CotaEmpresarialRepository extends JpaRepository<CotaEmpresarial, Long> {
    List<CotaEmpresarial> findByContratoId(Long contratoId);
}
