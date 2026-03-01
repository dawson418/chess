package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.LoginResult;
import server.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserService service;
    private UserDataAccess userDAO;
    private AuthDataAccess authDAO;

    public void initVars(){
        userDAO = new UserMemoryData();
        authDAO = new AuthMemoryData();
        service = new UserService(userDAO, authDAO);
    }

    @Test
    public void registerPositive() throws DataAccessException {
        initVars();
        RegisterRequest request = new RegisterRequest("chess_lover67", "password123", "test@byu.edu");
        LoginResult result = service.register(request);
        assertNotNull(result);
        assertEquals("chess_lover67", result.username());
        assertNotNull(result.authToken());
        assertInstanceOf(String.class, result.authToken());
    }

    @Test
    public void registerUserTaken() throws DataAccessException{
        initVars();
        service.register(new RegisterRequest("bob", "123", "hey@gmail.com"));
        RegisterRequest request = new RegisterRequest("bob", "password123", "test@byu.edu");
        assertThrows(AlreadyTakenException.class, () -> service.register(request));
    }


}