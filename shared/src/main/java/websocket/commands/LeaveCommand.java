package websocket.commands;

public class LeaveCommand extends UserGameCommand {
    public LeaveCommand(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}
