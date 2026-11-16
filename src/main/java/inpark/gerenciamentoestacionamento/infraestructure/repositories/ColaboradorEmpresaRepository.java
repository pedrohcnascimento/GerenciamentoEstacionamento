package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.ColaboradorEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColaboradorEmpresaRepository extends JpaRepository<ColaboradorEmpresa, Long> {
    List<ColaboradorEmpresa> findByEmpresaId(Long empresaId);
}
