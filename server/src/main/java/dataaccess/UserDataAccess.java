package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDataAccess {
    void clear() throws DataAccessException;
    AuthData createUser(UserData data) throws DataAccessException;
    UserData getUser(String username, String password) throws DataAccessException;
}
