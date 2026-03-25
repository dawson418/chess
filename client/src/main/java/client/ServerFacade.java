package client;

import com.google.gson.Gson;
import exception.ResponseException;
import request.*;
import result.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public LoginResult register(RegisterRequest requestInfo) throws ResponseException{
        var request = buildRequest("POST", "/user", requestInfo, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public LoginResult login(LoginRequest requestInfo) throws ResponseException{
        var request = buildRequest("POST", "/session", requestInfo, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(LogoutRequest requestInfo)throws ResponseException{
        var request = buildRequest("DELETE", "/session", null, requestInfo.authToken());
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public CreateGameResult createGame(CreateGameRequest requestInfo, String authToken) throws ResponseException{
        var request = buildRequest("POST", "/game", requestInfo, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public ListGamesResult listGames(String authToken) throws ResponseException{
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public void joinGame(JoinGameRequest requestInfo, String authToken) throws ResponseException{
        var request = buildRequest("PUT", "/game", requestInfo, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            String message = "Error: " + status;
            var body = response.body();
            if (body != null && !body.isEmpty()) {
                var map = new Gson().fromJson(body, java.util.Map.class);
                message = map.get("message").toString();
            }
            throw new ResponseException(status, message);
        }

        if (responseClass != null && !response.body().isEmpty()) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
