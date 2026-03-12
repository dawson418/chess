package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.List;

public class GameSQLData extends SQLData implements GameDataAccess{

    public GameSQLData() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    @Override
    public GameData createGame(String name) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public boolean isEmpty(ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        return false;
    }

    @Override
    public void joinGame(ChessGame.TeamColor playerColor, int gameID, String username) throws DataAccessException {

    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }
}
