package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import request.*;
import ui.BoardUI;
import websocket.messages.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static client.State.IN_GAME;
import static client.State.POSTLOGIN;
import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageHandler{
    private String name = null;
    private final ServerFacade server;
    private final String serverUrl;
    private int currGameID = -1;
    private chess.ChessGame currGame = null;
    private State state = State.PRELOGIN;
    private String authToken = null;
    private ArrayList<GameData> gameList = new ArrayList<>();
    private ChessGame.TeamColor playerColor = ChessGame.TeamColor.WHITE;
    private WebSocketFacade ws;

    public ChessClient(String serverUrl) throws ResponseException {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(WHITE_PAWN + "Welcome to chess!" + WHITE_PAWN);
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == IN_GAME) {
                return switch (cmd) {
                    case "redraw" -> redrawBoard();
                    case "leave" -> leaveGame();
                    case "move" -> makeMove(params);
                    case "resign" -> resignGame();
                    case "highlight" -> highlightMoves(params);
                    case "help" -> help();
                    default -> help();
                };
            }
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> join(params);
                case "logout" -> logout();
                case "observe" -> observe(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String highlightMoves(String... params) throws ResponseException{
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: highlight <POSITION>");
        }
        if (currGame == null) {
            throw new ResponseException(400, "No game loaded");
        }
        ChessPosition position = parsePosition(params[0]);
        Collection<ChessMove> validMoves = currGame.validMoves(position);
        if (validMoves == null || validMoves.isEmpty()){
            return "This piece cannot move!";
        }
        String highlightedBoard = new BoardUI(currGame.getBoard()).drawBoard(playerColor, position, validMoves);
        return "\n" + highlightedBoard;
    }

    private String resignGame() throws ResponseException{
        System.out.print("Resign game? (y/n)");
        String input = new Scanner(System.in).nextLine().toLowerCase();
        if (input.equals("yes")) {
            ws.resign(authToken, currGameID);
        }
        return "";
    }

    private String makeMove(String... params)throws ResponseException{
        if (params.length < 2){
            throw new ResponseException(400, "Expected: move <START> <END> [PROMOTION]");
        }

        ChessPosition startPos = parsePosition(params[0]);
        ChessPosition endPos = parsePosition(params[1]);

        ChessPiece.PieceType promotionPiece = null;
        if (params.length == 3) {
            promotionPiece = switch (params[2].toUpperCase()) {
                case "QUEEN" -> chess.ChessPiece.PieceType.QUEEN;
                case "ROOK" -> chess.ChessPiece.PieceType.ROOK;
                case "BISHOP" -> chess.ChessPiece.PieceType.BISHOP;
                case "KNIGHT" -> chess.ChessPiece.PieceType.KNIGHT;
                default -> null;
            };
        }
        ChessMove move = new ChessMove(startPos, endPos, promotionPiece);
        ws.makeMove(authToken, currGameID, move);
        return "";
    }

    private chess.ChessPosition parsePosition(String pos) throws ResponseException {
        try {
            if (pos.length() != 2) throw new Exception();
            int col = pos.charAt(0) - 'a' + 1;
            int row = Integer.parseInt(pos.substring(1, 2));
            if (col < 1 || col > 8 || row < 1 || row > 8) throw new Exception();
            return new chess.ChessPosition(row, col);
        } catch (Exception e) {
            throw new ResponseException(400, "Invalid position: " + pos);
        }
    }

    private String leaveGame() throws ResponseException{
        ws.leave(authToken, currGameID);
        this.state = State.POSTLOGIN;
        this.currGameID = -1;
        this.ws = null;
        return "You left the game.";
    }

    private String redrawBoard() throws ResponseException{
        if (currGame == null) {
            throw new ResponseException(400, "No game loaded");
        }
        String board = new BoardUI(currGame.getBoard()).drawBoard(playerColor);
        return "\n" + board;
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            var result = server.login(new LoginRequest(params[0], params[1]));
            this.authToken = result.authToken();
            state = POSTLOGIN;
            this.name = params[0];
            return "Login success";
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String logout() throws ResponseException {
        assertSignedIn();
        server.logout(new LogoutRequest(this.authToken));
        this.state = State.PRELOGIN;
        return "Logged out";
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            var result = server.register(new RegisterRequest(params[0], params[1], params[2]));
            this.authToken = result.authToken();
            state = POSTLOGIN;
            this.name = params[0];
            return "Register success";
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            server.createGame(new CreateGameRequest(params[0]), this.authToken);
            return "Game created";
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        var result = server.listGames(this.authToken).games();
        this.gameList = new ArrayList<>(result);
        var output = new StringBuilder();
        for (int i = 0; i < gameList.size(); i++) {
            GameData game = gameList.get(i);
            output.append(String.format("%d. %s (White: %s, Black: %s)\n",
                    i + 1, game.gameName(), game.whiteUsername(), game.blackUsername()));
        }
        return output.toString();
    }

    public String join(String... params) throws ResponseException{
        if (params.length == 2){
            assertSignedIn();
            int i;
            try {
                i = Integer.parseInt(params[0]) - 1;
            }
            catch (NumberFormatException e){
                throw new ResponseException(400, "Please enter a valid game number");
            }
            if (i < 0 || i > gameList.size()){
                throw new ResponseException(400, "Please enter a valid game number");
            }
            ChessGame game = gameList.get(i).game();
            ChessGame.TeamColor color;
            int gameID = gameList.get(i).gameID();
            if (params[1].equalsIgnoreCase("WHITE")){
                color = ChessGame.TeamColor.WHITE;
            }
            else if (params[1].equalsIgnoreCase("BLACK")){
                color = ChessGame.TeamColor.BLACK;
            } else {
                throw new ResponseException(400, "Error: Not a valid color");
            }
            server.joinGame(new JoinGameRequest(color, gameID, this.name), this.authToken);
            playerColor = color;
            ws = new WebSocketFacade(serverUrl, this);
            ws.connect(authToken, gameID);
            this.currGameID = gameID;
            this.state = State.IN_GAME;
            return new BoardUI(game.getBoard()).drawBoard(color);
        }
        throw new ResponseException(400, "Expected: join <NUMBER> [WHITE|BLACK]");
    }

    public String observe(String... params) throws ResponseException{
        assertSignedIn();
        if (params.length == 1){
            assertSignedIn();
            int i;
            try {
                i = Integer.parseInt(params[0]) - 1;
            }
            catch (NumberFormatException e){
                throw new ResponseException(400, "Please enter a valid game number");
            }
            if (i < 0 || i > gameList.size()){
                throw new ResponseException(400, "Please enter a valid game number");
            }
            ChessGame game = gameList.get(i).game();
            playerColor = ChessGame.TeamColor.WHITE;
            if (params[0].equalsIgnoreCase("BLACK")){
                playerColor = ChessGame.TeamColor.BLACK;
            }
            ws = new WebSocketFacade(this.serverUrl, this);
            ws.connect(authToken, gameList.get(i).gameID());
            this.currGameID = gameList.get(i).gameID();
            this.state = State.IN_GAME;
            return new BoardUI(game.getBoard()).drawBoard(playerColor);
        }
        throw new ResponseException(400, "Expected: join <NUMBER>");
    }

    public String help() {
        switch(state)
        {
            case PRELOGIN ->{
                return """
                    register <USERNAME> <PASSWORD> <EMAIL>
                    login <USERNAME> <PASSWORD>
                    quit
                    help
                    """;
            }
            case POSTLOGIN ->{
                return """
                    create <NAME>
                    list
                    join <ID> [WHITE|BLACK]
                    observe <ID>
                    logout
                    quit
                    help
                    """;
            }
            case IN_GAME -> {
                return """
                    redraw
                    leave
                    move <START_POSITION> <END_POSITION>
                    resign
                    highlight <POSITION>
                    help""";
            }

            default -> {
                return null;
            }
        }

    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.PRELOGIN) {
            throw new ResponseException(400, "You must sign in");
        }
    }


    @Override
    public void notify(String message) {
        ServerMessage genMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (genMessage.getServerMessageType()) {
            case NOTIFICATION -> handleNotification(message);
            case ERROR -> handleError(message);
            case LOAD_GAME -> handleLoadGame(message);
        }
    }

    private void handleLoadGame(String message) {
        LoadGameMessage gameMsg = new Gson().fromJson(message, LoadGameMessage.class);
        this.currGame = gameMsg.getGame();
        String board = new BoardUI(gameMsg.getGame().getBoard()).drawBoard(this.playerColor);
        System.out.println("\n" + board);
        printPrompt();
    }

    private void handleError(String message) {
        ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
        System.out.println("\n" + SET_TEXT_COLOR_RED + error.getMessage() + RESET_TEXT_COLOR);
    }

    private void handleNotification(String message){
        NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
        System.out.println("\n" + SET_TEXT_COLOR_BLUE + notification.getMessage() + RESET_TEXT_COLOR);
        printPrompt();
    }
}
