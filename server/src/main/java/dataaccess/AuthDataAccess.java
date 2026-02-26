package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    AuthData createAuth(int username) throws DataAccessException;
    AuthData getAuth(int authToken) throws DataAccessException;
    void deleteAuth(int authToken) throws DataAccessException;
}
