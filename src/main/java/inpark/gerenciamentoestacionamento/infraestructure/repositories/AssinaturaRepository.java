package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.Assinatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {
    List<Assinatura> findByClienteId(Long clienteId);
}
