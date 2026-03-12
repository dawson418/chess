package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthSQLData extends SQLData implements AuthDataAccess{

    public AuthSQLData() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        var statement = "INSERT INTO auth (token, username) VALUES (?, ?)";
        executeUpdate(statement, token, username);
        return new AuthData(token, username);
    }

    @Override
    public void isAuthorized(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT token, username FROM auth WHERE token=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try(ResultSet rs = ps.executeQuery()){
                    if (!rs.next()){
                        throw new UnauthorizedException();
                    }
                }
            }
        } catch (SQLException e) {
            throw new UnauthorizedException();
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if(getUsername(authToken) == null){
            throw new UnauthorizedException();
        }
        var statement = "DELETE FROM auth WHERE token=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT username FROM auth WHERE token=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        return rs.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            throw new UnauthorizedException();
        }
        return null;
    }
}
