package server;

import dataaccess.*;
import model.*;
import request.*;
import result.*;

public class UserService{
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
    public LoginResult login(LoginRequest loginRequest) {
        return null;
    }
    public void logout(LogoutRequest logoutRequest) {
    }
}
