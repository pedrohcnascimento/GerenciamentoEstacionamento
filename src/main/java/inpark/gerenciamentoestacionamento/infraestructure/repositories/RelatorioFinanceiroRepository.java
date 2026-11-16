package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.RelatorioFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelatorioFinanceiroRepository extends JpaRepository<RelatorioFinanceiro, Long> {
    List<RelatorioFinanceiro> findByEstacionamentoId(Long estacionamentoId);
}
