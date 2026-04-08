package websocket.commands;

public class ResignCommand extends UserGameCommand {
    public ResignCommand(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}
