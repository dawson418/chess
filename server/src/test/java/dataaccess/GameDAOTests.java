package dataaccess;

import chess.ChessGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {
    private final GameSQLData gameDAO = new GameSQLData();

    public GameDAOTests() throws DataAccessException {
    }

    @Test
    void createGamePositive() throws DataAccessException {
        gameDAO.clear();
        assertDoesNotThrow(() -> gameDAO.createGame("Big Cheese Tourney"));
    }

    @Test
    void createGameNegative() throws DataAccessException {
        gameDAO.clear();
        assertThrows(BadRequestException.class, () -> gameDAO.createGame(""));
    }

    @Test
    void getGamePositive() throws DataAccessException {
        gameDAO.clear();
        var game = gameDAO.createGame("Big Cheese Tourney");
        assertNotNull(gameDAO.getGame(game.gameID()));
    }

    @Test
    void getGameNegative() throws DataAccessException {
        gameDAO.clear();
        assertNull(gameDAO.getGame(9999));
    }


    @Test
    void listGamesPositive() throws DataAccessException {
        gameDAO.clear();
        gameDAO.createGame("Swiss Round 1");
        gameDAO.createGame("Swiss Round 2");
        assertEquals(2, gameDAO.listGames().size());
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        gameDAO.clear();
        var list = gameDAO.listGames();
        assertEquals(0, list.size());
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        gameDAO.clear();
        var game = gameDAO.createGame("Big Cheese Tourney");
        assertDoesNotThrow(() -> gameDAO.joinGame(ChessGame.TeamColor.WHITE, game.gameID(), "cheese_lover67"));
    }

    @Test
    void joinGameNegative() throws DataAccessException {
        gameDAO.clear();
        assertThrows(BadRequestException.class, ()-> gameDAO.joinGame(ChessGame.TeamColor.BLACK, -1, "cheese_lover67"));
    }

    @Test
    void isEmptyPositive() throws DataAccessException {
        gameDAO.clear();
        var game = gameDAO.createGame("Big Cheese Tourney");
        assertTrue(gameDAO.isEmpty(ChessGame.TeamColor.WHITE, game.gameID()));
    }

    @Test
    void  isEmptyNegative() throws DataAccessException {
        gameDAO.clear();
        var game = gameDAO.createGame("Big Cheese Tourney");
        gameDAO.joinGame(ChessGame.TeamColor.BLACK, game.gameID(), "cheese_lover67");
        assertFalse(gameDAO.isEmpty(ChessGame.TeamColor.BLACK, game.gameID()));
    }

    @Test
    void updateGamePositive() throws DataAccessException {
        gameDAO.clear();
        var gameData = gameDAO.createGame("Big Cheese Tourney");
        var game = gameData.game();

        game.setTeamTurn(ChessGame.TeamColor.BLACK);
        assertDoesNotThrow(() -> gameDAO.updateGame(gameData.gameID(), game));

        var retrieved = gameDAO.getGame(gameData.gameID());
        assertEquals(ChessGame.TeamColor.BLACK, retrieved.game().getTeamTurn());
    }

    @Test
    void updateGameNegative() throws DataAccessException {
        gameDAO.clear();
        assertThrows(Exception.class, () -> gameDAO.updateGame(777, new ChessGame()));
    }

    @Test
    void clearGame() throws DataAccessException {
        gameDAO.createGame("Big Cheese Tourney");
        gameDAO.clear();
        assertEquals(0, gameDAO.listGames().size());
    }
}
