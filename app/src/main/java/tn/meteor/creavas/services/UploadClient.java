package tn.meteor.creavas.services;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.utils.Constants;

/**
 * Created by lilk on 25/11/2017.
 */

public interface UploadClient {
    @Multipart
    @POST(Constants.HTTP.UPLOADIMAGE)
    Call<Result> uploadProfileImgUser(
            @Part MultipartBody.Part file
    );



    @Multipart
    @POST(Constants.HTTP.UPLOADPOSTIMAGE)
    Call<Result> uploadMedia(
            @Part MultipartBody.Part file
    );
}
