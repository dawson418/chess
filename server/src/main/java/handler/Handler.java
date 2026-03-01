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
import server.Service;
import server.UserService;

public class Handler {
    public void handleError(Context ctx, Exception e){
        Gson gson = new Gson();
        ErrorResult result = new ErrorResult(e.getMessage());
        if (e.getClass().equals(BadRequestException.class)){
            ctx.status(400);
        }
        else if(e.getClass().equals(UnauthorizedException.class)){
            ctx.status(401);
        }
        else if(e.getClass().equals(AlreadyTakenException.class)){
            ctx.status(403);
        }
        else{ctx.status(500);}
        ctx.result(gson.toJson(result));
    }

    public void handleClear(Context ctx, AuthService as, GameService gs, UserService us){
        Gson gson = new Gson();
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
}
