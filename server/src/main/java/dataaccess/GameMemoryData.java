package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.List;

public class GameMemoryData implements GameDataAccess{

    @Override
    public void clear() throws DataAccessException {

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
    public boolean checkEmpty(ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        return false;
    }

    @Override
    public GameData updateGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }
}
