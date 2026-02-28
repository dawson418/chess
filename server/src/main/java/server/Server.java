package server;

import dataaccess.AuthDataAccess;
import dataaccess.AuthMemoryData;
import dataaccess.UserDataAccess;
import dataaccess.UserMemoryData;
import handler.UserHandler;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        UserDataAccess userDAO = new UserMemoryData();
        AuthDataAccess authDAO = new AuthMemoryData();

        UserService service = new UserService(userDAO, authDAO);

        UserHandler handler = new UserHandler(service);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", handler::handleRegister);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
