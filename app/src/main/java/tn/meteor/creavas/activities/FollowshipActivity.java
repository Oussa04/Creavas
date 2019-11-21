package tn.meteor.creavas.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.meteor.creavas.R;
import tn.meteor.creavas.adapters.ProfilesAdapter;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.services.ProfilesClient;
import tn.meteor.creavas.services.ServiceFactory;
import tn.meteor.creavas.utils.MyDividerItemDecoration;

public class FollowshipActivity extends AppCompatActivity implements ProfilesAdapter.ContactsAdapterListener {
    private RecyclerView recyclerView;
    private List<User> usersList;
    private ProfilesAdapter mAdapter;
    private FirebaseAuth mAuth;
    private TextView title;
    private LinearLayout nopersons;
    private CircleButton close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followship);
        String type = getIntent().getStringExtra("type");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        title = (TextView) findViewById(R.id.pagetitle);
        nopersons = (LinearLayout) findViewById(R.id.nopersons);
        close = (CircleButton) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        nopersons.setVisibility(View.GONE);
        usersList = new ArrayList<User>();
        mAuth = FirebaseAuth.getInstance();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 0));
        mAdapter = new ProfilesAdapter(this, usersList, this);
        recyclerView.setAdapter(mAdapter);
        if (type.equals("followers")) {
            getFollowers();
            title.setText("Followers");
        } else if (type.equals("followed")) {
            getFollowed();
            title.setText("Following");

        }


    }

    @Override
    public void onUserSelected(User user) {

    }


    protected void getFollowers() {

        ProfilesClient client = ServiceFactory.getApiClient().create(ProfilesClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("idUser", mAuth.getUid());
        Call<List<User>> call = client.getFollowers(parameters);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                mAdapter.notifyDataSetChanged();

                if (response.body() != null) {

                    if (response.body().isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        nopersons.setVisibility(View.VISIBLE);

                    } else {
                        usersList.clear();
                        usersList.addAll(response.body());
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        nopersons.setVisibility(View.GONE);

                    }
                } else {


                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
            }
        });

    }


    protected void getFollowed() {

        ProfilesClient client = ServiceFactory.getApiClient().create(ProfilesClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("idUser", mAuth.getUid());
        Call<List<User>> call = client.getFollowed(parameters);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                mAdapter.notifyDataSetChanged();

                if (response.body() != null) {

                    if (response.body().isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        usersList.clear();
                        usersList.addAll(response.body());
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);

                    }
                } else {


                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
            }
        });

    }
}
