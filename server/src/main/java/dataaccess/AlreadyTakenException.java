package dataaccess;

public class AlreadyTakenException extends DataAccessException {
    public AlreadyTakenException() {
        super("Error: already taken");
    }
}
