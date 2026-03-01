package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import server.AuthService;
import server.GameService;


public class GameHandler extends Handler{
    final private GameService gameService;
    final private AuthService authService;

    public GameHandler(GameService service, AuthService authService) {
        this.gameService = service;
        this.authService = authService;
    }

    public void handleCreateGame(Context ctx) throws DataAccessException{
        Gson gson = new Gson();
        try{
            String authToken = ctx.header("authorization");
            checkAuth(authToken, authService);
            CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameResult result =  gameService.createGame(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        }
        catch(DataAccessException e){
            handleError(ctx, e);
        }
    }

    public void handleListGames(Context ctx) throws DataAccessException{
        Gson gson = new Gson();
        try{
            String authToken = ctx.header("authorization");
            checkAuth(authToken, authService);
            ListGamesResult result = gameService.listGames();
            ctx.status(200);
            ctx.result(gson.toJson(result));
        }
        catch(DataAccessException e){
            handleError(ctx, e);
        }
    }

    public void handleJoinGame(Context ctx) throws DataAccessException{
        Gson gson = new Gson();
        try{
            String authToken = ctx.header("authorization");
            checkAuth(authToken, authService);
            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            String username = authService.getUsername(authToken);
            request = new JoinGameRequest(request.playerColor(), request.gameID(), username);
            gameService.joinGame(request);
            ctx.status(200);
            ctx.result("{}");
        }
        catch(DataAccessException e){
            handleError(ctx, e);
        }
    }
}
