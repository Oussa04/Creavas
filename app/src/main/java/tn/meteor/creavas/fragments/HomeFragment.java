package tn.meteor.creavas.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.meteor.creavas.R;
import tn.meteor.creavas.adapters.HomeAdapter;
import tn.meteor.creavas.models.Post;
import tn.meteor.creavas.services.HomePosts;
import tn.meteor.creavas.services.ServiceFactory;


public class HomeFragment extends Fragment implements HomeAdapter.HomeAdapterListener {
    Unbinder unbinder;
    private RecyclerView postRecyclerView;
    private HomeAdapter adapter;
    private List<Post> postList;


    private FirebaseAuth mAuth;




    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        mAuth = FirebaseAuth.getInstance();


    }

private SharedPreferences sharedPreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, v);

        postRecyclerView = (RecyclerView) v.findViewById(R.id.post_recycler_view);

        postList = new ArrayList<>();
        adapter = new HomeAdapter(getActivity(), this, postList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        postRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        postRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(4), true));
        postRecyclerView.setItemAnimator(new DefaultItemAnimator());
        postRecyclerView.setAdapter(adapter);
        postRecyclerView.setNestedScrollingEnabled(false);

        sharedPreferences = getActivity().getSharedPreferences("listCache", Context.MODE_PRIVATE);

        Gson gson = new Gson();

        if (!sharedPreferences.getString("postList", "").equals("")) {
            String greeting = sharedPreferences.getString("postList", "");
            Type listOfTestObject = new TypeToken<List<Post>>() {
            }.getType();
            postList.clear();
            postList.addAll(gson.fromJson(greeting, listOfTestObject));
            adapter.notifyDataSetChanged();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("postList"); // will delete key email
            editor.apply();
            editor.clear();
        }
        preparePosts();



        return v;

    }

    SharedPreferences preferences;

    private void preparePosts() {
        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        HomePosts homePosts = ServiceFactory.getApiClient().create(HomePosts.class);
        JsonObject posts = new JsonObject();
        posts.addProperty("idUser", mAuth.getUid());

        Call<List<Post>> call = homePosts.getHomePosts(posts);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {


                if (response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body());
                    adapter.notifyDataSetChanged();
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
        if(postList.size()>0){
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(postList);
        editor.putString("postList", json); // Storing hashtag
        editor.apply();
        }
        unbinder.unbind();
    }

    @Override
    public void onPostSelected(Post post) {
        Gson gson = new Gson();
        String json = gson.toJson(post);
        Bundle bundle = new Bundle();
        bundle.putString("postSelected", json);


        PostFragment postFragment = new PostFragment();
        postFragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, postFragment).addToBackStack(null).commit();

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}