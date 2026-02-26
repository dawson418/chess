package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDataAccess {
    void clear() throws DataAccessException;
    AuthData createUser() throws DataAccessException;
    UserData getUser() throws DataAccessException;
}
