package parkwise.gerenciamentoestacionamento.dao;

import parkwise.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import parkwise.gerenciamentoestacionamento.model.enums.Role;
import parkwise.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DonoDao extends AbstractDao {

    public DonoDao(DataSource dataSource) {
        super(dataSource);
    }
    public DonoEstacionamento save(DonoEstacionamento dono) {
        try (Connection conn = getConnection()) {
            if (dono.getId() == null) {
                Long id = nextVal(conn, "sq_tb_usuario");
                dono.setId(id);
                if (dono.getTipoDeUsuario() == null) {
                    dono.setTipoDeUsuario(TipoDeUsuario.DONO);
                }
                if (dono.getRole() == null) {
                    dono.setRole(Role.DONO);
                }
                if (dono.getStatus() == null) {
                    dono.setStatus(true);
                }

                String sqlUser = """
                        INSERT INTO tb_usuario
                        (cd_usuario, nm_usuario, ds_email, ds_senha, nr_telefone, dt_nascimento, tp_usuario, st_ativo, ds_role, dt_criado_em)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                    ps.setLong(1, id);
                    ps.setString(2, dono.getNome());
                    ps.setString(3, dono.getEmail());
                    ps.setString(4, dono.getSenha());
                    ps.setString(5, dono.getTelefone());
                    ps.setDate(6, dono.getDataNascimento() != null ? new Date(dono.getDataNascimento().getTime()) : new Date(System.currentTimeMillis()));
                    ps.setString(7, dono.getTipoDeUsuario().name());
                    ps.setInt(8, Boolean.FALSE.equals(dono.getStatus()) ? 0 : 1);
                    ps.setString(9, dono.getRole().name());
                    ps.setTimestamp(10, dono.getCriadoEm() != null ? Timestamp.from(dono.getCriadoEm()) : new Timestamp(System.currentTimeMillis()));
                    ps.executeUpdate();
                }

                String sqlDono = "INSERT INTO tb_dono_estacionamento (cd_dono) VALUES (?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlDono)) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
            } else {
                String sqlUpdate = """
                        UPDATE tb_usuario
                        SET nm_usuario = ?, ds_email = ?, ds_senha = ?, nr_telefone = ?, dt_nascimento = ?,
                            tp_usuario = ?, st_ativo = ?, ds_role = ?
                        WHERE cd_usuario = ?
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setString(1, dono.getNome());
                    ps.setString(2, dono.getEmail());
                    ps.setString(3, dono.getSenha());
                    ps.setString(4, dono.getTelefone());
                    ps.setDate(5, dono.getDataNascimento() != null ? new Date(dono.getDataNascimento().getTime()) : new Date(System.currentTimeMillis()));
                    ps.setString(6, dono.getTipoDeUsuario() != null ? dono.getTipoDeUsuario().name() : TipoDeUsuario.DONO.name());
                    ps.setInt(7, Boolean.FALSE.equals(dono.getStatus()) ? 0 : 1);
                    ps.setString(8, dono.getRole() != null ? dono.getRole().name() : Role.DONO.name());
                    ps.setLong(9, dono.getId());
                    ps.executeUpdate();
                }
            }
            return dono;
        } catch (SQLException e) {
            throw translateException("Erro ao salvar dono de estacionamento", e);
        }
    }
    public Optional<DonoEstacionamento> findById(Long id) {
        if (id == null) return Optional.empty();
        String sql = """
                SELECT u.cd_usuario, u.nm_usuario, u.ds_email, u.ds_senha, u.nr_telefone,
                       u.dt_nascimento, u.tp_usuario, u.st_ativo, u.ds_role, u.dt_criado_em
                FROM tb_usuario u
                INNER JOIN tb_dono_estacionamento d ON d.cd_dono = u.cd_usuario
                WHERE u.cd_usuario = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapDono(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar dono por ID", e);
        }
    }
    public List<DonoEstacionamento> findByStatusTrue() {
        String sql = """
                SELECT u.cd_usuario, u.nm_usuario, u.ds_email, u.ds_senha, u.nr_telefone,
                       u.dt_nascimento, u.tp_usuario, u.st_ativo, u.ds_role, u.dt_criado_em
                FROM tb_usuario u
                INNER JOIN tb_dono_estacionamento d ON d.cd_dono = u.cd_usuario
                WHERE u.st_ativo = 1
                """;
        List<DonoEstacionamento> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapDono(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar donos ativos", e);
        }
    }
    public Optional<DonoEstacionamento> findByEmail(String email) {
        if (email == null) return Optional.empty();
        String sql = """
                SELECT u.cd_usuario, u.nm_usuario, u.ds_email, u.ds_senha, u.nr_telefone,
                       u.dt_nascimento, u.tp_usuario, u.st_ativo, u.ds_role, u.dt_criado_em
                FROM tb_usuario u
                INNER JOIN tb_dono_estacionamento d ON d.cd_dono = u.cd_usuario
                WHERE u.ds_email = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapDono(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar dono por email", e);
        }
    }
    public void deleteAll() {
        String[] deletes = {
                "DELETE FROM tb_acesso",
                "DELETE FROM tb_avaliacao",
                "DELETE FROM tb_reserva",
                "DELETE FROM tb_valor",
                "DELETE FROM tb_gerente",
                "DELETE FROM tb_estacionamento",
                "DELETE FROM tb_dono_estacionamento",
                "DELETE FROM tb_usuario WHERE tp_usuario = 'DONO'"
        };
        try (Connection conn = getConnection()) {
            for (String sql : deletes) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw translateException("Erro ao remover donos", e);
        }
    }

    private DonoEstacionamento mapDono(ResultSet rs) throws SQLException {
        DonoEstacionamento dono = new DonoEstacionamento();
        dono.setId(rs.getLong("cd_usuario"));
        dono.setNome(rs.getString("nm_usuario"));
        dono.setEmail(rs.getString("ds_email"));
        dono.setSenha(rs.getString("ds_senha"));
        dono.setTelefone(rs.getString("nr_telefone"));
        Date dtNasc = rs.getDate("dt_nascimento");
        if (dtNasc != null) {
            dono.setDataNascimento(new java.util.Date(dtNasc.getTime()));
        }
        String tipo = rs.getString("tp_usuario");
        if (tipo != null) dono.setTipoDeUsuario(TipoDeUsuario.valueOf(tipo));
        dono.setStatus(rs.getInt("st_ativo") == 1);
        String role = rs.getString("ds_role");
        if (role != null) dono.setRole(Role.valueOf(role));
        Timestamp criadoEm = rs.getTimestamp("dt_criado_em");
        if (criadoEm != null) dono.setCriadoEm(criadoEm.toInstant());
        return dono;
    }
}
