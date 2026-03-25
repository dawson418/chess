package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    public enum Code { ServerError, ClientError }
    private final Code code;

    public ResponseException(int status, String message) {
        super(message);
        this.code = (status >= 400 && status < 500) ? Code.ClientError : Code.ServerError;
    }
}
