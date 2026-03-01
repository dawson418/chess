package server;

import dataaccess.*;
import model.AuthData;

public class GameService extends Service{
    private final GameDataAccess gameDAO;
    private final AuthDataAccess authDAO;

    public GameService(GameDataAccess gameDAO, AuthDataAccess authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}
