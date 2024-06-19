package br.com.acpgroup.safira.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MySQLDatabase {
    private Connection connection;

    public MySQLDatabase(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public Long doesRecordExist(String processo, Long aviso) throws SQLException {
        String query = "select * from pje_aviso_pendentes pap  where  processo_numeracao_unica = ? and id_aviso =?;";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, processo);
            stmt.setLong(2, aviso);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                } else {
                    return null;
                }
            }
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}