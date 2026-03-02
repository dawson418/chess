package server;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;

public class AuthService extends Service {
    private final AuthDataAccess authDAO;

    public AuthService(AuthDataAccess authDAO) {
        this.authDAO = authDAO;
    }

    @Override
    public void clear() throws DataAccessException{
        authDAO.clear();
    }

    public void isAuthorized(String authToken) throws DataAccessException{
        authDAO.isAuthorized(authToken);
    }

    public String getUsername(String authToken) throws DataAccessException{
        return authDAO.getUsername(authToken);
    }
}