package inpark.gerenciamentoestacionamento.dao;

import inpark.gerenciamentoestacionamento.model.entities.Carro;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CarroDao extends AbstractDao {

    public CarroDao(DataSource dataSource) {
        super(dataSource);
    }
    public Carro save(Carro carro) {
        try (Connection conn = getConnection()) {
            if (carro.getId() == null) {
                Long id = nextVal(conn, "sq_tb_carro");
                carro.setId(id);
                String sql = "INSERT INTO tb_carro (cd_carro, cd_cliente, ds_placa, nm_modelo, nm_cor, st_ativo) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, id);
                    ps.setLong(2, carro.getCliente().getId());
                    ps.setString(3, carro.getPlaca());
                    ps.setString(4, carro.getModelo());
                    ps.setString(5, carro.getCor());
                    ps.setInt(6, Boolean.FALSE.equals(carro.getStatus()) ? 0 : 1);
                    ps.executeUpdate();
                }
            } else {
                String sql = "UPDATE tb_carro SET cd_cliente = ?, ds_placa = ?, nm_modelo = ?, nm_cor = ?, st_ativo = ? WHERE cd_carro = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, carro.getCliente().getId());
                    ps.setString(2, carro.getPlaca());
                    ps.setString(3, carro.getModelo());
                    ps.setString(4, carro.getCor());
                    ps.setInt(5, Boolean.FALSE.equals(carro.getStatus()) ? 0 : 1);
                    ps.setLong(6, carro.getId());
                    ps.executeUpdate();
                }
            }
            return carro;
        } catch (SQLException e) {
            throw translateException("Erro ao salvar carro", e);
        }
    }
    public Optional<Carro> findById(Long id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM tb_carro WHERE cd_carro = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCarro(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar carro por ID", e);
        }
    }
    public List<Carro> findByClienteId(Long clienteId) {
        if (clienteId == null) return List.of();
        String sql = "SELECT * FROM tb_carro WHERE cd_cliente = ? AND st_ativo = 1";
        List<Carro> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapCarro(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar carros do cliente", e);
        }
    }
    public List<Carro> findByStatusTrue() {
        String sql = "SELECT * FROM tb_carro WHERE st_ativo = 1";
        List<Carro> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapCarro(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar carros ativos", e);
        }
    }
    public Optional<Carro> findByPlaca(String placa) {
        if (placa == null) return Optional.empty();
        String sql = "SELECT * FROM tb_carro WHERE ds_placa = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCarro(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar carro por placa", e);
        }
    }
    public void deleteAll() {
        try (Connection conn = getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_acesso WHERE cd_carro IS NOT NULL")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_carro")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao remover carros", e);
        }
    }

    private Carro mapCarro(ResultSet rs) throws SQLException {
        Carro carro = new Carro();
        carro.setId(rs.getLong("cd_carro"));
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("cd_cliente"));
        carro.setCliente(cliente);
        carro.setPlaca(rs.getString("ds_placa"));
        carro.setModelo(rs.getString("nm_modelo"));
        carro.setCor(rs.getString("nm_cor"));
        carro.setStatus(rs.getInt("st_ativo") == 1);
        return carro;
    }
}
