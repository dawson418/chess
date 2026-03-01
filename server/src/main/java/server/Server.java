package server;

import dataaccess.*;
import handler.AuthHandler;
import handler.GameHandler;
import handler.Handler;
import handler.UserHandler;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        UserDataAccess userDAO = new UserMemoryData();
        AuthDataAccess authDAO = new AuthMemoryData();
        GameDataAccess gameDAO = new GameMemoryData();

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        AuthService authService = new AuthService(authDAO);

        UserHandler userHandler = new UserHandler(userService);
        AuthHandler authHandler = new AuthHandler(authService);
        GameHandler gameHandler = new GameHandler();
        Handler masterHandler = new Handler();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", userHandler::handleRegister)
                .post("/session", userHandler::handleLogin)
                .post("/game", gameHandler::handleCreate)
                .delete("/session", userHandler::handleLogout)
                .delete("/db", ctx -> masterHandler.handleClear(ctx, authService, gameService, userService));

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
