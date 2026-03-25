package client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import request.*;
import ui.BoardUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class ChessClient {
    private String name = null;
    private final ServerFacade server;
    private State state = State.PRELOGIN;
    private String authToken = null;
    private ArrayList<GameData> gameList = new ArrayList<>();

    public ChessClient(String serverUrl) throws ResponseException {
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

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            var result = server.login(new LoginRequest(params[0], params[1]));
            this.authToken = result.authToken();
            state = State.POSTLOGIN;
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
            state = State.POSTLOGIN;
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
            int gameID = gameList.get(i).gameID();
            ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
            if (params[1].equalsIgnoreCase("BLACK")){
                color = ChessGame.TeamColor.BLACK;
            }
            server.joinGame(new JoinGameRequest(color, gameID, this.name), this.authToken);
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
            int gameID = gameList.get(i).gameID();
            ChessGame game = gameList.get(i).game();
            ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
            if (params[0].equalsIgnoreCase("BLACK")){
                color = ChessGame.TeamColor.BLACK;
            }
            server.joinGame(new JoinGameRequest(null, gameID, this.name), this.authToken);
            return new BoardUI(game.getBoard()).drawBoard(color);
        }
        throw new ResponseException(400, "Expected: join <NUMBER>");
    }

    public String help() {
        if (state == State.PRELOGIN) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL>
                    login <USERNAME> <PASSWORD>
                    quit
                    help
                    """;
        }
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

    private void assertSignedIn() throws ResponseException {
        if (state == State.PRELOGIN) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
