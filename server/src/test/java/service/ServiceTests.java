package service;

import dataaccess.AuthDataAccess;
import dataaccess.AuthMemoryData;
import dataaccess.UserDataAccess;
import dataaccess.UserMemoryData;
import server.UserService;

public class ServiceTests {

    protected UserService service;
    protected UserDataAccess userDAO;
    protected AuthDataAccess authDAO;

    public void initVars(){
        this.userDAO = new UserMemoryData();
        this.authDAO = new AuthMemoryData();
        this.service = new UserService(userDAO, authDAO);
    }
}
