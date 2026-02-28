package dataaccess;

import model.AuthData;

import java.util.UUID;
import java.util.HashMap;

public class AuthMemoryData implements AuthDataAccess{

    private final HashMap<String, String> authTokens;

    public AuthMemoryData(){
        this.authTokens = new HashMap<>();
    }

    public AuthMemoryData(HashMap<String, String> authTokens) {
        this.authTokens = authTokens;
    }

    @Override
    public void clear() throws DataAccessException {
        authTokens.clear();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        if(authTokens.containsValue(username)){
            return new AuthData(authTokens.get(username), username);
        }
        String newAuth = UUID.randomUUID().toString();
        authTokens.put(newAuth, username);
        return new AuthData(newAuth, username);
    }

    @Override
    public boolean isAuthorized(String authToken) throws DataAccessException {
        if(!authTokens.containsKey(authToken)){
            throw new DataAccessException("Not authorized");
        }
        else return true;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authTokens.remove(authToken);
    }
}
