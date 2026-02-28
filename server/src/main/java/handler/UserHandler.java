package handler;


import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import request.RegisterRequest;
import result.*;
import server.UserService;

public class UserHandler{
    final private UserService service;

    public UserHandler(UserService userService) {
        this.service = userService;
    }

    public void handleRegister(Context ctx){
        Gson gson = new Gson();
        try{
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            LoginResult result =  service.register(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        }
        catch(DataAccessException e){
            ErrorResult result = new ErrorResult(e.getMessage());
            if (e.getClass().equals(BadRequestException.class)){
                ctx.status(400);
            }
            else if(e.getClass().equals(AlreadyTakenException.class)){
                ctx.status(403);
            }
            else{ctx.status(500);}
            ctx.result(gson.toJson(result));
        }
    }
}