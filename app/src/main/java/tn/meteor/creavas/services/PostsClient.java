package tn.meteor.creavas.services;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tn.meteor.creavas.models.LayerDB;
import tn.meteor.creavas.models.Post;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.services.response.ResultPost;
import tn.meteor.creavas.utils.Constants;

/**
 * Created by lilk on 12/12/2017.
 */

public interface PostsClient {

    @POST(Constants.HTTP.ADDPOST)
    Call<ResultPost> addPost(@Body JsonObject post);

    @POST(Constants.HTTP.ADDLAYERS)
    Call<Result> addLayers(@Body List<LayerDB> layers);

    @POST(Constants.HTTP.GETLAYERS)
    Call<List<LayerDB>> getLayers(@Body JsonObject layers);


    @POST(Constants.HTTP.POSTBYID)
    Call<Post> getPostById(@Body JsonObject idPost);

    @POST(Constants.HTTP.POSTSDELETE)
    Call<Result> deletePostById(@Body JsonObject idPost);

}
