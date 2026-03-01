package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
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

    @Test
    public void loginPositive() throws DataAccessException{
        initVars();
        userDAO.createUser(new UserData("chess_lover67", "password123", "lol"));
        LoginRequest request = new LoginRequest("chess_lover67", "password123");
        LoginResult result = service.login(request);
        assertNotNull(result);
        assertEquals("chess_lover67", result.username());
        assertNotNull(result.authToken());
        assertInstanceOf(String.class, result.authToken());
    }

    @Test
    public void loginWrongPassword() throws DataAccessException{
        initVars();
        userDAO.createUser(new UserData("chess_lover67", "password123", "lol"));
        LoginRequest request = new LoginRequest("chess_lover67", "wrong");
        assertThrows(UnauthorizedException.class, () -> service.login(request));
    }

    @Test
    public void logoutPositive() throws DataAccessException {
        initVars();
        AuthData authData = authDAO.createAuth("chess_lover67");
        LogoutRequest request = new LogoutRequest(authData.authToken());
        service.logout(request);
        assertThrows(UnauthorizedException.class, () -> authDAO.isAuthorized(authData.authToken()));
    }

    @Test
    public void logoutInvalidToken() throws DataAccessException {
        initVars();
        AuthData authData = authDAO.createAuth("chess_lover67");
        LogoutRequest request = new LogoutRequest("this is the wrong token");
        assertThrows(UnauthorizedException.class, () -> service.logout(request));
    }
}