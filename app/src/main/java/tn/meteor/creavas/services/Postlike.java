package tn.meteor.creavas.services;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tn.meteor.creavas.services.response.PostlikeResult;
import tn.meteor.creavas.utils.Constants;

/**
 * Created by Oussaa on 13/12/2017.
 */

public interface Postlike {
    @POST(Constants.HTTP.POSTLIKE)
    Call<PostlikeResult> addRemovePostlike(@Body JsonObject like);

    @POST(Constants.HTTP.ISLIKED)
    Call<PostlikeResult> checkLiked(@Body JsonObject like);
}
