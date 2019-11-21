package tn.meteor.creavas.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.meteor.creavas.R;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.services.ServiceFactory;
import tn.meteor.creavas.services.SignClient;


public class SplashActivity extends Activity {

    private static final int SPLASH_TIME_OUT = 1000;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            setContentView(R.layout.activity_splash_screen);
            mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            getUserFromDB();
        }else{

            Intent i = new Intent(SplashActivity.this, UserSignActivity.class);
            startActivity(i);
            finish();

        }


    }
    private SharedPreferences preferences;

    public void editpreferences(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        preferences.edit().putString("user",json).apply();
    }


    protected void getUserFromDB() {

        SignClient client = ServiceFactory.getApiClient().create(SignClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("firebaseid", mAuth.getUid());
        Call<User> call = client.getUser(parameters);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body().getUsername() == null) { //user not in registred in server yet (only in Firebase)
                    Intent intent = new Intent(SplashActivity.this, UserSignActivity.class);
                    startActivity(intent);
                    finish();
                } else { //user registred in server
                    editpreferences(response.body());
                    Intent intent = new Intent(SplashActivity.this, MainHomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

}
