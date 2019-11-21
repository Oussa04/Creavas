package tn.meteor.creavas.services;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tn.meteor.creavas.models.Comment;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.utils.Constants;

/**
 * Created by Oussaa on 12/12/2017.
 */

public interface CommentsClient {
    @POST(Constants.HTTP.COMMENTGET)
    Call<List<Comment>> getComments(@Body JsonObject idPost);

    @POST(Constants.HTTP.COMMENTADD)
    Call<Result> addComment(@Body JsonObject comment);

    @POST(Constants.HTTP.COMMENTDELETE)
    Call<Result> deleteComment(@Body Comment comment);




}
