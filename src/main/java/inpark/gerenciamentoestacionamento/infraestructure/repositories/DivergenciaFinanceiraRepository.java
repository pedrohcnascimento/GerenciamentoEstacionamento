package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.DivergenciaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DivergenciaFinanceiraRepository extends JpaRepository<DivergenciaFinanceira, Long> {
    List<DivergenciaFinanceira> findByRelatorioId(Long relatorioId);
    List<DivergenciaFinanceira> findByRelatorio_Estacionamento_Id(Long estacionamentoId);
}
