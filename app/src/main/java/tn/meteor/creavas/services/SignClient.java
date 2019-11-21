package tn.meteor.creavas.services;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.utils.Constants;

/**
 * Created by lilk on 24/11/2017.
 */

public interface SignClient {

    @POST(Constants.HTTP.USERADD)
    Call<User> addUser(@Body User user);

    @POST(Constants.HTTP.USERGET)
    Call<User> getUser(@Body JsonObject userFirebaseID);

    @POST(Constants.HTTP.UPDATETOKEN)
    Call<Result> updateToken(@Body JsonObject token);

    @POST(Constants.HTTP.USERTOKENDELETE)
    Call<Result> deleteToken(@Body JsonObject noToken);

}
