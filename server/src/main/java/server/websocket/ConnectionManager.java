package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String,Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String authToken, Session session) {
        connections.putIfAbsent(gameID, new ConcurrentHashMap<>());
        ConcurrentHashMap<String, Session> gameLobby = connections.get(gameID);
        gameLobby.put(authToken, session);
    }

    public void remove(int gameID, String authToken) {
        ConcurrentHashMap<String, Session> gameConnections = connections.get(gameID);
        if (gameConnections != null){
            gameConnections.remove(authToken);
            if (gameConnections.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, String excludedToken, String msg) throws IOException {
        ConcurrentHashMap<String, Session> gameConnections = connections.get(gameID);
        if (gameConnections != null) {
            for (var entry : gameConnections.entrySet()) {
                String authToken = entry.getKey();
                Session session = entry.getValue();
                if (session.isOpen() && (excludedToken == null || !excludedToken.equals(authToken))){
                    session.getRemote().sendString(msg);
                }
            }
        }
    }

    public void soloMessage(int gameID, String targetToken, String msg) throws IOException{
        ConcurrentHashMap<String, Session> gameConnections = connections.get(gameID);
        if (gameConnections != null) {
            Session session = gameConnections.get(targetToken);
            if (session != null && session.isOpen()){
                session.getRemote().sendString(msg);
            }
        }
    }
}
