package server;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;

public class GameService extends Service{
    private final GameDataAccess gameDAO;

    public GameService(GameDataAccess gameDAO) {
        this.gameDAO = gameDAO;
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

    public CreateGameResult createGame(CreateGameRequest request)throws DataAccessException {
        if (request.gameName() == null) {
            throw new BadRequestException();
        }
        GameData data = gameDAO.createGame(request.gameName());
        return new CreateGameResult(data.gameID());
    }

    public ListGamesResult listGames() throws DataAccessException {
        return new ListGamesResult(gameDAO.listGames());
    }
}