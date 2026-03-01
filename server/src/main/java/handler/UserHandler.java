package handler;


import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.*;
import server.UserService;

public class UserHandler extends Handler{
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
            handleError(ctx, e);
        }
    }

    public void handleLogin(Context ctx){
        Gson gson = new Gson();
        try{
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = service.login(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        }catch(DataAccessException e){
            handleError(ctx, e);
        }
    }

    public void handleLogout(Context ctx){
        Gson gson = new Gson();
        try{
            String authToken = ctx.header("authorization");
            LogoutRequest request = new LogoutRequest(authToken);
            service.logout(request);
            ctx.status(200);
            ctx.result("{}");
        } catch(DataAccessException e){
            handleError(ctx, e);
        }
    }
}