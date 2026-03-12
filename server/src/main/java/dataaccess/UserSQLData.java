package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSQLData extends SQLData implements UserDataAccess {

    public UserSQLData() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(data.password(), BCrypt.gensalt());
        executeUpdate(statement, data.username(), hashedPassword, data.email());
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedHashedPW = rs.getString("password");
                        if(BCrypt.checkpw(password, storedHashedPW)){
                            return new UserData(username, password, rs.getString("email"));
                        } else {
                            throw new UnauthorizedException();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        throw new UnauthorizedException();
    }
}