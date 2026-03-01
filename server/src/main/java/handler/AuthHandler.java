package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.AuthService;

public class AuthHandler extends Handler{
    final private AuthService service;

    public AuthHandler(AuthService service) {
        this.service = service;
    }
}
