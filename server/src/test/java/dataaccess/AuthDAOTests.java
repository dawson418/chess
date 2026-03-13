package dataaccess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {

    private final AuthDataAccess authDAO = new AuthSQLData();

    public AuthDAOTests() throws DataAccessException {
    }

    @Test
void createAuthPositive() throws DataAccessException {
    authDAO.clear();
    assertDoesNotThrow(() -> authDAO.createAuth("cheese_lover67"));
}

@Test
void getUsernamePositive() throws DataAccessException {
    authDAO.clear();
    var auth = authDAO.createAuth("cheese_lover67");
    assertEquals("cheese_lover67", authDAO.getUsername(auth.authToken()));
}

@Test
void getUsernameNegative() throws DataAccessException {
    authDAO.clear();

    assertNull(authDAO.getUsername("badtoken"));
}


@Test
void isAuthorizedPositive() throws DataAccessException {
    authDAO.clear();
    var auth = authDAO.createAuth("cheese_lover67");
    assertDoesNotThrow(() -> authDAO.isAuthorized(auth.authToken()));
}

@Test
void  isAuthorizedNegative() throws DataAccessException {
    authDAO.clear();
    assertThrows(UnauthorizedException.class, () -> authDAO.isAuthorized("badtoken"));
}

@Test
void deleteAuthPositive() throws DataAccessException {
    authDAO.clear();
    var auth = authDAO.createAuth("cheese_lover67");
    assertDoesNotThrow(() -> authDAO.deleteAuth(auth.authToken()));
}

@Test
void deleteAuthNegative() throws DataAccessException {
    authDAO.clear();
    assertThrows(UnauthorizedException.class, () -> authDAO.deleteAuth("badtoken"));
    }
}
