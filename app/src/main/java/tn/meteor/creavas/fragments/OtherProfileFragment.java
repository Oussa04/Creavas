package tn.meteor.creavas.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
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
import tn.meteor.creavas.adapters.GridImageAdapter;
import tn.meteor.creavas.models.Post;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.services.HomePosts;
import tn.meteor.creavas.services.ProfilesClient;
import tn.meteor.creavas.services.ServiceFactory;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.utils.Constants;


public class OtherProfileFragment extends Fragment implements GridImageAdapter.GridAdapterListener {
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
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.noposts)
    TextView noposts;
    @BindView(R.id.posts)
    RecyclerView postRecyclerView;
    @BindView(R.id.settings)
    CircleButton settings;
    View v;
    private FirebaseAuth mAuth;
    private SharedPreferences preferences;
    private User user;

    public static OtherProfileFragment newInstance() {
        OtherProfileFragment fragment = new OtherProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        mAuth = FirebaseAuth.getInstance();
        Bundle bundle = this.getArguments();
        String userselected = bundle.getString("userselected", "");
        Gson gson = new Gson();
        user = gson.fromJson(userselected, User.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, v);
        settings.setVisibility(View.GONE);
        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        //   ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(preferences.getString("name", ""));
        followers = (TextView) v.findViewById(R.id.followers);
        following = (TextView) v.findViewById(R.id.following);
        creavasnum = (TextView) v.findViewById(R.id.creavas);
        progress.setVisibility(View.VISIBLE);
        noposts.setVisibility(View.GONE);
        postRecyclerView.setVisibility(View.GONE);
        settings.setVisibility(View.GONE);
        postList = new ArrayList<>();
        adapter = new GridImageAdapter(getActivity(), this, postList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        postRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        postRecyclerView.setAdapter(adapter);
        postRecyclerView.setNestedScrollingEnabled(false);
        tempGridSetup();
        userInfoSetup();
        return v;

    }

    private void userInfoSetup() {
        nameview.setText(user.getName());
        username.setText(user.getUsername());
        descriptionview.setText(user.getDescription());
        url = (TextView) v.findViewById(R.id.url);
        creavasnum.setText(user.getPosts() + "");
        followers.setText(user.getHowManyUsersFollowingHim() + "");
        following.setText(user.getHowManyUsersIsHeFollowing() + "");
        if (user.getSocial_link() != "") {
            url.setText(user.getSocial_link());
        } else {
            url.setVisibility(View.GONE);
        }
        if (user.getFirebaseid().equals(mAuth.getUid())) {

            followship.setVisibility(View.GONE);
        }


        if (user.getIsFollowed().equals("true")) {
            followship.setText("- Unfollow");
        }

        followship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (user.getIsFollowed().equals("false")) {
                    followship.setClickable(false);

                    follow();

                } else {
                    followship.setClickable(false);
                    unfollow();

                }


            }


        });

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


        if (user.getProfile_pic_name().contains("https://") || user.getProfile_pic_name().contains("http://")) {
            picasso.load(user.getProfile_pic_name()).into(profilepic);
            //image taken from google+

        } else {
            picasso.load(Constants.HTTP.IMAGE_URL + "users/" + user.getProfile_pic_name()).into(profilepic);
        }


    }

    private void tempGridSetup() {
        ArrayList<String> userposts = new ArrayList<>();
        HomePosts homePosts = ServiceFactory.getApiClient().create(HomePosts.class);
        JsonObject posts = new JsonObject();
        posts.addProperty("idUser", user.getFirebaseid());
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

    private List<Post> postList;
    private GridImageAdapter adapter;


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // unbind the view to free some memory
        unbinder.unbind();
    }

    private void follow() {

        ProfilesClient client = ServiceFactory.getApiClient().create(ProfilesClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("user", mAuth.getUid());
        parameters.addProperty("userAdded", user.getFirebaseid());
        Call<Result> call = client.follow(parameters);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResult().equals("success"))
                        Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();

                    else
                        Toast.makeText(getActivity(), "FAIL / SUCCESS", Toast.LENGTH_SHORT).show();
                    followship.setText("- Unfollow");
                    user.setIsFollowed("true");
                    followship.setClickable(true);
                    user.setHowManyUsersFollowingHim(user.getHowManyUsersFollowingHim() + 1);
                    followers.setText(user.getHowManyUsersFollowingHim() + "");
                } else {
                    Toast.makeText(getActivity(), "FAIL KHLAS", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });

    }

    private void unfollow() {

        {

            ProfilesClient client = ServiceFactory.getApiClient().create(ProfilesClient.class);
            JsonObject parameters = new JsonObject();
            parameters.addProperty("user", mAuth.getUid());
            parameters.addProperty("userToDelete", user.getFirebaseid());
            Call<Result> call = client.unfollow(parameters);
            call.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getResult().equals("success"))
                            Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "FAIL / SUCCESS", Toast.LENGTH_SHORT).show();
                        followship.setText("+ follow");
                        user.setIsFollowed("false");
                        followship.setClickable(true);
                        user.setHowManyUsersFollowingHim(user.getHowManyUsersFollowingHim() - 1);
                        followers.setText(user.getHowManyUsersFollowingHim() + "");

                    } else {
                        Toast.makeText(getActivity(), "FAIL KHLAS", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {

                }
            });

        }
    }

    @Override
    public void onPostSelected(Post post) {
        post.setUserImage(user.getProfile_pic_name());
        post.setUsername(user.getUsername());
        Gson gson = new Gson();
        String json = gson.toJson(post);
        Bundle bundle = new Bundle();
        bundle.putString("postSelected", json);
        PostFragment postFragment = new PostFragment();
        postFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(getParentFragment().getView().findViewById(R.id.childfragmentContainer).getId(), postFragment, "userfragment")
                .addToBackStack(null).commit();

    }

    @Override
    public void onPostLongClickSelected(Post post) {

    }
//    protected void getUserFromDB() {
//

//        Toast.makeText(getActivity(), "Gettting User", Toast.LENGTH_SHORT).show();
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//
//                nameview.setText(response.body().getName().toString());
//                username.setText(response.body().getUsername().toString());
//                descriptionview.setText(response.body().getDescription().toString());
//                v.invalidate();
//
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Toast.makeText(getActivity(), "Connextion ERROR", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//    }
}