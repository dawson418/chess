package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import server.AuthService;
import server.GameService;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private AuthService authService;
    private GameService gameService;
    private AuthDataAccess authDAO;
    private GameDataAccess gameDAO;

    public void initVars(){
        this.gameDAO = new GameMemoryData();
        this.authDAO = new AuthMemoryData();
        this.gameService = new GameService(gameDAO);
        this.authService = new AuthService(authDAO);
    }

    @Test
    public void createGamePositive() throws DataAccessException{
        initVars();
        CreateGameRequest request = new CreateGameRequest("Dawson's cool game of chess");
        CreateGameResult result = gameService.createGame(request);
        GameData createdGame = gameDAO.getGame(result.gameID());
        assertNotNull(createdGame);
        assertEquals("Dawson's cool game of chess", createdGame.gameName());
    }

    @Test
    public void createGameUnauthorized() throws DataAccessException{
        initVars();
        CreateGameRequest request = new CreateGameRequest(null);
        assertThrows(BadRequestException.class, () -> gameService.createGame(request));
    }

    @Test
    public void listGamesPositive() throws DataAccessException{
        initVars();
        CreateGameRequest request = new CreateGameRequest("Dawson's cool game of chess");
        CreateGameResult result1 = gameService.createGame(request);
        request = new CreateGameRequest("Dawson's slightly less cool game of chess");
        CreateGameResult result2 = gameService.createGame(request);
        ListGamesResult resultList = gameService.listGames();
        assertEquals(result1.gameID(), resultList.games().stream().toList().getFirst().gameID());
        assertEquals(result2.gameID(), resultList.games().stream().toList().get(1).gameID());
    }

    @Test
    public void listGamesNegative() throws DataAccessException{
        initVars();
        ListGamesResult resultList = gameService.listGames();

    }

    @Test
    public void joinGamePositive() throws DataAccessException{
        initVars();
        CreateGameRequest createRequest = new CreateGameRequest("Dawson's cool game of chess");
        CreateGameResult result = gameService.createGame(createRequest);
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, result.gameID(), "danish418");
        gameService.joinGame(request);
        GameData joinedGame = gameDAO.getGame(result.gameID());
        assertEquals("danish418", joinedGame.whiteUsername());
        assertEquals("Dawson's cool game of chess", joinedGame.gameName());
    }

    @Test
    public void joinGameInvalid() throws DataAccessException{
        initVars();
        CreateGameRequest createRequest = new CreateGameRequest("Dawson's cool game of chess");
        CreateGameResult result = gameService.createGame(createRequest);
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, result.gameID(), null);
        assertThrows(BadRequestException.class, () -> gameService.joinGame(request));
    }
}
