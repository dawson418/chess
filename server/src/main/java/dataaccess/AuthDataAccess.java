package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    AuthData createAuth() throws DataAccessException;
    AuthData getAuth() throws DataAccessException;
    AuthData deleteAuth() throws DataAccessException;
}
