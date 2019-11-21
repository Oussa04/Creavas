package tn.meteor.creavas.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.meteor.creavas.R;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.services.ServiceFactory;
import tn.meteor.creavas.services.SignClient;
import tn.meteor.creavas.services.UploadClient;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.utils.Common;
import tn.meteor.creavas.utils.Constants;

public class UserRegistrationActivity extends AppCompatActivity {


    @BindView(R.id.photo)
    Button photo;
    @BindView(R.id.profilepic)
    CircleImageView profilepic;
    @BindView(R.id.name)
    MaterialEditText name;
    @BindView(R.id.username)
    MaterialEditText username;
    @BindView(R.id.bio)
    MaterialEditText bio;
    @BindView(R.id.website)
    MaterialEditText website;
    @BindView(R.id.finish)
    Button finish;
    private FirebaseAuth mAuth;
    private Boolean imageChanged = false;
    private Boolean hasImage = false;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        ButterKnife.bind(this);
     //   getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(UserRegistrationActivity.this);
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String a = name.getText().toString().replaceAll("\\s", "").toLowerCase();
                username.setText(a);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(name.getText().toString())) {
                    name.setError("Invalid name");
                } else if (TextUtils.isEmpty(username.getText().toString())) {
                    username.setError("Invalid username");
                } else if (TextUtils.isEmpty(bio.getText().toString())) {
                    bio.setError("Nothing? :(");
                } else if (!URLUtil.isValidUrl(website.getText().toString()) && website.getText().equals("")) {
                    website.setError("Invalid URL");
                } else {
                    showLoading();

                    if (imageChanged) {
                        addUserToDB(new User(name.getText().toString(),
                                username.getText().toString(),
                                mAuth.getCurrentUser().getEmail(),
                                mAuth.getCurrentUser().getUid(),
                                bio.getText().toString(),
                                "user_" + mAuth.getUid() + "_pic.jpg",
                                website.getText().toString()));
                        try {
                            uploadImage(resultUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        if (hasImage) {
                            addUserToDB(new User(name.getText().toString(),
                                    username.getText().toString(),
                                    mAuth.getCurrentUser().getEmail(),
                                    mAuth.getCurrentUser().getUid(),
                                    bio.getText().toString(),
                                    mAuth.getCurrentUser().getPhotoUrl().toString(),
                                    website.getText().toString()));
                        } else {
                            addUserToDB(new User(name.getText().toString(),
                                    username.getText().toString(),
                                    mAuth.getCurrentUser().getEmail(),
                                    mAuth.getCurrentUser().getUid(),
                                    bio.getText().toString(),
                                    Constants.PLACEHOLDER_IMAGE,
                                    website.getText().toString()));
                        }

                    }
                }


            }
        });

        Picasso.with(this)
                .load(Constants.PLACEHOLDER_IMAGE)
                .into(profilepic);

        if (mAuth.getCurrentUser().getDisplayName() != null && mAuth.getCurrentUser().getPhotoUrl() != null) {
            name.setText(mAuth.getCurrentUser().getDisplayName().toString());
            Picasso.with(this)
                    .load(mAuth.getCurrentUser().getPhotoUrl().toString())
                    .into(profilepic);
            hasImage = true;
        } else {
            hasImage = false;
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, currentUser.getEmail().toString(), Toast.LENGTH_SHORT).show();
            // User logged in
        }
    }

    private ProgressDialog mProgressDialog;


    public void showLoading() {
        hideLoading();

        mProgressDialog = Common.showLoadingDialog(this);
    }


    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    protected Uri resultUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    imageChanged = true;
                    Picasso.with(this)
                            .load(resultUri).into(profilepic);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


    protected void addUserToDB(User user) {

        SignClient client = ServiceFactory.getApiClient().create(SignClient.class);
        Call<User> call = client.addUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                editpreferences(response.body());
                Log.e("WEBSERVICE RESPONSE", response.toString());
                hideLoading();
                Intent sign = new Intent(UserRegistrationActivity.this, UserSignActivity.class);
                startActivity(sign);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                hideLoading();
                Log.e("WEBSERVICE ERROR", call.toString());
                Toast.makeText(UserRegistrationActivity.this, "Connection Failure", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void uploadImage(Uri imageUri) throws IOException {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading profile image");
        progressDialog.show();
        UploadClient client = ServiceFactory.getApiClient().create(UploadClient.class);
       // File file = new Compressor(this).compressToFile(new File(imageUri.getPath()));
        File file = new File(imageUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "user_" + mAuth.getUid() + "_pic.jpg", requestFile);
        Call<Result> resultCall = client.uploadProfileImgUser(body);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                progressDialog.dismiss();

                // Response Success or Fail
                if (response.isSuccessful()) {
                    if (response.body().getResult().equals("success"))
                        Toast.makeText(UserRegistrationActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(UserRegistrationActivity.this, "FAIL / SUCCESS", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(UserRegistrationActivity.this, "FAIL KHLAS", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
            }
        });


    }

    public void editpreferences(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        preferences.edit().putString("user", json).apply();
    }
}
