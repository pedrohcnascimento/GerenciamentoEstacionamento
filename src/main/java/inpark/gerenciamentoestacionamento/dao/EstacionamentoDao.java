package inpark.gerenciamentoestacionamento.dao;

import inpark.gerenciamentoestacionamento.model.entities.Estacionamento;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.DonoEstacionamento;
import inpark.gerenciamentoestacionamento.model.enums.Metodo;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EstacionamentoDao extends AbstractDao {

    public EstacionamentoDao(DataSource dataSource) {
        super(dataSource);
    }
    public Estacionamento save(Estacionamento est) {
        try (Connection conn = getConnection()) {
            if (est.getId() == null) {
                Long id = nextVal(conn, "sq_tb_estacionamento");
                est.setId(id);
                String sql = """
                        INSERT INTO tb_estacionamento
                        (cd_estacionamento, cd_dono, nm_estacionamento, ds_endereco, nr_cep, nr_endereco, ds_complemento,
                         ds_foto_url, nr_alvara_funcionamento, st_funcionamento, vl_latitude, vl_longitude,
                         nr_max_vagas, nr_vagas_disponiveis, nr_vagas_preferenciais, nr_margem_seguranca,
                         hr_abertura, hr_fechamento, tp_metodo_pagamento, nr_conta_dono, dt_atual,
                         nr_escritura_imovel, vl_arrecadado_dia, vl_nota_media, nr_quantidade_avaliacoes, st_ativo)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    bindParams(ps, est, true);
                    ps.executeUpdate();
                }
            } else {
                String sql = """
                        UPDATE tb_estacionamento
                        SET cd_dono = ?, nm_estacionamento = ?, ds_endereco = ?, nr_cep = ?, nr_endereco = ?, ds_complemento = ?,
                            ds_foto_url = ?, nr_alvara_funcionamento = ?, st_funcionamento = ?, vl_latitude = ?, vl_longitude = ?,
                            nr_max_vagas = ?, nr_vagas_disponiveis = ?, nr_vagas_preferenciais = ?, nr_margem_seguranca = ?,
                            hr_abertura = ?, hr_fechamento = ?, tp_metodo_pagamento = ?, nr_conta_dono = ?, dt_atual = ?,
                            nr_escritura_imovel = ?, vl_arrecadado_dia = ?, vl_nota_media = ?, nr_quantidade_avaliacoes = ?, st_ativo = ?
                        WHERE cd_estacionamento = ?
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    bindParams(ps, est, false);
                    ps.executeUpdate();
                }
            }
            return est;
        } catch (SQLException e) {
            throw translateException("Erro ao salvar estacionamento", e);
        }
    }

    private void bindParams(PreparedStatement ps, Estacionamento est, boolean isInsert) throws SQLException {
        int idx = 1;
        if (isInsert) {
            ps.setLong(idx++, est.getId());
        }
        ps.setLong(idx++, est.getDono().getId());
        ps.setString(idx++, est.getNome());
        ps.setString(idx++, est.getEndereco());
        ps.setString(idx++, est.getCEP());
        ps.setString(idx++, est.getNumero());
        ps.setString(idx++, est.getComplemento());
        ps.setString(idx++, est.getFotoUrl());
        ps.setString(idx++, est.getNumeroAlvaraDeFuncionamento());
        ps.setInt(idx++, Boolean.FALSE.equals(est.getFuncionamento()) ? 0 : 1);
        if (est.getLatitude() != null) ps.setDouble(idx++, est.getLatitude()); else ps.setNull(idx++, Types.DOUBLE);
        if (est.getLongitude() != null) ps.setDouble(idx++, est.getLongitude()); else ps.setNull(idx++, Types.DOUBLE);
        ps.setInt(idx++, est.getMaxVagas() != null ? est.getMaxVagas() : 0);
        if (est.getVagasDisponiveis() != null) ps.setInt(idx++, est.getVagasDisponiveis()); else ps.setNull(idx++, Types.INTEGER);
        if (est.getVagasPreferenciais() != null) ps.setInt(idx++, est.getVagasPreferenciais()); else ps.setNull(idx++, Types.INTEGER);
        ps.setInt(idx++, est.getMargemSeguranca() != null ? est.getMargemSeguranca() : 0);
        ps.setString(idx++, est.getHoraAbertura() != null ? est.getHoraAbertura().toString() : "08:00:00");
        ps.setString(idx++, est.getHoraFechamento() != null ? est.getHoraFechamento().toString() : "22:00:00");
        ps.setString(idx++, est.getMetodoDePagamento() != null ? est.getMetodoDePagamento().name() : Metodo.DINHEIRO.name());
        ps.setString(idx++, est.getNumeroContaDono());
        ps.setDate(idx++, est.getDiaAtual() != null ? Date.valueOf(est.getDiaAtual()) : null);
        ps.setString(idx++, est.getNumeroDeEscrituraImovel());
        ps.setDouble(idx++, est.getValorArrecadadoDoDia() != null ? est.getValorArrecadadoDoDia() : 0.0);
        ps.setDouble(idx++, est.getNotaMedia() != null ? est.getNotaMedia() : 0.0);
        ps.setInt(idx++, est.getQuantidadeDeAvaliacoes() != null ? est.getQuantidadeDeAvaliacoes() : 0);
        ps.setInt(idx++, Boolean.FALSE.equals(est.getStatus()) ? 0 : 1);
        if (!isInsert) {
            ps.setLong(idx, est.getId());
        }
    }
    public Optional<Estacionamento> findById(Long id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM tb_estacionamento WHERE cd_estacionamento = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEstacionamento(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw translateException("Erro ao buscar estacionamento por ID", e);
        }
    }
    public List<Estacionamento> findByStatusTrue() {
        String sql = "SELECT * FROM tb_estacionamento WHERE st_ativo = 1";
        List<Estacionamento> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapEstacionamento(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar estacionamentos ativos", e);
        }
    }
    public List<Estacionamento> findByDono_Id(Long donoId) {
        if (donoId == null) return List.of();
        String sql = "SELECT * FROM tb_estacionamento WHERE cd_dono = ? AND st_ativo = 1";
        List<Estacionamento> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, donoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapEstacionamento(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw translateException("Erro ao listar estacionamentos por dono", e);
        }
    }
    public void deleteAll() {
        String[] deletes = {
                "DELETE FROM tb_acesso",
                "DELETE FROM tb_avaliacao",
                "DELETE FROM tb_reserva",
                "DELETE FROM tb_valor",
                "DELETE FROM tb_gerente",
                "DELETE FROM tb_estacionamento"
        };
        try (Connection conn = getConnection()) {
            for (String sql : deletes) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw translateException("Erro ao remover estacionamentos", e);
        }
    }

    private Estacionamento mapEstacionamento(ResultSet rs) throws SQLException {
        Estacionamento est = new Estacionamento();
        est.setId(rs.getLong("cd_estacionamento"));
        DonoEstacionamento dono = new DonoEstacionamento();
        dono.setId(rs.getLong("cd_dono"));
        est.setDono(dono);
        est.setNome(rs.getString("nm_estacionamento"));
        est.setEndereco(rs.getString("ds_endereco"));
        est.setCEP(rs.getString("nr_cep"));
        est.setNumero(rs.getString("nr_endereco"));
        est.setComplemento(rs.getString("ds_complemento"));
        est.setFotoUrl(rs.getString("ds_foto_url"));
        est.setNumeroAlvaraDeFuncionamento(rs.getString("nr_alvara_funcionamento"));
        est.setFuncionamento(rs.getInt("st_funcionamento") == 1);
        double lat = rs.getDouble("vl_latitude");
        if (!rs.wasNull()) est.setLatitude(lat);
        double lon = rs.getDouble("vl_longitude");
        if (!rs.wasNull()) est.setLongitude(lon);
        est.setMaxVagas(rs.getInt("nr_max_vagas"));
        int vagasDisp = rs.getInt("nr_vagas_disponiveis");
        if (!rs.wasNull()) est.setVagasDisponiveis(vagasDisp);
        int vagasPref = rs.getInt("nr_vagas_preferenciais");
        if (!rs.wasNull()) est.setVagasPreferenciais(vagasPref);
        est.setMargemSeguranca(rs.getInt("nr_margem_seguranca"));
        String abertura = rs.getString("hr_abertura");
        if (abertura != null) est.setHoraAbertura(LocalTime.parse(abertura));
        String fechamento = rs.getString("hr_fechamento");
        if (fechamento != null) est.setHoraFechamento(LocalTime.parse(fechamento));
        String metodo = rs.getString("tp_metodo_pagamento");
        if (metodo != null) est.setMetodoDePagamento(Metodo.valueOf(metodo));
        est.setNumeroContaDono(rs.getString("nr_conta_dono"));
        Date dtAtual = rs.getDate("dt_atual");
        if (dtAtual != null) est.setDiaAtual(dtAtual.toLocalDate());
        est.setNumeroDeEscrituraImovel(rs.getString("nr_escritura_imovel"));
        est.setValorArrecadadoDoDia(rs.getDouble("vl_arrecadado_dia"));
        est.setNotaMedia(rs.getDouble("vl_nota_media"));
        est.setQuantidadeDeAvaliacoes(rs.getInt("nr_quantidade_avaliacoes"));
        est.setStatus(rs.getInt("st_ativo") == 1);
        return est;
    }
}
