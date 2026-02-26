package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDataAccess {
    GameData createGame() throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    GameData updateGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
}
