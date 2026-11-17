package parkwise.gerenciamentoestacionamento.dao;

import parkwise.gerenciamentoestacionamento.model.entities.Avaliacao;
import parkwise.gerenciamentoestacionamento.model.entities.Carro;
import parkwise.gerenciamentoestacionamento.model.entities.Estacionamento;
import parkwise.gerenciamentoestacionamento.model.entities.Reserva;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import parkwise.gerenciamentoestacionamento.model.enums.Role;
import parkwise.gerenciamentoestacionamento.model.enums.StatusReserva;
import parkwise.gerenciamentoestacionamento.model.enums.TipoDeUsuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteDao extends AbstractDao {

    public ClienteDao(DataSource dataSource) {
        super(dataSource);
    }
    public Cliente save(Cliente cliente) {
        try (Connection conn = getConnection()) {
            if (cliente.getId() == null) {
                Long id = nextVal(conn, "sq_tb_usuario");
                cliente.setId(id);
                if (cliente.getTipoDeUsuario() == null) {
                    cliente.setTipoDeUsuario(TipoDeUsuario.CLIENTE);
                }
                if (cliente.getRole() == null) {
                    cliente.setRole(Role.CLIENTE);
                }
                if (cliente.getStatus() == null) {
                    cliente.setStatus(true);
                }

                String sqlUser = """
                        INSERT INTO tb_usuario
                        (cd_usuario, nm_usuario, ds_email, ds_senha, nr_telefone, dt_nascimento, tp_usuario, st_ativo, ds_role, dt_criado_em)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                    ps.setLong(1, id);
                    ps.setString(2, cliente.getNome());
                    ps.setString(3, cliente.getEmail());
                    ps.setString(4, cliente.getSenha());
                    ps.setString(5, cliente.getTelefone());
                    ps.setDate(6, cliente.getDataNascimento() != null ? new Date(cliente.getDataNascimento().getTime()) : new Date(System.currentTimeMillis()));
                    ps.setString(7, cliente.getTipoDeUsuario().name());
                    ps.setInt(8, Boolean.FALSE.equals(cliente.getStatus()) ? 0 : 1);
                    ps.setString(9, cliente.getRole().name());
                    ps.setTimestamp(10, cliente.getCriadoEm() != null ? Timestamp.from(cliente.getCriadoEm()) : new Timestamp(System.currentTimeMillis()));
                    ps.executeUpdate();
                }

                String sqlCliente = "INSERT INTO tb_cliente (cd_cliente) VALUES (?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlCliente)) {
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
                    ps.setString(1, cliente.getNome());
                    ps.setString(2, cliente.getEmail());
                    ps.setString(3, cliente.getSenha());
                    ps.setString(4, cliente.getTelefone());
                    ps.setDate(5, cliente.getDataNascimento() != null ? new Date(cliente.getDataNascimento().getTime()) : new Date(System.currentTimeMillis()));
                    ps.setString(6, cliente.getTipoDeUsuario() != null ? cliente.getTipoDeUsuario().name() : TipoDeUsuario.CLIENTE.name());
                    ps.setInt(7, Boolean.FALSE.equals(cliente.getStatus()) ? 0 : 1);
                    ps.setString(8, cliente.getRole() != null ? cliente.getRole().name() : Role.CLIENTE.name());
                    ps.setLong(9, cliente.getId());
                    ps.executeUpdate();
                }
            }
            return cliente;
        } catch (SQLException e) {
            throw translateException("Erro ao salvar cliente", e);
        }
    }
    public Optional<Cliente> findById(Long id) {
        if (id == null) return Optional.empty();
        String sql = """
                SELECT u.cd_usuario, u.nm_usuario, u.ds_email, u.ds_senha, u.nr_telefone,
                       u.dt_nascimento, u.tp_usuario, u.st_ativo, u.ds_role, u.dt_criado_em
                FROM tb_usuario u
                INNER JOIN tb_cliente c ON c.cd_cliente = u.cd_usuario
                WHERE u.cd_usuario = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = mapCliente(rs);
                    carregarRelacionamentos(conn, cliente);
                    return Optional.of(cliente);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar cliente por ID", e);
        }
    }
    public List<Cliente> findByStatusTrue() {
        String sql = """
                SELECT u.cd_usuario, u.nm_usuario, u.ds_email, u.ds_senha, u.nr_telefone,
                       u.dt_nascimento, u.tp_usuario, u.st_ativo, u.ds_role, u.dt_criado_em
                FROM tb_usuario u
                INNER JOIN tb_cliente c ON c.cd_cliente = u.cd_usuario
                WHERE u.st_ativo = 1
                """;
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapCliente(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar clientes ativos", e);
        }
    }
    public Optional<Cliente> findByEmail(String email) {
        if (email == null) return Optional.empty();
        String sql = """
                SELECT u.cd_usuario, u.nm_usuario, u.ds_email, u.ds_senha, u.nr_telefone,
                       u.dt_nascimento, u.tp_usuario, u.st_ativo, u.ds_role, u.dt_criado_em
                FROM tb_usuario u
                INNER JOIN tb_cliente c ON c.cd_cliente = u.cd_usuario
                WHERE u.ds_email = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCliente(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar cliente por email", e);
        }
    }
    public void deleteAll() {
        String[] deletes = {
                "DELETE FROM tb_acesso WHERE cd_carro IN (SELECT cd_carro FROM tb_carro)",
                "DELETE FROM tb_avaliacao",
                "DELETE FROM tb_reserva",
                "DELETE FROM tb_carro",
                "DELETE FROM tb_cliente",
                "DELETE FROM tb_usuario WHERE cd_usuario NOT IN (SELECT cd_cliente FROM tb_cliente) AND cd_usuario NOT IN (SELECT cd_dono FROM tb_dono_estacionamento) AND cd_usuario NOT IN (SELECT cd_gerente FROM tb_gerente)"
        };
        try (Connection conn = getConnection()) {
            for (String sql : deletes) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw translateException("Erro ao remover clientes", e);
        }
    }

    private void carregarRelacionamentos(Connection conn, Cliente cliente) throws SQLException {
        String sqlCarros = "SELECT cd_carro, ds_placa, nm_modelo, nm_cor, st_ativo FROM tb_carro WHERE cd_cliente = ? AND st_ativo = 1";
        try (PreparedStatement ps = conn.prepareStatement(sqlCarros)) {
            ps.setLong(1, cliente.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<Carro> carros = new ArrayList<>();
                while (rs.next()) {
                    Carro carro = new Carro();
                    carro.setId(rs.getLong("cd_carro"));
                    carro.setPlaca(rs.getString("ds_placa"));
                    carro.setModelo(rs.getString("nm_modelo"));
                    carro.setCor(rs.getString("nm_cor"));
                    carro.setStatus(rs.getInt("st_ativo") == 1);
                    carro.setCliente(cliente);
                    carros.add(carro);
                }
                cliente.setCarros(carros);
            }
        }

        String sqlReservas = "SELECT cd_reserva, cd_estacionamento, dt_reserva, hr_reserva, st_reserva, st_ativo FROM tb_reserva WHERE cd_cliente = ? AND st_ativo = 1";
        try (PreparedStatement ps = conn.prepareStatement(sqlReservas)) {
            ps.setLong(1, cliente.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<Reserva> reservas = new ArrayList<>();
                while (rs.next()) {
                    Reserva r = new Reserva();
                    r.setId(rs.getLong("cd_reserva"));
                    Estacionamento e = new Estacionamento();
                    e.setId(rs.getLong("cd_estacionamento"));
                    r.setEstacionamento(e);
                    r.setCliente(cliente);
                    Date dt = rs.getDate("dt_reserva");
                    if (dt != null) r.setDataDaReserva(new java.util.Date(dt.getTime()));
                    String hr = rs.getString("hr_reserva");
                    if (hr != null) r.setHoraDaReserva(Time.valueOf(hr));
                    String stRes = rs.getString("st_reserva");
                    if (stRes != null) r.setStatusReserva(StatusReserva.valueOf(stRes));
                    r.setStatus(rs.getInt("st_ativo") == 1);
                    reservas.add(r);
                }
                cliente.setReservas(reservas);
            }
        }

        String sqlAvaliacoes = "SELECT cd_avaliacao, cd_estacionamento, nr_nota, ds_comentario, dt_avaliacao, st_ativo FROM tb_avaliacao WHERE cd_cliente = ? AND st_ativo = 1";
        try (PreparedStatement ps = conn.prepareStatement(sqlAvaliacoes)) {
            ps.setLong(1, cliente.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<Avaliacao> avaliacoes = new ArrayList<>();
                while (rs.next()) {
                    Avaliacao a = new Avaliacao();
                    a.setId(rs.getLong("cd_avaliacao"));
                    Estacionamento e = new Estacionamento();
                    e.setId(rs.getLong("cd_estacionamento"));
                    a.setEstacionamento(e);
                    a.setCliente(cliente);
                    a.setNota(rs.getShort("nr_nota"));
                    a.setComentario(rs.getString("ds_comentario"));
                    Timestamp ts = rs.getTimestamp("dt_avaliacao");
                    if (ts != null) a.setDataDeAvaliacao(ts.toLocalDateTime());
                    a.setStatus(rs.getInt("st_ativo") == 1);
                    avaliacoes.add(a);
                }
                cliente.setAvaliacoes(avaliacoes);
            }
        }
    }

    private Cliente mapCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("cd_usuario"));
        cliente.setNome(rs.getString("nm_usuario"));
        cliente.setEmail(rs.getString("ds_email"));
        cliente.setSenha(rs.getString("ds_senha"));
        cliente.setTelefone(rs.getString("nr_telefone"));
        Date dtNasc = rs.getDate("dt_nascimento");
        if (dtNasc != null) {
            cliente.setDataNascimento(new java.util.Date(dtNasc.getTime()));
        }
        String tipo = rs.getString("tp_usuario");
        if (tipo != null) cliente.setTipoDeUsuario(TipoDeUsuario.valueOf(tipo));
        cliente.setStatus(rs.getInt("st_ativo") == 1);
        String role = rs.getString("ds_role");
        if (role != null) cliente.setRole(Role.valueOf(role));
        Timestamp criadoEm = rs.getTimestamp("dt_criado_em");
        if (criadoEm != null) cliente.setCriadoEm(criadoEm.toInstant());
        return cliente;
    }
}
