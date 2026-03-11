package dataaccess;

import model.UserData;

public class UserSQLData implements UserDataAccess{
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void createUser(UserData data) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        return null;
    }
}
