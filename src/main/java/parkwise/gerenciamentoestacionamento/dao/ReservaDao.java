package parkwise.gerenciamentoestacionamento.dao;

import parkwise.gerenciamentoestacionamento.model.entities.Estacionamento;
import parkwise.gerenciamentoestacionamento.model.entities.Reserva;
import parkwise.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import parkwise.gerenciamentoestacionamento.model.enums.StatusReserva;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservaDao extends AbstractDao {

    public ReservaDao(DataSource dataSource) {
        super(dataSource);
    }
    public Reserva save(Reserva reserva) {
        try (Connection conn = getConnection()) {
            if (reserva.getId() == null) {
                Long id = nextVal(conn, "sq_tb_reserva");
                reserva.setId(id);
                String sql = """
                        INSERT INTO tb_reserva
                        (cd_reserva, cd_cliente, cd_estacionamento, dt_reserva, hr_reserva, st_reserva, st_ativo)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, id);
                    ps.setLong(2, reserva.getCliente().getId());
                    ps.setLong(3, reserva.getEstacionamento().getId());
                    ps.setDate(4, reserva.getDataDaReserva() != null ? new Date(reserva.getDataDaReserva().getTime()) : null);
                    ps.setString(5, reserva.getHoraDaReserva() != null ? reserva.getHoraDaReserva().toString() : null);
                    ps.setString(6, reserva.getStatusReserva() != null ? reserva.getStatusReserva().name() : StatusReserva.PENDENTE.name());
                    ps.setInt(7, Boolean.FALSE.equals(reserva.getStatus()) ? 0 : 1);
                    ps.executeUpdate();
                }
            } else {
                String sql = """
                        UPDATE tb_reserva
                        SET cd_cliente = ?, cd_estacionamento = ?, dt_reserva = ?, hr_reserva = ?, st_reserva = ?, st_ativo = ?
                        WHERE cd_reserva = ?
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, reserva.getCliente().getId());
                    ps.setLong(2, reserva.getEstacionamento().getId());
                    ps.setDate(3, reserva.getDataDaReserva() != null ? new Date(reserva.getDataDaReserva().getTime()) : null);
                    ps.setString(4, reserva.getHoraDaReserva() != null ? reserva.getHoraDaReserva().toString() : null);
                    ps.setString(5, reserva.getStatusReserva() != null ? reserva.getStatusReserva().name() : StatusReserva.PENDENTE.name());
                    ps.setInt(6, Boolean.FALSE.equals(reserva.getStatus()) ? 0 : 1);
                    ps.setLong(7, reserva.getId());
                    ps.executeUpdate();
                }
            }
            return reserva;
        } catch (SQLException e) {
            throw translateException("Erro ao salvar reserva", e);
        }
    }
    public Optional<Reserva> findById(Long id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM tb_reserva WHERE cd_reserva = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapReserva(conn, rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar reserva por ID", e);
        }
    }
    public List<Reserva> findByClienteId(Long clienteId) {
        if (clienteId == null) return List.of();
        String sql = "SELECT * FROM tb_reserva WHERE cd_cliente = ? AND st_ativo = 1";
        List<Reserva> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapReserva(conn, rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar reservas por cliente", e);
        }
    }
    public List<Reserva> findByEstacionamentoId(Long estacionamentoId) {
        if (estacionamentoId == null) return List.of();
        String sql = "SELECT * FROM tb_reserva WHERE cd_estacionamento = ? AND st_ativo = 1";
        List<Reserva> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, estacionamentoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapReserva(conn, rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar reservas por estacionamento", e);
        }
    }
    public List<Reserva> findByStatusTrue() {
        String sql = "SELECT * FROM tb_reserva WHERE st_ativo = 1";
        List<Reserva> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapReserva(conn, rs));
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar reservas ativas", e);
        }
    }
    public void deleteAll() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_reserva")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw translateException("Erro ao remover reservas", e);
        }
    }

    private Reserva mapReserva(Connection conn, ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setId(rs.getLong("cd_reserva"));

        Long clienteId = rs.getLong("cd_cliente");
        Cliente cliente = new ClienteDao(dataSource).findById(clienteId).orElseGet(() -> {
            Cliente c = new Cliente();
            c.setId(clienteId);
            return c;
        });
        reserva.setCliente(cliente);

        Long estId = rs.getLong("cd_estacionamento");
        Estacionamento est = new EstacionamentoDao(dataSource).findById(estId).orElseGet(() -> {
            Estacionamento e = new Estacionamento();
            e.setId(estId);
            return e;
        });
        reserva.setEstacionamento(est);

        Date dt = rs.getDate("dt_reserva");
        if (dt != null) reserva.setDataDaReserva(new java.util.Date(dt.getTime()));
        String hr = rs.getString("hr_reserva");
        if (hr != null) reserva.setHoraDaReserva(Time.valueOf(hr));
        String stRes = rs.getString("st_reserva");
        if (stRes != null) reserva.setStatusReserva(StatusReserva.valueOf(stRes));
        reserva.setStatus(rs.getInt("st_ativo") == 1);
        return reserva;
    }
}
