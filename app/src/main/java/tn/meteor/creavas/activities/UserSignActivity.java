package tn.meteor.creavas.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;
import com.xwray.passwordview.PasswordView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.meteor.creavas.R;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.services.ServiceFactory;
import tn.meteor.creavas.services.SignClient;
import tn.meteor.creavas.utils.Common;


public class UserSignActivity extends AppCompatActivity {

    @BindView(R.id.emailLogin)
    ImageButton emailLogin;
    @BindView(R.id.googleLogin)
    ImageButton googleLogin;
    @BindView(R.id.googleButton)
    SignInButton googleButton;

    @BindView(R.id.facebookLogin)
    ImageButton facebookLogin;

    private PasswordView passwordInput;
    private EditText emailInput;
    private Button signin;
    private Button signup;
    private Button forget;
    private Button reset;
    private FirebaseAuth mAuth;
    private SharedPreferences preferences;


    //google stuff
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //TODO FACEBOOK FUCKIN LOGIIN
            }
        });
        mAuth = FirebaseAuth.getInstance();
        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSigninDialog();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                showToast("ErrorGoogle");
            }
        })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            showLoading();
            getUserFromDB();
        }
    }

    public void editpreferences(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        preferences.edit().putString("user", json).apply();
    }


    MaterialDialog signinDialog;
    MaterialDialog signUpDialog;
    MaterialDialog forgetPasswordDialog;

    public void showSigninDialog() {
        signinDialog =
                new MaterialDialog.Builder(this)
                        .title("Sign in to Creavas")
                        .customView(R.layout.layout_signin_cutom, true)
                        .build();
        passwordInput = signinDialog.getCustomView().findViewById(R.id.password);
        emailInput = signinDialog.getCustomView().findViewById(R.id.email);
        signin = signinDialog.getCustomView().findViewById(R.id.signin);
        signup = signinDialog.getCustomView().findViewById(R.id.signup);
        forget = signinDialog.getCustomView().findViewById(R.id.forget);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
                showToast("Logging!");
                signIn(emailInput.getText().toString(), passwordInput.getText().toString(), true);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinDialog.hide();
                showSignupDialog();
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinDialog.hide();
                showForgetPasswordDialog();
            }
        });
        int widgetColor = ThemeSingleton.get().widgetColor;

        signinDialog.show();

    }

    private void signIn(String email, String password, boolean userRegistred) {
        email = email.trim();
        password = password.trim();

        if (TextUtils.isEmpty(email)) {
            hideLoading();
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();

            return;
        }

        if (TextUtils.isEmpty(password)) {
            hideLoading();
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        Alerter a = Alerter.create(this)
                .setTitle("Sign In Failed")
                .setText("Please check your credentials then try again.")
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.red) // or setBackgroundColorInt(Color.CYAN)
                ;
        showToast("User Registred?" + userRegistred);
        mAuth.removeAuthStateListener(mAuthListener);
        mAuthListener = null;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            if (!userRegistred) {
                                Intent intent = new Intent(UserSignActivity.this, UserRegistrationActivity.class);
                                startActivity(intent);
                            } else {
                                getUserFromDB();

                                FirebaseUser user = mAuth.getCurrentUser();

                            }
                            Log.d("SIGN IN", "signInWithEmail:success");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGN IN", "signInWithEmail:failure", task.getException());
                            hideLoading();
                            signinDialog.hide();
                            a.show();

                        }
                    }
                });
        // [END sign_in_with_email]

    }

    //    END SIGN IN STUFF
    public void showSignupDialog() {
        signUpDialog =
                new MaterialDialog.Builder(this)
                        .title("Create a Creavas profile")
                        .customView(R.layout.layout_signup_custom, true)
                        .build();

        //noinspection ConstantConditions
        passwordInput = signUpDialog.getCustomView().findViewById(R.id.signuppassword);
        emailInput = signUpDialog.getCustomView().findViewById(R.id.signupemail);
        signup = signUpDialog.getCustomView().findViewById(R.id.signupbutton);
        signin = signUpDialog.getCustomView().findViewById(R.id.signinbutton);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpDialog.hide();
                showSigninDialog();

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
                createAccount(emailInput.getText().toString(), passwordInput.getText().toString());

            }
        });
        int widgetColor = ThemeSingleton.get().widgetColor;
        signUpDialog.show();

    }

    private void createAccount(String email, String password) {
        email = email.trim();
        password = password.trim();
        if (TextUtils.isEmpty(email)) {
            hideLoading();
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            hideLoading();
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            hideLoading();
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(UserSignActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            hideLoading();
                            FirebaseUser user = mAuth.getCurrentUser();
                            signIn(emailInput.getText().toString(), passwordInput.getText().toString(), false);
                        } else {
                            showToast("fail");
                            hideLoading();
                        }
                    }
                });
    }

    public void showForgetPasswordDialog() {
        forgetPasswordDialog =
                new MaterialDialog.Builder(this)
                        .title("Reset my password")
                        .customView(R.layout.layout_signin_forget_password, true)
                        .build();

        //noinspection ConstantConditions
        emailInput = forgetPasswordDialog.getCustomView().findViewById(R.id.emailforget);
        reset = forgetPasswordDialog.getCustomView().findViewById(R.id.resetforget);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
                resetPassword(emailInput.getText().toString());

            }
        });


        int widgetColor = ThemeSingleton.get().widgetColor;

        forgetPasswordDialog.show();

    }

    public void resetPassword(String email) {
        email = email.trim();

        if (TextUtils.isEmpty(email)) {
            hideLoading();
            Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            hideLoading();
                            showToast("we're good");
                        } else {
                            hideLoading();
                            showToast("failed");
                        }
                    }
                });
    }

    public void SignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        showLoading();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private Toast toast;

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                showToast("google failded");                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getUserFromDB();
                        } else {
                            showToast("Failed");
                        }


                    }
                });
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
                    hideLoading();
                    Intent intent = new Intent(UserSignActivity.this, UserRegistrationActivity.class);
                    startActivity(intent);
                } else { //user registred in server
                    editpreferences(response.body());
                    Intent intent = new Intent(UserSignActivity.this, MainHomeActivity.class);
                    hideLoading();
                    startActivity(intent);
                    finish();
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }




}
