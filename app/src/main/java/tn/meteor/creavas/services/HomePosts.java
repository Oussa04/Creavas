package tn.meteor.creavas.services;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tn.meteor.creavas.models.Post;
import tn.meteor.creavas.utils.Constants;

/**
 * Created by Oussaa on 10/12/2017.
 */

public interface HomePosts {

    @POST(Constants.HTTP.HOMEPOSTSGET)
    Call<List<Post>> getHomePosts(@Body JsonObject homePosts);

    @POST(Constants.HTTP.USERPOSTSGET)
    Call<List<Post>> getUserPost(@Body JsonObject user);

    @POST(Constants.HTTP.HOTPOSTSGET)
    Call<List<Post>> getHotPosts();
    @POST(Constants.HTTP.POSTSWITHHASHTAG)
    Call<List<Post>> getPostsByHashtag(@Body JsonObject hashtag);





}
