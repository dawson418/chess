package dataaccess;

import model.UserData;

import java.util.HashMap;

public class UserMemoryData implements UserDataAccess{

    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clear() throws DataAccessException{
        users.clear();
    }

    @Override
    public void createUser(UserData data) throws DataAccessException{
        if (users.containsKey(data.username())){
            throw new AlreadyTakenException();
        }
        else{
            users.put(data.username(), data);
        }
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException{
        if (users.containsKey(username) && users.get(username).password().equals(password)){
            return users.get(username);
        } else {
            throw new UnauthorizedException();
        }
    }
}
