package server;

import dataaccess.*;
import handler.GameHandler;
import handler.Handler;
import handler.UserHandler;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {

        UserDataAccess userDAO;
        AuthDataAccess authDAO;
        GameDataAccess gameDAO;

        try {
            userDAO = new UserSQLData();
            authDAO = new AuthSQLData();
            gameDAO = new GameSQLData();
        } catch(DataAccessException e){
            throw new RuntimeException("Failed to initialize SQL DAOs");
        }
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO);
        AuthService authService = new AuthService(authDAO);

        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService, authService);
        Handler masterHandler = new Handler();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", userHandler::handleRegister)
                .post("/session", userHandler::handleLogin)
                .post("/game", gameHandler::handleCreateGame)
                .put("/game", gameHandler::handleJoinGame)
                .get("/game", gameHandler::handleListGames)
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
