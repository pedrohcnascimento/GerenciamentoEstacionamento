package inpark.gerenciamentoestacionamento.infraestructure.repositories;

import inpark.gerenciamentoestacionamento.model.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    List<Empresa> findByStatusTrue();
    Optional<Empresa> findByCnpj(String cnpj);
}
