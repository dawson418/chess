package server;

import dataaccess.*;
import model.*;
import request.*;
import result.*;

public class UserService extends Service{
    private final UserDataAccess userDAO;
    private final AuthDataAccess authDAO;

    public UserService(UserDataAccess userDAO, AuthDataAccess authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException();
        }
        userDAO.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
        AuthData auth = authDAO.createAuth(registerRequest.username());
        return new LoginResult(auth.username(), auth.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.username() == null || loginRequest.password() == null){
            throw new BadRequestException();
        }
        userDAO.getUser(loginRequest.username(), loginRequest.password());
        AuthData newAuth = authDAO.createAuth(loginRequest.username());
        return new LoginResult(loginRequest.username(), newAuth.authToken());
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException{
        if (logoutRequest.authToken() == null){
            throw new BadRequestException();
        }
        authDAO.deleteAuth(logoutRequest.authToken());
    }

    public void clear() throws DataAccessException{
        userDAO.clear();
    }
}
