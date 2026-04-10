package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
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
import websocket.messages.ServerMessage;

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

    private void makeMove(String message, Session session) throws Exception {
        MakeMoveCommand cmd = new Gson().fromJson(message, MakeMoveCommand.class);
        String authToken = cmd.getAuthToken();
        int gameID = cmd.getGameID();
        ChessMove move = cmd.getMove();
        String username;

        try {
            username = authDAO.getUsername(authToken);
            if (username == null) throw new UnauthorizedException();
        } catch (DataAccessException e){
            throw new UnauthorizedException();
        }

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData ==null) throw new BadRequestException();
        ChessGame game = gameData.game();

        ChessGame.TeamColor playerColor = null;
        if(username.equals(gameData.whiteUsername())){
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())){
            playerColor = ChessGame.TeamColor.BLACK;
        }
        if (playerColor == null) {
            throw new Exception("Observers can't make moves!");
        }
        if(game.getTeamTurn() != playerColor){
            throw new Exception("It's not your turn!");
        }
        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != playerColor) {
            throw new Exception("Invalid piece");
        }

        game.makeMove(move);
        gameDAO.updateGame(gameID, game);
        LoadGameMessage loadMsg = new LoadGameMessage(LOAD_GAME, game);
        connections.broadcast(gameID, null, new Gson().toJson(loadMsg));

        String notificationMsg = username + " moved " + piece.toString();
        NotificationMessage notification = new NotificationMessage(NOTIFICATION, notificationMsg);
        connections.broadcast(gameID, authToken, new Gson().toJson(notification));()
    }

    private void leave(String message, Session session) throws Exception {
        LeaveCommand cmd = new Gson().fromJson(message, LeaveCommand.class);
        String authToken = cmd.getAuthToken();
        int gameID = cmd.getGameID();
        String username = authDAO.getUsername(authToken);
        model.GameData gameData = gameDAO.getGame(gameID);
        connections.remove(gameID, authToken);
        if (username.equals(gameData.whiteUsername())) {
            gameDAO.joinGame(ChessGame.TeamColor.WHITE, gameID, null);
        } else if (username.equals(gameData.blackUsername())) {
            gameDAO.joinGame(ChessGame.TeamColor.BLACK, gameID, null);
        }
        String leaveMsg = username + " left the game";
        NotificationMessage notification = new NotificationMessage(NOTIFICATION, leaveMsg);
        connections.broadcast(gameID, authToken, new Gson().toJson(notification));
    }

    private void resign(String message, Session session) throws Exception {
        ResignCommand cmd = new Gson().fromJson(message, ResignCommand.class);
        String authToken = cmd.getAuthToken();
        int gameID = cmd.getGameID();
        String username = authDAO.getUsername(authToken);
        model.GameData gameData = gameDAO.getGame(gameID);
        if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
            throw new Exception("observers cannot resign");
        }
        if (gameData.game().isOver()) {
            throw new Exception("game already over");
        }
        gameData.game().setGameOver();
        gameDAO.updateGame(gameID, gameData.game());
        String notificationMsg = username + " resigned the game";
        NotificationMessage notification = new NotificationMessage(NOTIFICATION, notificationMsg);
        connections.broadcast(gameID, null, new Gson().toJson(notification));
    }


    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }


}