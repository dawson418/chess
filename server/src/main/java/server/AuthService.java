package server;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import handler.Handler;

public class AuthService extends Service {
    private final AuthDataAccess authDAO;

    public AuthService(AuthDataAccess authDAO) {
        this.authDAO = authDAO;
    }

    @Override
    public void clear() throws DataAccessException{
        authDAO.clear();
    }
}
