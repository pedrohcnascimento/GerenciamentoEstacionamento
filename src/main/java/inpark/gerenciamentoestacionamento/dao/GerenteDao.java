package inpark.gerenciamentoestacionamento.dao;

import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Gerente;
import inpark.gerenciamentoestacionamento.model.enums.Role;
import inpark.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class GerenteDao extends AbstractDao {

    public GerenteDao(DataSource dataSource) {
        super(dataSource);
    }
    public Gerente save(Gerente gerente) {
        try (Connection conn = getConnection()) {
            if (gerente.getId() == null) {
                Long id = nextVal(conn, "sq_tb_usuario");
                gerente.setId(id);
                if (gerente.getTipoDeUsuario() == null) {
                    gerente.setTipoDeUsuario(TipoDeUsuario.GERENTE);
                }
                if (gerente.getRole() == null) {
                    gerente.setRole(Role.GERENTE);
                }
                if (gerente.getStatus() == null) {
                    gerente.setStatus(true);
                }

                String sqlUser = """
                        INSERT INTO tb_usuario
                        (cd_usuario, nm_usuario, ds_email, ds_senha, nr_telefone, dt_nascimento, tp_usuario, st_ativo, ds_role, dt_criado_em)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                    ps.setLong(1, id);
                    ps.setString(2, gerente.getNome());
                    ps.setString(3, gerente.getEmail());
                    ps.setString(4, gerente.getSenha());
                    ps.setString(5, gerente.getTelefone());
                    ps.setDate(6, gerente.getDataNascimento() != null ? new Date(gerente.getDataNascimento().getTime()) : new Date(System.currentTimeMillis()));
                    ps.setString(7, gerente.getTipoDeUsuario().name());
                    ps.setInt(8, Boolean.FALSE.equals(gerente.getStatus()) ? 0 : 1);
                    ps.setString(9, gerente.getRole().name());
                    ps.setTimestamp(10, gerente.getCriadoEm() != null ? Timestamp.from(gerente.getCriadoEm()) : new Timestamp(System.currentTimeMillis()));
                    ps.executeUpdate();
                }

                String sqlGerente = "INSERT INTO tb_gerente (cd_gerente, nr_cpf_cnpj, cd_estacionamento) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlGerente)) {
                    ps.setLong(1, id);
                    ps.setString(2, gerente.getCpfOuCnpj());
                    if (gerente.getEstacionamento() != null && gerente.getEstacionamento().getId() != null) {
                        ps.setLong(3, gerente.getEstacionamento().getId());
                    } else {
                        ps.setNull(3, Types.BIGINT);
                    }
                    ps.executeUpdate();
                }
            } else {
                String sqlUpdateUser = """
                        UPDATE tb_usuario
                        SET nm_usuario = ?, ds_email = ?, ds_senha = ?, nr_telefone = ?, dt_nascimento = ?,
                            tp_usuario = ?, st_ativo = ?, ds_role = ?
                        WHERE cd_usuario = ?
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateUser)) {
                    ps.setString(1, gerente.getNome());
                    ps.setString(2, gerente.getEmail());
                    ps.setString(3, gerente.getSenha());
                    ps.setString(4, gerente.getTelefone());
                    ps.setDate(5, gerente.getDataNascimento() != null ? new Date(gerente.getDataNascimento().getTime()) : new Date(System.currentTimeMillis()));
                    ps.setString(6, gerente.getTipoDeUsuario() != null ? gerente.getTipoDeUsuario().name() : TipoDeUsuario.GERENTE.name());
                    ps.setInt(7, Boolean.FALSE.equals(gerente.getStatus()) ? 0 : 1);
                    ps.setString(8, gerente.getRole() != null ? gerente.getRole().name() : Role.GERENTE.name());
                    ps.setLong(9, gerente.getId());
                    ps.executeUpdate();
                }

                String sqlUpdateGerente = "UPDATE tb_gerente SET nr_cpf_cnpj = ?, cd_estacionamento = ? WHERE cd_gerente = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateGerente)) {
                    ps.setString(1, gerente.getCpfOuCnpj());
                    if (gerente.getEstacionamento() != null && gerente.getEstacionamento().getId() != null) {
                        ps.setLong(2, gerente.getEstacionamento().getId());
                    } else {
                        ps.setNull(2, Types.BIGINT);
                    }
                    ps.setLong(3, gerente.getId());
                    ps.executeUpdate();
                }
            }
            return gerente;
        } catch (SQLException e) {
            throw translateException("Erro ao salvar gerente", e);
        }
    }
    public Optional<Gerente> findById(Long id) {
        if (id == null) return Optional.empty();
        String sql = """
                SELECT u.cd_usuario, u.nm_usuario, u.ds_email, u.ds_senha, u.nr_telefone,
                       u.dt_nascimento, u.tp_usuario, u.st_ativo, u.ds_role, u.dt_criado_em,
                       g.nr_cpf_cnpj, g.cd_estacionamento
                FROM tb_usuario u
                INNER JOIN tb_gerente g ON g.cd_gerente = u.cd_usuario
                WHERE u.cd_usuario = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapGerente(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar gerente por ID", e);
        }
    }
    public List<Gerente> findByStatusTrue() {
        String sql = """
                SELECT u.cd_usuario, u.nm_usuario, u.ds_email, u.ds_senha, u.nr_telefone,
                       u.dt_nascimento, u.tp_usuario, u.st_ativo, u.ds_role, u.dt_criado_em,
                       g.nr_cpf_cnpj, g.cd_estacionamento
                FROM tb_usuario u
                INNER JOIN tb_gerente g ON g.cd_gerente = u.cd_usuario
                WHERE u.st_ativo = 1
                """;
        List<Gerente> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapGerente(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar gerentes ativos", e);
        }
    }
    public Optional<Gerente> findByEmail(String email) {
        if (email == null) return Optional.empty();
        String sql = """
                SELECT u.cd_usuario, u.nm_usuario, u.ds_email, u.ds_senha, u.nr_telefone,
                       u.dt_nascimento, u.tp_usuario, u.st_ativo, u.ds_role, u.dt_criado_em,
                       g.nr_cpf_cnpj, g.cd_estacionamento
                FROM tb_usuario u
                INNER JOIN tb_gerente g ON g.cd_gerente = u.cd_usuario
                WHERE u.ds_email = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapGerente(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar gerente por email", e);
        }
    }
    public void deleteAll() {
        try (Connection conn = getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_gerente")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_usuario WHERE tp_usuario = 'GERENTE'")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao remover gerentes", e);
        }
    }

    private Gerente mapGerente(ResultSet rs) throws SQLException {
        Gerente gerente = new Gerente();
        gerente.setId(rs.getLong("cd_usuario"));
        gerente.setNome(rs.getString("nm_usuario"));
        gerente.setEmail(rs.getString("ds_email"));
        gerente.setSenha(rs.getString("ds_senha"));
        gerente.setTelefone(rs.getString("nr_telefone"));
        Date dtNasc = rs.getDate("dt_nascimento");
        if (dtNasc != null) {
            gerente.setDataNascimento(new java.util.Date(dtNasc.getTime()));
        }
        String tipo = rs.getString("tp_usuario");
        if (tipo != null) gerente.setTipoDeUsuario(TipoDeUsuario.valueOf(tipo));
        gerente.setStatus(rs.getInt("st_ativo") == 1);
        String role = rs.getString("ds_role");
        if (role != null) gerente.setRole(Role.valueOf(role));
        Timestamp criadoEm = rs.getTimestamp("dt_criado_em");
        if (criadoEm != null) gerente.setCriadoEm(criadoEm.toInstant());
        gerente.setCpfOuCnpj(rs.getString("nr_cpf_cnpj"));
        long estId = rs.getLong("cd_estacionamento");
        if (!rs.wasNull()) {
            Estacionamento est = new Estacionamento();
            est.setId(estId);
            gerente.setEstacionamento(est);
        }
        return gerente;
    }
}
