package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    public void clear() throws DataAccessException;
    AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuth(int authToken) throws DataAccessException;
    void deleteAuth(int authToken) throws DataAccessException;
}
