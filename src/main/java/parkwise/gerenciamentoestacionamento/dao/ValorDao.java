package parkwise.gerenciamentoestacionamento.dao;

import parkwise.gerenciamentoestacionamento.model.entities.Estacionamento;
import parkwise.gerenciamentoestacionamento.model.entities.Valor;
import parkwise.gerenciamentoestacionamento.model.enums.Cobranca;
import parkwise.gerenciamentoestacionamento.model.enums.Metodo;
import parkwise.gerenciamentoestacionamento.model.enums.Periodo;
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
public class ValorDao extends AbstractDao {

    public ValorDao(DataSource dataSource) {
        super(dataSource);
    }
    public Valor save(Valor valor) {
        try (Connection conn = getConnection()) {
            if (valor.getId() == null) {
                Long id = nextVal(conn, "sq_tb_valor");
                valor.setId(id);
                String sql = """
                        INSERT INTO tb_valor
                        (cd_valor, cd_estacionamento, vl_preco, tp_cobranca, tp_pagamento, tp_periodo, st_ativo)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, id);
                    ps.setLong(2, valor.getEstacionamento().getId());
                    ps.setDouble(3, valor.getPreco());
                    ps.setString(4, valor.getTipoDeCobranca().name());
                    ps.setString(5, valor.getTipoDePagamento().name());
                    ps.setString(6, valor.getPeriodo().name());
                    ps.setInt(7, Boolean.FALSE.equals(valor.getStatus()) ? 0 : 1);
                    ps.executeUpdate();
                }
            } else {
                String sql = """
                        UPDATE tb_valor
                        SET cd_estacionamento = ?, vl_preco = ?, tp_cobranca = ?, tp_pagamento = ?, tp_periodo = ?, st_ativo = ?
                        WHERE cd_valor = ?
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, valor.getEstacionamento().getId());
                    ps.setDouble(2, valor.getPreco());
                    ps.setString(3, valor.getTipoDeCobranca().name());
                    ps.setString(4, valor.getTipoDePagamento().name());
                    ps.setString(5, valor.getPeriodo().name());
                    ps.setInt(6, Boolean.FALSE.equals(valor.getStatus()) ? 0 : 1);
                    ps.setLong(7, valor.getId());
                    ps.executeUpdate();
                }
            }
            return valor;
        } catch (SQLException e) {
            throw translateException("Erro ao salvar tarifa", e);
        }
    }
    public Optional<Valor> findById(Long id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM tb_valor WHERE cd_valor = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapValor(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar tarifa por ID", e);
        }
    }
    public List<Valor> findByEstacionamentoId(Long estacionamentoId) {
        if (estacionamentoId == null) return List.of();
        String sql = "SELECT * FROM tb_valor WHERE cd_estacionamento = ? AND st_ativo = 1";
        List<Valor> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, estacionamentoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapValor(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar tarifas por estacionamento", e);
        }
    }
    public List<Valor> findByStatusTrue() {
        String sql = "SELECT * FROM tb_valor WHERE st_ativo = 1";
        List<Valor> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapValor(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar tarifas ativas", e);
        }
    }
    public void deleteAll() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_valor")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw translateException("Erro ao remover tarifas", e);
        }
    }

    private Valor mapValor(ResultSet rs) throws SQLException {
        Valor valor = new Valor();
        valor.setId(rs.getLong("cd_valor"));
        Estacionamento est = new Estacionamento();
        est.setId(rs.getLong("cd_estacionamento"));
        valor.setEstacionamento(est);
        valor.setPreco(rs.getDouble("vl_preco"));
        valor.setTipoDeCobranca(Cobranca.valueOf(rs.getString("tp_cobranca")));
        valor.setTipoDePagamento(Metodo.valueOf(rs.getString("tp_pagamento")));
        valor.setPeriodo(Periodo.valueOf(rs.getString("tp_periodo")));
        valor.setStatus(rs.getInt("st_ativo") == 1);
        return valor;
    }
}
