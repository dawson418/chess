package client;

import com.google.gson.Gson;
import exception.ResponseException;
import request.CreateGameRequest;
import request.LoginRequest;
import request.RegisterRequest;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private State state = State.PRELOGIN;
    private String authToken = null;

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
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
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
//                case "list" ->
//                case "join" ->
//                case "logout" ->
//                case "observe" ->
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            var result = server.login(new LoginRequest(params[0], params[1]));
            this.authToken = result.authToken();
            state = State.POSTLOGIN;
            return "Login success";
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            var result = server.register(new RegisterRequest(params[0], params[1], params[3]));
            this.authToken = result.authToken();
            state = State.POSTLOGIN;
            return "Register success";
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            var result = server.createGame(new CreateGameRequest(params[0]), this.authToken);
            return "Game created";
        }
        throw new ResponseException(400, "Expected: <NAME>");
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
