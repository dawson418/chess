package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDataAccess {
    public void clear() throws DataAccessException;

    GameData createGame(String name) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    boolean isEmpty(ChessGame.TeamColor playerColor, int gameID) throws DataAccessException;

    void joinGame(ChessGame.TeamColor playerColor, int gameID, String username) throws DataAccessException;

    GameData updateGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;
}
