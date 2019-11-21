package tn.meteor.creavas.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.meteor.creavas.R;
import tn.meteor.creavas.activities.MainHomeActivity;
import tn.meteor.creavas.adapters.ProfilesAdapter;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.services.ProfilesClient;
import tn.meteor.creavas.services.ServiceFactory;
import tn.meteor.creavas.utils.MyDividerItemDecoration;


public class PeopleFragment extends Fragment implements ProfilesAdapter.ContactsAdapterListener {
    Unbinder unbinder;
    List<String> mAllValues;
    private LinearLayout searchusers;
    private LinearLayout nousers;

    View v;

    private FloatingSearchView search;
    private FirebaseAuth mAuth;
    private SharedPreferences preferences;

    public static PeopleFragment newInstance() {
        PeopleFragment fragment = new PeopleFragment();
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
    private List<User> usersList;
    private ProfilesAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.fragment_people, container, false);
        unbinder = ButterKnife.bind(this, v);

        searchusers = (LinearLayout) v.findViewById(R.id.searchusers);
        nousers = (LinearLayout) v.findViewById(R.id.nousers);
        searchusers.setVisibility(View.VISIBLE);
        nousers.setVisibility(View.GONE);

        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        //  ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("People");
        recyclerView = v.findViewById(R.id.recycler_view);

        usersList = new ArrayList<User>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
        mAdapter = new ProfilesAdapter(getActivity(), usersList, this);
        recyclerView.setAdapter(mAdapter);

        search = (FloatingSearchView) v.findViewById(R.id.search);
        search.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                getUserFromDB(currentQuery);

            }
        });
        return v;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // unbind the view to free some memory
        unbinder.unbind();
    }


    protected void getUserFromDB(String word) {

        ProfilesClient client = ServiceFactory.getApiClient().create(ProfilesClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("word", word);
        parameters.addProperty("idUser", mAuth.getUid());
        Call<List<User>> call = client.searchUsers(parameters);
        Toast.makeText(getActivity(), "Gettting User", Toast.LENGTH_SHORT).show();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                mAdapter.notifyDataSetChanged();

                if (response.body() != null) {

                    if (response.body().isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        searchusers.setVisibility(View.GONE);
                        nousers.setVisibility(View.VISIBLE);
                    } else {
                        usersList.clear();
                        usersList.addAll(response.body());
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        searchusers.setVisibility(View.GONE);
                        nousers.setVisibility(View.GONE);

                    }
                } else {
                    nousers.setVisibility(View.VISIBLE);


                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onUserSelected(User user) {
        if (user.getFirebaseid().equals(mAuth.getUid())) {
            MyProfileFragment fragment = new MyProfileFragment();

//            getFragmentManager().beginTransaction()
//                    .replace(getParentFragment().getView().findViewById(R.id.childfragmentContainer).getId(), fragment, "userfragment")
//                    .commit();
            MainHomeActivity activity = (MainHomeActivity) getActivity();
            activity.homeNavigationBar.changeCurrentItem(3);
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(user);
            Bundle bundle = new Bundle();
            bundle.putString("userselected", json);
            OtherProfileFragment fragment = new OtherProfileFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(getParentFragment().getView().findViewById(R.id.childfragmentContainer).getId(), fragment, "userfragment")
                    .addToBackStack(null).commit();
        }
    }


}