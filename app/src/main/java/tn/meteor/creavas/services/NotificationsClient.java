package tn.meteor.creavas.services;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tn.meteor.creavas.models.Notification;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.utils.Constants;

/**
 * Created by lilk on 09/01/2018.
 */

public interface NotificationsClient {


    @POST(Constants.HTTP.GETNOTIFICATIONS)
    Call<List<Notification>> getAllNotifications(@Body JsonObject idUser);

    @POST(Constants.HTTP.DELETENOTIF)
    Call<Result> deleteNotif(@Body JsonObject id);

    @POST(Constants.HTTP.DELETEALL)
    Call<Result> deleteAllNotif(@Body JsonObject idUserConcerned);


}
