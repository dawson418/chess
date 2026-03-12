package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLData {

    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i+1, params[i]);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {return rs.getInt(1);}
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    protected void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    protected final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`))
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
            `token` varchar(256),
            `username` varchar(256),
            PRIMARY KEY (`token`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS game (
              `gameid` int NOT NULL AUTO_INCREMENT,
              `whiteuser` varchar(256) DEFAULT NULL,
              `blackuser` varchar(256) DEFAULT NULL,
              `name` varchar(256) NOT NULL,
              `chessgame` TEXT NOT NULL,
              PRIMARY KEY (`gameid`)
            )
            """
    };
}
