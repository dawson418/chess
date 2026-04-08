package websocket.messages;

import chess.ChessGame;

public class ErrorMessage extends ServerMessage{
    private final String errorMessage;

    public ErrorMessage(ServerMessageType type, String errorMessage){
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getMessage(){
        return errorMessage;
    }
}
