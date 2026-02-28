package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class UserMemoryData implements UserDataAccess{

    final private HashMap<String, UserData> users = new HashMap<>();

    public void clear() throws DataAccessException{
        users.clear();
    };
    public void createUser(UserData data) throws DataAccessException{
        if (users.containsKey(data.username())){
            throw new DataAccessException("Username already taken");
        }
        else{
            users.put(data.username(), data);
        }
    };
    public UserData getUser(String username, String password) throws DataAccessException{
        if (users.containsKey(username) && users.get(username).password().equals(password)){
            return users.get(username);
        } else {
            throw new DataAccessException("Incorrect username or password");
        }
    };
}
