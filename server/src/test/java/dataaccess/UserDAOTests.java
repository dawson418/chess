package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {
    private final UserDataAccess userDAO = new UserSQLData();

    public UserDAOTests() throws DataAccessException{
    }

    @Test
    void createUserPositive() throws DataAccessException{
        userDAO.clear();
        UserData user = new UserData("cheese_lover67", "goudaDay", "mozzy@rell.a");
        assertDoesNotThrow(()-> userDAO.createUser(user));
    }

    @Test
    void createUserNegative() throws DataAccessException{
        userDAO.clear();
        UserData user = new UserData("cheese_lover67", "goudaDay", "mozzy@rell.a");
        userDAO.createUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }


    @Test
    void getUserPositive() throws DataAccessException {
        userDAO.clear();
        userDAO.createUser(new UserData("cheese_lover67", "goudaDay", "mozzy@rell.a"));
        var result = userDAO.getUser("cheese_lover67", "goudaDay");
        assertNotNull(result);
    }

    @Test
    void getUserNegative() throws DataAccessException {
        userDAO.clear();
        userDAO.createUser(new UserData("cheese_lover67", "goudaDay", "mozzy@rell.a"));
        assertThrows(UnauthorizedException.class, () -> userDAO.getUser("cheese_lover67", "wrongCrackers"));
    }

    @Test
    void clearUser() throws DataAccessException {
        userDAO.createUser(new UserData("cheese_lover67", "goudaDay", "mozzy@rell.a"));
        userDAO.clear();
        assertThrows(UnauthorizedException.class, () -> userDAO.getUser("cheese_lover67", "goudaDay"));
    }
}
