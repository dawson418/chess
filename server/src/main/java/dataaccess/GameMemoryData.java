package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class GameMemoryData implements GameDataAccess{

    private int currID = 1233;
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public GameData createGame(String name) throws DataAccessException {
        int id = getNextID();
        games.put(id, new GameData(id, null, null, name, new ChessGame()));
        return games.get(id);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if(!games.containsKey(gameID)){
            throw new DataAccessException("No game with that Game ID");
        }
        return games.get(gameID);
    }

    @Override
    public boolean isEmpty(ChessGame.TeamColor playerColor, int gameID) {
        GameData game = games.get(gameID);
        if(playerColor.equals(ChessGame.TeamColor.BLACK)){
            return game.blackUsername() == null;
        }
        else{
            return game.whiteUsername() == null;
        }
    }

    @Override
    public void joinGame(ChessGame.TeamColor playerColor, int gameID, String username) throws DataAccessException{
        if(!games.containsKey(gameID)){
            throw new DataAccessException("Game doesn't exist");
        }
        GameData old = games.get(gameID);
        if (playerColor.equals(ChessGame.TeamColor.WHITE)){
            games.put(gameID, new GameData(gameID, username, old.blackUsername(),old.gameName(), old.game()));
        }
        else{
            games.put(gameID, new GameData(gameID, old.whiteUsername(), username,old.gameName(), old.game()));
        }
    }

    @Override
    public GameData updateGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    private int getNextID(){
        currID++;
        return currID;
    }
}
