package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDataAccess authDAO;
    private final GameDataAccess gameDAO;

    public WebSocketHandler(AuthDataAccess authDAO, GameDataAccess gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        String message = ctx.message();
        UserGameCommand genCmd = new Gson().fromJson(message, UserGameCommand.class);
        try {
            switch (genCmd.getCommandType()) {
                case CONNECT-> connect(message, ctx.session);
                case MAKE_MOVE-> makeMove(message, ctx.session);
                case LEAVE -> leave(message, ctx.session);
                case RESIGN -> resign(message, ctx.session);
            }
        } catch (Exception ex) {
            try {
                ErrorMessage error = new ErrorMessage(ERROR,"Error: " + ex.getMessage());
                ctx.session.getRemote().sendString(new Gson().toJson(error));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void connect(String message, Session session) throws Exception {
        ConnectCommand cmd = new Gson().fromJson(message, ConnectCommand.class);
        String username;
        try {
            authDAO.isAuthorized(cmd.getAuthToken());
            username = authDAO.getUsername(cmd.getAuthToken());
        } catch (DataAccessException e){
            throw new UnauthorizedException();
        }
        GameData gameData;
        try {
            gameData = gameDAO.getGame(cmd.getGameID());
            if (gameData == null){
                throw new BadRequestException();
            }
        } catch (DataAccessException e){
            throw new BadRequestException();
        }
        connections.add(cmd.getGameID(), cmd.getAuthToken(), session);

        LoadGameMessage loadGameMessage = new LoadGameMessage(LOAD_GAME, gameData.game());
        connections.soloMessage(cmd.getGameID(), cmd.getAuthToken(), new Gson().toJson(loadGameMessage));

        String role = "observer";
        if (username.equals(gameData.whiteUsername())) {
            role = "White";
        } else if (username.equals(gameData.blackUsername())) {
            role = "Black";
        }

        String joinMsg = username + "joined the game as" + role;
        NotificationMessage notification = new NotificationMessage(NOTIFICATION, joinMsg);
        connections.broadcast(cmd.getGameID(), cmd.getAuthToken(), new Gson().toJson(notification));
    }

    private void makeMove(String message, Session session) throws IOException {
        MakeMoveCommand cmd = new Gson().fromJson(message, MakeMoveCommand.class);
    }

    private void leave(String message, Session session) throws IOException {
        LeaveCommand cmd = new Gson().fromJson(message, LeaveCommand.class);
        connections.remove(cmd.getGameID(), cmd.getAuthToken());
    }

    private void resign(String message, Session session) throws IOException {
        ResignCommand cmd = new Gson().fromJson(message, ResignCommand.class);
    }


    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }


}