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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.meteor.creavas.R;
import tn.meteor.creavas.adapters.HomeAdapter;
import tn.meteor.creavas.models.Post;
import tn.meteor.creavas.services.HomePosts;
import tn.meteor.creavas.services.ServiceFactory;

/**
 * Created by lilk on 06/12/2017.
 */

public class HashtagFragment extends Fragment implements HomeAdapter.HomeAdapterListener {
    private FloatingSearchView search;
    View v;
    private FirebaseAuth mAuth;
    private SharedPreferences preferences;
    private RecyclerView postRecyclerView;
    private HomeAdapter adapter;
    private List<Post> postList;
    private TextView hashtagtext;
    SharedPreferences sharedpreferences;

    private LinearLayout searchInitial;
    private LinearLayout noposts;

    public static HashtagFragment newInstance() {
        HashtagFragment fragment = new HashtagFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_hashtag, container, false);

        postRecyclerView = (RecyclerView) v.findViewById(R.id.post_recycler_view);
        search = (FloatingSearchView) v.findViewById(R.id.search);
        searchInitial = (LinearLayout) v.findViewById(R.id.searchInitial);
        noposts = (LinearLayout) v.findViewById(R.id.noposts);
        hashtagtext = (TextView) v.findViewById(R.id.hashtagtext);
        postRecyclerView.setVisibility(View.GONE);
        searchInitial.setVisibility(View.VISIBLE);
        noposts.setVisibility(View.GONE);

        postList = new ArrayList<>();
        adapter = new HomeAdapter(getActivity(), this, postList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        postRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        postRecyclerView.addItemDecoration(new HashtagFragment.GridSpacingItemDecoration(2, dpToPx(4), true));
        postRecyclerView.setItemAnimator(new DefaultItemAnimator());
        postRecyclerView.setAdapter(adapter);
        postRecyclerView.setNestedScrollingEnabled(false);
        sharedpreferences = getActivity().getSharedPreferences("selectedHashtag", Context.MODE_PRIVATE);

        if (!sharedpreferences.getString("hashtag", "").equals("")) {
            String currentQuery = sharedpreferences.getString("hashtag", "");
            search.setSearchText(sharedpreferences.getString("hashtag", ""));
            currentQuery = currentQuery.replace("#", "");
            preparePosts(currentQuery);
            hashtagtext.setText("#" + currentQuery);


            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("hashtag"); // will delete key email
            editor.commit(); // commit changes
            editor.clear();
        }


        search.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                if (!currentQuery.equals("")) {
                    currentQuery = currentQuery.replace("#", "");
                    preparePosts(currentQuery);
                    hashtagtext.setText("#" + currentQuery);
                }
            }
        });
        return v;
    }


    private void preparePosts(String hashtag) {
        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        HomePosts homePosts = ServiceFactory.getApiClient().create(HomePosts.class);
        JsonObject posts = new JsonObject();
        posts.addProperty("hashtag", hashtag);

        Call<List<Post>> call = homePosts.getPostsByHashtag(posts);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.body() != null) {

                    if (response.body().size() > 0) {
                        postList.clear();
                        postList.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        postRecyclerView.setVisibility(View.VISIBLE);
                        searchInitial.setVisibility(View.GONE);
                        noposts.setVisibility(View.GONE);
                    } else {
                        postRecyclerView.setVisibility(View.GONE);
                        searchInitial.setVisibility(View.GONE);
                        noposts.setVisibility(View.VISIBLE);
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

    }

    @Override
    public void onPostSelected(Post post) {
        Gson gson = new Gson();
        String json = gson.toJson(post);
        Bundle bundle = new Bundle();
        bundle.putString("postSelected", json);


        PostFragment postFragment = new PostFragment();
        postFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(getParentFragment().getView().findViewById(R.id.childfragmentContainer).getId(), postFragment, "postFragment")
                .addToBackStack(null).commit();

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