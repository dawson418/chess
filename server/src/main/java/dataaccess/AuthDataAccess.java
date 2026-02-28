package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    public void clear() throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    boolean isAuthorized(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}
