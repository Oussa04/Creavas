package tn.meteor.creavas.services;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.utils.Constants;

/**
 * Created by lilk on 24/11/2017.
 */

public interface ProfilesClient {

    @POST(Constants.HTTP.USERSSEARCH)
    Call<List<User>> searchUsers(@Body JsonObject search);


    @POST(Constants.HTTP.GETFOLLOWED)
    Call<List<User>> getFollowed(@Body JsonObject idUser);


    @POST(Constants.HTTP.GETFOLLOWERS)
    Call<List<User>> getFollowers(@Body JsonObject idUser);

    @POST(Constants.HTTP.FOLLOW)
    Call<Result> follow(
            @Body JsonObject follow
    );

    @POST(Constants.HTTP.UNFOLLOW)
    Call<Result> unfollow(
            @Body JsonObject unfollow
    );
}
