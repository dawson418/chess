package client;

import chess.ChessGame;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.*;
import server.Server;
import java.net.HttpURLConnection;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static String url;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        url = "http://localhost:" + port;
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URI(url + "/db").toURL().openConnection();
        connection.setRequestMethod("DELETE");
        connection.getResponseCode();
    }

    @Test
    void registerPositive() throws ResponseException {
        var result = facade.register(new RegisterRequest("Biggy_Cheese", "chedda67", "hehehe@byu.edu"));
        assertNotNull(result.authToken());
        assertEquals("Biggy_Cheese", result.username());
    }

    @Test
    void registerNegative() throws ResponseException {
        facade.register(new RegisterRequest("Biggy_Cheese", "chedda67", "hehehe@byu.edu"));
        assertThrows(ResponseException.class, () -> facade.register(new RegisterRequest("Biggy_Cheese", "chedda67", "hehehe@byu.edu")));
    }

    @Test
    void loginPositive() throws ResponseException {
        facade.register(new RegisterRequest("Biggy_Cheese", "chedda67", "hehehe@byu.edu"));
        var result = facade.login(new LoginRequest("Biggy_Cheese", "chedda67"));
        assertNotNull(result.authToken());
    }

    @Test
    void loginNegative() {
        assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("Biggy_Cheese", "wrong_password")));
    }

    @Test
    void logoutPositive() throws ResponseException {
        var reg = facade.register(new RegisterRequest("Biggy_Cheese", "chedda67", "hehehe@byu.edu"));
        assertDoesNotThrow(() -> facade.logout(new LogoutRequest(reg.authToken())));
    }

    @Test
    void logoutNegative() {
        assertThrows(ResponseException.class, () -> facade.logout(new LogoutRequest("invalid_token")));
    }

    @Test
    void createGamePositive() throws ResponseException {
        var reg = facade.register(new RegisterRequest("Biggy_Cheese", "chedda67", "hehehe@byu.edu"));
        var result = facade.createGame(new CreateGameRequest("TestGame"), reg.authToken());
        assertTrue(result.gameID() > 0);
    }

    @Test
    void createGameNegative() {
        assertThrows(ResponseException.class, () -> facade.createGame(new CreateGameRequest("TestGame"), "invalid_token"));
    }

    @Test
    void listGamesPositive() throws ResponseException {
        var reg = facade.register(new RegisterRequest("Biggy_Cheese", "chedda67", "hehehe@byu.edu"));
        facade.createGame(new CreateGameRequest("Game1"), reg.authToken());
        var result = facade.listGames(reg.authToken());
        assertEquals(1, result.games().size());
    }

    @Test
    void listGamesNegative() {
        assertThrows(ResponseException.class, () -> facade.listGames("invalid_token"));
    }

    @Test
    void joinGamePositive() throws ResponseException {
        var reg = facade.register(new RegisterRequest("Biggy_Cheese", "chedda67", "hehehe@byu.edu"));
        var game = facade.createGame(new CreateGameRequest("Game1"), reg.authToken());
        assertDoesNotThrow(() -> facade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, game.gameID(), "Biggy_Cheese"), reg.authToken()));
    }

    @Test
    void joinGameNegative() throws ResponseException {
        var reg = facade.register(new RegisterRequest("Biggy_Cheese", "chedda67", "hehehe@byu.edu"));
        assertThrows(ResponseException.class, () -> facade.joinGame(
                new JoinGameRequest(ChessGame.TeamColor.WHITE, -1, "Biggy_Cheese"),
                reg.authToken())
        );
    }
}