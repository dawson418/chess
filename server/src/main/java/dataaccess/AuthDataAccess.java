package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    void clear() throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    void isAuthorized(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    String getUsername(String authToken) throws DataAccessException;
}
