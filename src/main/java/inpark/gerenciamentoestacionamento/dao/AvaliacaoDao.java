package inpark.gerenciamentoestacionamento.dao;

import inpark.gerenciamentoestacionamento.model.entities.Avaliacao;
import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AvaliacaoDao extends AbstractDao {

    public AvaliacaoDao(DataSource dataSource) {
        super(dataSource);
    }
    public Avaliacao save(Avaliacao avaliacao) {
        try (Connection conn = getConnection()) {
            if (avaliacao.getId() == null) {
                Long id = nextVal(conn, "sq_tb_avaliacao");
                avaliacao.setId(id);
                String sql = """
                        INSERT INTO tb_avaliacao
                        (cd_avaliacao, cd_cliente, cd_estacionamento, nr_nota, ds_comentario, dt_avaliacao, st_ativo)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, id);
                    ps.setLong(2, avaliacao.getCliente().getId());
                    ps.setLong(3, avaliacao.getEstacionamento().getId());
                    ps.setShort(4, avaliacao.getNota());
                    ps.setString(5, avaliacao.getComentario());
                    ps.setTimestamp(6, avaliacao.getDataDeAvaliacao() != null ? Timestamp.valueOf(avaliacao.getDataDeAvaliacao()) : new Timestamp(System.currentTimeMillis()));
                    ps.setInt(7, Boolean.FALSE.equals(avaliacao.getStatus()) ? 0 : 1);
                    ps.executeUpdate();
                }
            } else {
                String sql = """
                        UPDATE tb_avaliacao
                        SET cd_cliente = ?, cd_estacionamento = ?, nr_nota = ?, ds_comentario = ?, dt_avaliacao = ?, st_ativo = ?
                        WHERE cd_avaliacao = ?
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, avaliacao.getCliente().getId());
                    ps.setLong(2, avaliacao.getEstacionamento().getId());
                    ps.setShort(3, avaliacao.getNota());
                    ps.setString(4, avaliacao.getComentario());
                    ps.setTimestamp(5, avaliacao.getDataDeAvaliacao() != null ? Timestamp.valueOf(avaliacao.getDataDeAvaliacao()) : new Timestamp(System.currentTimeMillis()));
                    ps.setInt(6, Boolean.FALSE.equals(avaliacao.getStatus()) ? 0 : 1);
                    ps.setLong(7, avaliacao.getId());
                    ps.executeUpdate();
                }
            }
            return avaliacao;
        } catch (SQLException e) {
            throw translateException("Erro ao salvar avaliacao", e);
        }
    }
    public Optional<Avaliacao> findById(Long id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM tb_avaliacao WHERE cd_avaliacao = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAvaliacao(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar avaliacao por ID", e);
        }
    }
    public List<Avaliacao> findByEstacionamentoId(Long estacionamentoId) {
        if (estacionamentoId == null) return List.of();
        String sql = "SELECT * FROM tb_avaliacao WHERE cd_estacionamento = ? AND st_ativo = 1";
        List<Avaliacao> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, estacionamentoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapAvaliacao(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar avaliacoes por estacionamento", e);
        }
    }
    public List<Avaliacao> findByClienteId(Long clienteId) {
        if (clienteId == null) return List.of();
        String sql = "SELECT * FROM tb_avaliacao WHERE cd_cliente = ? AND st_ativo = 1";
        List<Avaliacao> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapAvaliacao(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar avaliacoes por cliente", e);
        }
    }
    public List<Avaliacao> findByStatusTrue() {
        String sql = "SELECT * FROM tb_avaliacao WHERE st_ativo = 1";
        List<Avaliacao> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapAvaliacao(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar avaliacoes ativas", e);
        }
    }
    public void deleteAll() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_avaliacao")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw translateException("Erro ao remover avaliacoes", e);
        }
    }

    private Avaliacao mapAvaliacao(ResultSet rs) throws SQLException {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(rs.getLong("cd_avaliacao"));

        Long clienteId = rs.getLong("cd_cliente");
        Cliente cliente = new ClienteDao(dataSource).findById(clienteId).orElseGet(() -> {
            Cliente c = new Cliente();
            c.setId(clienteId);
            return c;
        });
        avaliacao.setCliente(cliente);

        Estacionamento est = new Estacionamento();
        est.setId(rs.getLong("cd_estacionamento"));
        avaliacao.setEstacionamento(est);

        avaliacao.setNota(rs.getShort("nr_nota"));
        avaliacao.setComentario(rs.getString("ds_comentario"));
        Timestamp ts = rs.getTimestamp("dt_avaliacao");
        if (ts != null) avaliacao.setDataDeAvaliacao(ts.toLocalDateTime());
        avaliacao.setStatus(rs.getInt("st_ativo") == 1);
        return avaliacao;
    }
}
