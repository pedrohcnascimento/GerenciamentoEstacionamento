package inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario;

import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByStatusTrue();
    Optional<Cliente> findByEmail(String email);
}
