package handler;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.RegisterRequest;
import result.LoginResult;
import server.UserService;

public class UserHandler {
    public String handleRegister(String reqData){
        try{Gson gson = new Gson();
        RegisterRequest request = gson.fromJson(reqData, RegisterRequest.class);
        UserService service = new UserService();
        LoginResult result = service.register(request);
        return gson.toJson(result);
        } catch(DataAccessException e){
            //TODO implement error handling here
        }
    }
    Gson serializer = new Gson();


}
