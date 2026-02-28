package server;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;

public class UserService{
    private final UserDataAccess u;
    private final AuthDataAccess a;

    public UserService(UserDataAccess u, AuthDataAccess a) {
        this.u = u;
        this.a = a;
    }

    public LoginResult register(RegisterRequest registerRequest) throws DataAccessException {
        u.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
        AuthData auth = a.createAuth(registerRequest.username());
        return new LoginResult(auth.username(), auth.authToken());
    }
    public LoginResult login(LoginRequest loginRequest) {
        return null;
    }
    public void logout(LogoutRequest logoutRequest) {
    }
}
