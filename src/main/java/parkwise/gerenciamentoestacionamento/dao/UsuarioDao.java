package parkwise.gerenciamentoestacionamento.dao;

import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Gerente;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Usuario;
import parkwise.gerenciamentoestacionamento.model.enums.Role;
import parkwise.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Repository
public class UsuarioDao extends AbstractDao {

    public UsuarioDao(DataSource dataSource) {
        super(dataSource);
    }
    public Optional<Usuario> findByEmail(String email) {
        String sql = """
                SELECT u.cd_usuario, u.nm_usuario, u.ds_email, u.ds_senha, u.nr_telefone,
                       u.dt_nascimento, u.tp_usuario, u.st_ativo, u.ds_role, u.dt_criado_em,
                       g.nr_cpf_cnpj, g.cd_estacionamento
                FROM tb_usuario u
                LEFT JOIN tb_gerente g ON g.cd_gerente = u.cd_usuario
                WHERE u.ds_email = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUsuario(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar usuario por email", e);
        }
    }
    public Usuario save(Usuario usuario) {
        if (usuario instanceof Cliente cliente) {
            return new ClienteDao(dataSource).save(cliente);
        }
        if (usuario instanceof DonoEstacionamento dono) {
            return new DonoDao(dataSource).save(dono);
        }
        if (usuario instanceof Gerente gerente) {
            return new GerenteDao(dataSource).save(gerente);
        }
        throw new IllegalArgumentException("Tipo de usuario nao suportado para persistencia");
    }
    public void deleteAll() {
        String[] deletes = {
                "DELETE FROM tb_acesso",
                "DELETE FROM tb_avaliacao",
                "DELETE FROM tb_reserva",
                "DELETE FROM tb_valor",
                "DELETE FROM tb_gerente",
                "DELETE FROM tb_carro",
                "DELETE FROM tb_estacionamento",
                "DELETE FROM tb_dono_estacionamento",
                "DELETE FROM tb_cliente",
                "DELETE FROM tb_usuario"
        };
        try (Connection conn = getConnection()) {
            for (String sql : deletes) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw translateException("Erro ao limpar usuarios", e);
        }
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        String tipoStr = rs.getString("tp_usuario");
        String roleStr = rs.getString("ds_role");
        TipoDeUsuario tipo = tipoStr != null ? TipoDeUsuario.valueOf(tipoStr) : null;
        Role role = roleStr != null ? Role.valueOf(roleStr) : null;

        Usuario usuario;
        if (tipo == TipoDeUsuario.DONO) {
            usuario = new DonoEstacionamento();
        } else if (tipo == TipoDeUsuario.GERENTE) {
            Gerente g = new Gerente();
            g.setCpfOuCnpj(rs.getString("nr_cpf_cnpj"));
            usuario = g;
        } else {
            usuario = new Cliente();
        }

        usuario.setId(rs.getLong("cd_usuario"));
        usuario.setNome(rs.getString("nm_usuario"));
        usuario.setEmail(rs.getString("ds_email"));
        usuario.setSenha(rs.getString("ds_senha"));
        usuario.setTelefone(rs.getString("nr_telefone"));
        Date dtNasc = rs.getDate("dt_nascimento");
        if (dtNasc != null) {
            usuario.setDataNascimento(new java.util.Date(dtNasc.getTime()));
        }
        usuario.setTipoDeUsuario(tipo);
        usuario.setStatus(rs.getInt("st_ativo") == 1);
        usuario.setRole(role);
        Timestamp criadoEm = rs.getTimestamp("dt_criado_em");
        if (criadoEm != null) {
            usuario.setCriadoEm(criadoEm.toInstant());
        }
        return usuario;
    }
}
