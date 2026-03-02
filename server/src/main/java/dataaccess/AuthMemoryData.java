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
        String newAuth = UUID.randomUUID().toString();
        authTokens.put(newAuth, username);
        return new AuthData(newAuth, username);
    }

    @Override
    public void isAuthorized(String authToken) throws DataAccessException {
        if(!authTokens.containsKey(authToken)){
            throw new UnauthorizedException();
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if(!authTokens.containsKey(authToken)) {
            throw new UnauthorizedException();
        }
        authTokens.remove(authToken);
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }
}
