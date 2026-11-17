package inpark.gerenciamentoestacionamento.dao;

import inpark.gerenciamentoestacionamento.model.entities.Acesso;
import inpark.gerenciamentoestacionamento.model.entities.Carro;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AcessoDao extends AbstractDao {

    public AcessoDao(DataSource dataSource) {
        super(dataSource);
    }
    public Acesso save(Acesso acesso) {
        try (Connection conn = getConnection()) {
            if (acesso.getId() == null) {
                Long id = nextVal(conn, "sq_tb_acesso");
                acesso.setId(id);
                String sql = """
                        INSERT INTO tb_acesso
                        (cd_acesso, cd_estacionamento, cd_carro, ds_placa_carro, hr_entrada, hr_saida, nr_total_horas, vl_pagar, st_ativo)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, id);
                    ps.setLong(2, acesso.getEstacionamento().getId());
                    if (acesso.getCarro() != null && acesso.getCarro().getId() != null) {
                        ps.setLong(3, acesso.getCarro().getId());
                    } else {
                        ps.setNull(3, Types.BIGINT);
                    }
                    ps.setString(4, acesso.getPlacaDoCarro());
                    ps.setString(5, acesso.getHoraDeEntrada() != null ? acesso.getHoraDeEntrada().toString() : null);
                    ps.setString(6, acesso.getHoraDeSaida() != null ? acesso.getHoraDeSaida().toString() : null);
                    if (acesso.getTotalHoras() != null) ps.setInt(7, acesso.getTotalHoras()); else ps.setNull(7, Types.INTEGER);
                    if (acesso.getValorAPagar() != null) ps.setDouble(8, acesso.getValorAPagar()); else ps.setNull(8, Types.DOUBLE);
                    ps.setInt(9, Boolean.FALSE.equals(acesso.getStatus()) ? 0 : 1);
                    ps.executeUpdate();
                }
            } else {
                String sql = """
                        UPDATE tb_acesso
                        SET cd_estacionamento = ?, cd_carro = ?, ds_placa_carro = ?, hr_entrada = ?, hr_saida = ?,
                            nr_total_horas = ?, vl_pagar = ?, st_ativo = ?
                        WHERE cd_acesso = ?
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, acesso.getEstacionamento().getId());
                    if (acesso.getCarro() != null && acesso.getCarro().getId() != null) {
                        ps.setLong(2, acesso.getCarro().getId());
                    } else {
                        ps.setNull(2, Types.BIGINT);
                    }
                    ps.setString(3, acesso.getPlacaDoCarro());
                    ps.setString(4, acesso.getHoraDeEntrada() != null ? acesso.getHoraDeEntrada().toString() : null);
                    ps.setString(5, acesso.getHoraDeSaida() != null ? acesso.getHoraDeSaida().toString() : null);
                    if (acesso.getTotalHoras() != null) ps.setInt(6, acesso.getTotalHoras()); else ps.setNull(6, Types.INTEGER);
                    if (acesso.getValorAPagar() != null) ps.setDouble(7, acesso.getValorAPagar()); else ps.setNull(7, Types.DOUBLE);
                    ps.setInt(8, Boolean.FALSE.equals(acesso.getStatus()) ? 0 : 1);
                    ps.setLong(9, acesso.getId());
                    ps.executeUpdate();
                }
            }
            return acesso;
        } catch (SQLException e) {
            throw translateException("Erro ao salvar acesso", e);
        }
    }
    public Optional<Acesso> findById(Long id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM tb_acesso WHERE cd_acesso = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAcesso(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar acesso por ID", e);
        }
    }
    public List<Acesso> findByPlacaDoCarro(String placaDoCarro) {
        if (placaDoCarro == null) return List.of();
        String sql = "SELECT * FROM tb_acesso WHERE ds_placa_carro = ? AND st_ativo = 1";
        List<Acesso> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, placaDoCarro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapAcesso(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar acessos por placa", e);
        }
    }
    public List<Acesso> findByEstacionamentoId(Long estacionamentoId) {
        if (estacionamentoId == null) return List.of();
        String sql = "SELECT * FROM tb_acesso WHERE cd_estacionamento = ? AND st_ativo = 1";
        List<Acesso> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, estacionamentoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapAcesso(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar acessos por estacionamento", e);
        }
    }
    public List<Acesso> findByStatusTrue() {
        String sql = "SELECT * FROM tb_acesso WHERE st_ativo = 1";
        List<Acesso> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapAcesso(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar acessos ativos", e);
        }
    }
    public void deleteAll() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_acesso")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw translateException("Erro ao remover acessos", e);
        }
    }

    private Acesso mapAcesso(ResultSet rs) throws SQLException {
        Acesso acesso = new Acesso();
        acesso.setId(rs.getLong("cd_acesso"));
        Estacionamento est = new Estacionamento();
        est.setId(rs.getLong("cd_estacionamento"));
        acesso.setEstacionamento(est);
        long carroId = rs.getLong("cd_carro");
        if (!rs.wasNull()) {
            Carro carro = new Carro();
            carro.setId(carroId);
            carro.setPlaca(rs.getString("ds_placa_carro"));
            acesso.setCarro(carro);
        }
        acesso.setPlacaDoCarro(rs.getString("ds_placa_carro"));
        String entrada = rs.getString("hr_entrada");
        if (entrada != null) acesso.setHoraDeEntrada(LocalTime.parse(entrada));
        String saida = rs.getString("hr_saida");
        if (saida != null) acesso.setHoraDeSaida(LocalTime.parse(saida));
        int totalHoras = rs.getInt("nr_total_horas");
        if (!rs.wasNull()) acesso.setTotalHoras(totalHoras);
        double valor = rs.getDouble("vl_pagar");
        if (!rs.wasNull()) acesso.setValorAPagar(valor);
        acesso.setStatus(rs.getInt("st_ativo") == 1);
        return acesso;
    }
}
