package tn.meteor.creavas.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.meteor.creavas.R;
import tn.meteor.creavas.activities.MainHomeActivity;
import tn.meteor.creavas.adapters.NotificationsAdapter;
import tn.meteor.creavas.models.Notification;
import tn.meteor.creavas.models.Post;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.services.NotificationsClient;
import tn.meteor.creavas.services.PostsClient;
import tn.meteor.creavas.services.ServiceFactory;
import tn.meteor.creavas.services.SignClient;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.utils.MyDividerItemDecoration;


public class NotificationsFragment extends Fragment implements NotificationsAdapter.NotificationsAdapterListener {
    Unbinder unbinder;
    List<String> mAllValues;
    private LinearLayout nonotifications;
    private CircleButton clearall;
    View v;
    private Post post;
    private User u;

    private FirebaseAuth mAuth;
    private SharedPreferences preferences;

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        mAuth = FirebaseAuth.getInstance();

        setHasOptionsMenu(true);
    }

    private RecyclerView recyclerView;
    private List<Notification> notificationList;
    private NotificationsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.fragment_notifications, container, false);
        unbinder = ButterKnife.bind(this, v);

        nonotifications = (LinearLayout) v.findViewById(R.id.nonotifications);
        clearall = (CircleButton) v.findViewById(R.id.clearall);

        clearall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllNotifictions();
            }
        });
        nonotifications.setVisibility(View.VISIBLE);
        sharedPreferences = getActivity().getSharedPreferences("listCache", Context.MODE_PRIVATE);
        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        //  ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("People");
        recyclerView = v.findViewById(R.id.recycler_view);

        notificationList = new ArrayList<Notification>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
        mAdapter = new NotificationsAdapter(getActivity(), notificationList, this);
        recyclerView.setAdapter(mAdapter);


        Gson gson = new Gson();

        if (!sharedPreferences.getString("notificationsList", "").equals("")) {
            String greeting = sharedPreferences.getString("notificationsList", "");
            Type listOfTestObject = new TypeToken<List<Notification>>() {
            }.getType();
            notificationList.clear();
            notificationList.addAll(gson.fromJson(greeting, listOfTestObject));
            mAdapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            nonotifications.setVisibility(View.GONE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("notificationsList");
            editor.apply();
            editor.clear();
        }
        getNotificationsFromDb();

        return v;

    }

    private SharedPreferences sharedPreferences;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
if (notificationList.size()>0){
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(notificationList);
        editor.putString("notificationsList", json); // Storing hashtag
        editor.apply();
}
        unbinder.unbind();
    }


    protected void getNotificationsFromDb() {

        NotificationsClient client = ServiceFactory.getApiClient().create(NotificationsClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("idUser", mAuth.getUid());
        Call<List<Notification>> call = client.getAllNotifications(parameters);
        Toast.makeText(getActivity(), "Getting Notifications...", Toast.LENGTH_SHORT).show();


        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                mAdapter.notifyDataSetChanged();


                if (response.body() != null) {

                    if (response.body().isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        nonotifications.setVisibility(View.VISIBLE);
                    } else {
                        notificationList.clear();
                        notificationList.addAll(response.body());
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        nonotifications.setVisibility(View.GONE);
                        MainHomeActivity mainHomeActivity = (MainHomeActivity) getActivity();
                        mainHomeActivity.homeNavigationBar.changeBadgeTextAtIndex(2, notificationList.size());
                    }
                } else {
                    nonotifications.setVisibility(View.VISIBLE);


                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {

            }
        });

    }

    @Override
    public void onNotificationSelected(Notification notification) {

        if (notification.getNotification_type().equals("like") || notification.getNotification_type().equals("comment")) {

            getPostById(notification.getIdPost());
            Toast.makeText(getActivity(), "--> " + notification.getIdPost(), Toast.LENGTH_SHORT).show();

        } else if (notification.getNotification_type().equals("follow")) {

            getUserFromNotif(notification.getIdUser());
        }

    }

    @Override
    public void onNotificationLongClick(Notification notification) {
        showNotificationLongClickDialog(notification);
    }



    public void showNotificationLongClickDialog(Notification notification) {
        new MaterialDialog.Builder(getActivity())
                .items(R.array.notificationLongClick)
                .itemsCallback((dialog, view1, which, text) -> {
                    if (which == 0) {

                        deleteNotification(notification);

                    }

                })
                .show();
    }

    public void deleteNotification(Notification notification) {

        NotificationsClient client = ServiceFactory.getApiClient().create(NotificationsClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("id", notification.getId());
        Call<Result> call = client.deleteNotif(parameters);


        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                Toast.makeText(getActivity(), "Notification Deleted", Toast.LENGTH_SHORT).show();

                notificationList.remove(notificationList.indexOf(notification));
                mAdapter.notifyDataSetChanged();
                MainHomeActivity mainHomeActivity = (MainHomeActivity) getActivity();
                mainHomeActivity.homeNavigationBar.changeBadgeTextAtIndex(2, notificationList.size());

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }


    public void deleteAllNotifictions() {

        NotificationsClient client = ServiceFactory.getApiClient().create(NotificationsClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("id", mAuth.getUid());
        Call<Result> call = client.deleteAllNotif(parameters);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                notificationList.clear();
                mAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.GONE);
                nonotifications.setVisibility(View.VISIBLE);
                MainHomeActivity mainHomeActivity = (MainHomeActivity) getActivity();
                mainHomeActivity.homeNavigationBar.changeBadgeTextAtIndex(2, notificationList.size());

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });


    }

    public void getPostById(int idPost) {

        post = new Post();
        PostsClient client = ServiceFactory.getApiClient().create(PostsClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("idPost", idPost);
        Call<Post> call = client.getPostById(parameters);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                post = response.body();
                Log.w("POSTID", "" + post.getId());

                Toast.makeText(getActivity(), response.body().toString(), Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                String json = gson.toJson(post);
                Bundle bundle = new Bundle();
                bundle.putString("postSelected", json);


                PostFragment postFragment = new PostFragment();
                postFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, postFragment).addToBackStack(null).commit();


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });

    }


    public void getUserFromNotif(String idUser) {
        u = new User();
        SignClient client = ServiceFactory.getApiClient().create(SignClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("firebaseid", idUser);
        Call<User> call = client.getUser(parameters);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                u = response.body();
                Log.w("USERID", "" + u.getFirebaseid());

                Gson gson = new Gson();
                String json = gson.toJson(u);
                Bundle bundle = new Bundle();
                bundle.putString("userselected", json);

                OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
                otherProfileFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, otherProfileFragment).addToBackStack(null).commit();


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}