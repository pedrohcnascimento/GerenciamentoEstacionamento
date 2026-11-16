package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.ContratoEmpresarial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratoEmpresarialRepository extends JpaRepository<ContratoEmpresarial, Long> {
    List<ContratoEmpresarial> findByEmpresaId(Long empresaId);
    List<ContratoEmpresarial> findByEstacionamentoId(Long estacionamentoId);
}
