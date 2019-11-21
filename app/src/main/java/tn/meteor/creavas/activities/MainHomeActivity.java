package tn.meteor.creavas.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.alexbykov.nopermission.PermissionHelper;
import tn.meteor.creavas.R;
import tn.meteor.creavas.fragments.ExploreFragment;
import tn.meteor.creavas.fragments.HomeFragment;
import tn.meteor.creavas.fragments.MyProfileFragment;
import tn.meteor.creavas.fragments.NotificationsFragment;
import tn.meteor.creavas.kitchen.ui.CreavasActivity;
import tn.meteor.creavas.services.ServiceFactory;
import tn.meteor.creavas.services.SignClient;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.utils.Constants;
import tn.meteor.creavas.utils.UniversalImageLoader;


public class MainHomeActivity extends AppCompatActivity {
  public   SpaceNavigationView homeNavigationBar;

    SharedPreferences pref;
    String regId;
    private FirebaseAuth mAuth;
    private  ShowcaseView showcaseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
//        Toast.makeText(this, CurrentUser.getUser().toString(), Toast.LENGTH_SHORT).show();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.INTERNET)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {/*..*/}

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                })
                .check();



        homeNavigationBar = (SpaceNavigationView) findViewById(R.id.space);
        homeNavigationBar.initWithSaveInstanceState(savedInstanceState);
        homeNavigationBar.setCentreButtonIcon(R.drawable.ic_photo_filter);
        homeNavigationBar.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_home));
        homeNavigationBar.addSpaceItem(new SpaceItem("EXPLORE", R.drawable.ic_public));
        homeNavigationBar.addSpaceItem(new SpaceItem("NOTIFICATIONS", R.drawable.ic_notifications));
        homeNavigationBar.addSpaceItem(new SpaceItem("PROFILE", R.drawable.ic_person));
        homeNavigationBar.showIconOnly();
        homeNavigationBar.shouldShowFullBadgeText(true);
        homeNavigationBar.setCentreButtonIconColorFilterEnabled(false);

        /*new MaterialShowcaseView.Builder(this)
                .setTarget(homeNavigationBar.)
                .setDismissText("Home")
                .setContentText("Here you can view Your followers Creavas and interact with them")
                .singleUse("0") // provide a unique ID used to ensure it is only shown once
                .show();*/


        Target viewTarget = new ViewTarget(homeNavigationBar.getId(), this);

      showcaseView = new ShowcaseView.Builder(this)
                .setTarget(viewTarget)
                .setContentTitle("Navigation Bar")
                .setContentText(" Here you can Navigation through the app, Press \'OK\' to continue the tutorial")
                .setOnClickListener(v -> {
                  Intent intent = new Intent(getApplicationContext(), CreavasActivity.class);
showcaseView.hide();
                  startActivity(intent);
              })
              .singleShot(55)
              .build();


        homeNavigationBar.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent intent = new Intent(getApplicationContext(), UserCreavasActivity.class);
                startActivity(intent);

            }


            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {

                    case 0:
                        replaceFragment(new HomeFragment());
                        break;

                    case 1:
                        replaceFragment(new ExploreFragment());
                          break;
                    case 2:
                        replaceFragment(new NotificationsFragment());
                        break;
                    case 3:
                        replaceFragment(new MyProfileFragment());
                        break;

                }

            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                switch (itemIndex) {

                    case 0:
                        replaceFragment(new HomeFragment());
                        break;
                    case 1:
                        replaceFragment(new ExploreFragment());
                        break;
                    case 2:
                        replaceFragment(new NotificationsFragment());
                        break;
                    case 3:
                        replaceFragment(new MyProfileFragment());
                        break;

                }
            }


        });

//
        initImageLoader();
        tokenization();
        replaceFragment(new HomeFragment());
        setupPermissionHelper();
        askPermission();
    }

    public void tokenization() {


        pref = getApplication().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER, 0);
        regId = pref.getString("regId", null);
        Log.v("FireBase Reg ID",regId);
        mAuth = FirebaseAuth.getInstance();
        Log.w("REG",regId);
        SignClient updateToken = ServiceFactory.getApiClient().create(SignClient.class);
        JsonObject token = new JsonObject();
        token.addProperty("idUser", mAuth.getCurrentUser().getUid());
        token.addProperty("token", regId);
        Call<Result> call = updateToken.updateToken(token);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (Integer.parseInt(response.body().getResult())>0){
                homeNavigationBar.showBadgeAtIndex(2, Integer.parseInt(response.body().getResult()), ContextCompat.getColor(MainHomeActivity.this, R.color.badge_background_color));
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    public void replaceFragment(android.app.Fragment fragment) {
       getFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        homeNavigationBar.onSaveInstanceState(outState);
    }

    private PermissionHelper permissionHelper;



    private void askPermission() {
        permissionHelper.check(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withDialogBeforeRun(R.string.dialog_before_run_title, R.string.dialog_before_run_message, R.string.dialog_positive_button)
                .setDialogPositiveButtonColor(android.R.color.holo_orange_dark)
                .onSuccess(this::onSuccess)
                .onDenied(this::onDenied)
                .onNeverAskAgain(this::onNeverAskAgain)
                .run();
    }
    private final String TAG = "PermissionResult: ";

    private void onSuccess() {

    }


    private void onNeverAskAgain() {
        permissionHelper.startApplicationSettingsActivity();
    }

    private void onDenied() {
        askPermission();

    }

    private void setupPermissionHelper() {
        permissionHelper = new PermissionHelper(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
