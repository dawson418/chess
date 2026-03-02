package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;
import request.JoinGameRequest;
import server.AuthService;
import server.GameService;
import server.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    protected UserService userService;
    protected GameService gameService;
    protected AuthService authService;
    protected UserDataAccess userDAO;
    protected AuthDataAccess authDAO;
    protected GameDataAccess gameDAO;

    public void initVars(){
        this.authDAO = new AuthMemoryData();
        this.userDAO = new UserMemoryData();
        this.gameDAO = new GameMemoryData();
        this.gameService = new GameService(gameDAO);
        this.userService = new UserService(userDAO, authDAO);
        this.authService = new AuthService(authDAO);
    }

    @Test
    public void clearPositive() throws DataAccessException{
        initVars();
        userDAO.createUser(new UserData("chess_lover67", "password123", "lol"));
        String authToken = authDAO.createAuth("chess_lover67").authToken();
        gameDAO.createGame("chess game");
        userService.clear();
        authService.clear();
        gameService.clear();
        assertThrows(UnauthorizedException.class, () -> userDAO.getUser("chess_lover67", "password123"));
        assertTrue(gameService.listGames().games().isEmpty());
        assertThrows(UnauthorizedException.class, () -> authService.isAuthorized(authToken));
    }

    @Test
    public void isAuthorizedPositive() throws DataAccessException{
        initVars();
        String authToken = authDAO.createAuth("chess_lover67").authToken();
        assertDoesNotThrow(() -> authService.isAuthorized(authToken));
    }

    @Test
    public void isAuthorizedNegative() throws DataAccessException{
        initVars();
        String authToken = authDAO.createAuth("chess_lover67").authToken();
        authDAO.clear();
        assertThrows(UnauthorizedException.class, () -> authService.isAuthorized(authToken));
    }

    @Test
    public void getUsernamePositive() throws DataAccessException{
        initVars();
        String authToken = authDAO.createAuth("chess_lover67").authToken();
        assertEquals("chess_lover67", authService.getUsername(authToken));
    }

    @Test
    public void getUsernameNegative() throws DataAccessException{
        initVars();
        String authToken = authDAO.createAuth("chess_lover67").authToken();
        authDAO.clear();
        assertNull(authService.getUsername(authToken));
    }
}
