package inpark.gerenciamentoestacionamento.dao;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractDao {

    protected final DataSource dataSource;

    protected AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    protected Long nextVal(Connection conn, String sequenceName) throws SQLException {
        String sql = "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new SQLException("Falha ao obter proximo valor da sequence " + sequenceName);
        }
    }

    protected RuntimeException translateException(String message, SQLException e) {
        String sqlState = e.getSQLState();
        int errorCode = e.getErrorCode();
        // Oracle ORA-00001 (1) / ORA-02292 (2292) / H2 23505 or 23503 (SQLState 23xxx)
        if ((sqlState != null && sqlState.startsWith("23")) || errorCode == 1 || errorCode == 2291 || errorCode == 2292) {
            return new DataIntegrityViolationException(message, e);
        }
        return new DataAccessResourceFailureException(message, e);
    }
}
