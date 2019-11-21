package tn.meteor.creavas.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import tn.meteor.creavas.R;
import tn.meteor.creavas.activities.FollowshipActivity;
import tn.meteor.creavas.activities.UserSignActivity;
import tn.meteor.creavas.adapters.GridImageAdapter;
import tn.meteor.creavas.models.Post;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.services.HomePosts;
import tn.meteor.creavas.services.PostsClient;
import tn.meteor.creavas.services.ServiceFactory;
import tn.meteor.creavas.services.SignClient;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.utils.Constants;


public class MyProfileFragment extends Fragment implements GridImageAdapter.GridAdapterListener, View.OnClickListener {
    Unbinder unbinder;
    @BindView(R.id.profilepic)
    CircleImageView profilepic;
    @BindView(R.id.name)
    TextView nameview;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.description)
    TextView descriptionview;
    @BindView(R.id.creavas)
    TextView creavasnum;
    @BindView(R.id.followers)
    TextView followers;
    @BindView(R.id.following)
    TextView following;
    @BindView(R.id.url)
    TextView url;
    @BindView(R.id.followship)
    Button followship;
    @BindView(R.id.settings)
    CircleButton settings;
    ProgressBar progress;
    TextView noposts;
    RecyclerView postRecyclerView;

    View v;
    private FirebaseAuth mAuth;
    private SharedPreferences preferences;
    private User currentUser;
    private List<Post> postList;
    private GridImageAdapter adapter;

    public static MyProfileFragment newInstance() {
        MyProfileFragment fragment = new MyProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        mAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_action_bar_userprofile, menu);
    }

    private SharedPreferences postPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, v);
        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        //  ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(preferences.getString("name", ""));
        String userpref = preferences.getString("user", "");
        Gson gson = new Gson();
        //JSONObject jsonObj = new JSONObject(preferences.getString("user", null));
        currentUser = gson.fromJson(userpref, User.class);
        Log.e("--->", userpref.toString());
        postRecyclerView = (RecyclerView) v.findViewById(R.id.posts);
        noposts = (TextView) v.findViewById(R.id.noposts);
        progress = (ProgressBar) v.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        noposts.setVisibility(View.GONE);
        postRecyclerView.setVisibility(View.GONE);

        postList = new ArrayList<>();
        adapter = new GridImageAdapter(getActivity(), this, postList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        postRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        postRecyclerView.setAdapter(adapter);
        postRecyclerView.setNestedScrollingEnabled(false);


        postPreferences = getActivity().getSharedPreferences("listCache", Context.MODE_PRIVATE);
        if (!postPreferences.getString("profilePost", "").equals("")) {
            Log.e("PPPPPPPPPPP", postPreferences.getString("profilePost", ""));
            String greeting = postPreferences.getString("profilePost", "");
            Type listOfTestObject = new TypeToken<List<Post>>() {
            }.getType();
            progress.setVisibility(View.GONE);
            noposts.setVisibility(View.GONE);
            postRecyclerView.setVisibility(View.VISIBLE);
            postList.clear();
            postList.addAll(gson.fromJson(greeting, listOfTestObject));
            adapter.notifyDataSetChanged();
            SharedPreferences.Editor editor = postPreferences.edit();
            editor.remove("profilePost"); // will delete key email
            editor.apply();
            editor.clear();
        }
        tempGridSetup();
        userInfoSetup();
        reloadUserData();
        //SharedPreferences pref = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER, 0);
        //Toast.makeText(getActivity(), ""+pref.getString("regId", "null"), Toast.LENGTH_SHORT).show();
        return v;

    }

    private void userInfoSetup() {


        nameview.setText(currentUser.getName());
        username.setText(currentUser.getUsername());
        descriptionview.setText(currentUser.getDescription());
        url = (TextView) v.findViewById(R.id.url);
        creavasnum.setText(currentUser.getPosts() + "");
        followers.setText(currentUser.getHowManyUsersFollowingHim() + "");
        following.setText(currentUser.getHowManyUsersIsHeFollowing() + "");
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowshipActivity.class);
                intent.putExtra("type", "followers");

                startActivity(intent);
            }
        });
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowshipActivity.class);
                intent.putExtra("type", "followed");

                startActivity(intent);
            }
        });
        if (currentUser.getSocial_link() != "") {
            url.setText(currentUser.getSocial_link());
        } else {
            url.setVisibility(View.GONE);
        }


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingsDialog();

            }
        });


        followship.setVisibility(View.GONE);

        Picasso.with(getActivity()).setLoggingEnabled(true);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().header("Cache-Control", "max-age=" + (60 * 60 * 24 * 365)).build();
            }
        });
        okHttpClient.setCache(new Cache(getActivity().getCacheDir(), Integer.MAX_VALUE));
        OkHttpDownloader okHttpDownloader = new OkHttpDownloader(okHttpClient);
        Picasso picasso = new Picasso.Builder(getActivity()).downloader(okHttpDownloader).build();


        if (currentUser.getProfile_pic_name().contains("https://") || currentUser.getProfile_pic_name().contains("http://")) {
            picasso.load(currentUser.getProfile_pic_name()).into(profilepic);
            //image taken from google+

        } else {
            picasso.load(Constants.HTTP.IMAGE_URL + "users/" + currentUser.getProfile_pic_name()).into(profilepic);
        }

    }


    public void showSettingsDialog() {
        new MaterialDialog.Builder(getActivity())
                .title("Settings")
                .items(R.array.userSettings)
                .itemsCallback((dialog, view1, which, text) -> {
                    if (which == 0      ) {
                        deleteToken();
                        Intent backToLogin = new Intent(getActivity(), UserSignActivity.class); // Your list's Intent
                        backToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(backToLogin);
                        preferences.edit().clear();
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        getActivity().finish();


                    }

                })
                .show();
    }

    private void tempGridSetup() {
        ArrayList<String> userposts = new ArrayList<>();
        HomePosts homePosts = ServiceFactory.getApiClient().create(HomePosts.class);
        JsonObject posts = new JsonObject();
        posts.addProperty("idUser", mAuth.getUid());
        Call<List<Post>> call = homePosts.getUserPost(posts);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, retrofit2.Response<List<Post>> response) {


                if (response.body() != null) {
                    if (response.body().size() > 0) {
                        postList.clear();
                        postList.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        postRecyclerView.setVisibility(View.VISIBLE);
                        noposts.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                    } else {
                        postRecyclerView.setVisibility(View.GONE);
                        noposts.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);


                    }
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (postList.size() > 0) {
            Gson gson = new Gson();
            SharedPreferences.Editor editor = postPreferences.edit();
            String json = gson.toJson(postList);
            editor.putString("profilePost", json); // Storing hashtag
            editor.apply();
        }
        unbinder.unbind();
    }


    protected void reloadUserData() {

        SignClient client = ServiceFactory.getApiClient().create(SignClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("firebaseid", mAuth.getUid());
        Call<User> call = client.getUser(parameters);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {

                if (!(response.body().equals(currentUser))) {
                    currentUser = response.body();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    preferences.edit().putString("user", json).apply();
                    currentUser = response.body();
                    userInfoSetup();
                }


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }


    @Override
    public void onPostSelected(Post post) {
        post.setUserImage(currentUser.getProfile_pic_name());
        post.setUsername(currentUser.getUsername());
        Gson gson = new Gson();
        String json = gson.toJson(post);
        Bundle bundle = new Bundle();
        bundle.putString("postSelected", json);
        PostFragment postFragment = new PostFragment();
        postFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, postFragment).commit();

    }

    @Override
    public void onPostLongClickSelected(Post post) {
        showNotificationLongClickDialog(post);
    }

    public void showNotificationLongClickDialog(Post post) {
        new MaterialDialog.Builder(getActivity())
                .items(R.array.postLongClick)
                .itemsCallback((dialog, view1, which, text) -> {
                    if (which == 0) {

                        deleteNotification(post);

                    }

                })
                .show();
    }

    public void deleteNotification(Post post) {

        PostsClient client = ServiceFactory.getApiClient().create(PostsClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("id", post.getId());
        Call<Result> call = client.deletePostById(parameters);


        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {


                postList.remove(postList.indexOf(post));
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    public void deleteToken() {
        SignClient signClient = ServiceFactory.getApiClient().create(SignClient.class);
        JsonObject tokenDelete = new JsonObject();
        tokenDelete.addProperty("idUser", mAuth.getCurrentUser().getUid());
        tokenDelete.addProperty("token", "");
        Call<Result> call = signClient.deleteToken(tokenDelete);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    ShowcaseView showcaseView;
    int counter=0;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        showcaseView = new ShowcaseView.Builder(getActivity())
                .setContentTitle("Your profile")
                .setContentText("Here you can find different Informations ")
                .setOnClickListener(this)
                .withMaterialShowcase()
                .singleShot(57)
                .build();
        showcaseView.setButtonText("Next");
        showcaseView.setTarget(Target.NONE);

    }

    @Override
    public void onClick(View v) {

        switch (counter) {
            case 0:
                showcaseView.setShowcase(new ViewTarget(followers), true);
                showcaseView.setContentTitle("Followers");
                showcaseView.setContentText("You can view Your list of followers");
                break;

            case 1:
                showcaseView.setShowcase(new ViewTarget(following), true);
                showcaseView.setContentTitle("Following");
                showcaseView.setContentText("Or your list of followed users");
                break;

            case 2:

                showcaseView.setShowcase(new ViewTarget(profilepic), true);
                showcaseView.setContentTitle("Have fun using the App");
                showcaseView.setTarget(Target.NONE);
                showcaseView.setButtonText("Close");
                break;

            case 3:
                showcaseView.hide();;
                break;
        }
        counter++;
    }

}