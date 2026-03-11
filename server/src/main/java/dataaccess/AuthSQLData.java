package dataaccess;

import model.AuthData;

public class AuthSQLData implements AuthDataAccess{

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void isAuthorized(String authToken) throws DataAccessException {

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        return "";
    }
}
