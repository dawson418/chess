package handler;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import result.ErrorResult;
import server.AuthService;
import server.GameService;
import server.UserService;

import java.util.Map;

public class Handler {
    public void handleError(Context ctx, Exception e){
        String message = e.getMessage();
        ErrorResult result = new ErrorResult(e.getMessage());
        if (e.getClass().equals(BadRequestException.class)){
            ctx.status(400);
        }
        else if(e.getClass().equals(UnauthorizedException.class)){
            ctx.status(401);
        }
        else if(e.getClass().equals(AlreadyTakenException.class) || message.contains("Duplicate entry")){
            ctx.status(403);
        }
        else{ctx.status(500);}
        if (message == null || !message.toLowerCase().contains("error")) {
            message = "Error: " + (message == null ? "internal server error" : message);
        }
        ctx.result(new Gson().toJson(Map.of("message", message)));
    }

    public void handleClear(Context ctx, AuthService as, GameService gs, UserService us){
        try{
            us.clear();
            as.clear();
            gs.clear();
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e){
            handleError(ctx, e);
        }
    }

    public void checkAuth(String authToken, AuthService as) throws DataAccessException{
        as.isAuthorized(authToken);
    }
}
