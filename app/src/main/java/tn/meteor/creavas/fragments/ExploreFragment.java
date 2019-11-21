package tn.meteor.creavas.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.List;

import butterknife.ButterKnife;
import tn.meteor.creavas.R;

public class ExploreFragment extends Fragment {

    List<String> mAllValues;
    private ArrayAdapter<String> mAdapter;
    private Context mContext;

    public BottomBar topbar;
    SharedPreferences sharedpreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
        ButterKnife.bind(getActivity());
    }

    private void initTopBar() {

        topbar = (BottomBar) layout.findViewById(R.id.topbar);
        sharedpreferences = getActivity().getSharedPreferences("selectedHashtag", Context.MODE_PRIVATE);
        if (!sharedpreferences.getString("hashtag", "").equals("")) {
            topbar.setDefaultTab(R.id.hashtag);
        } else {
            topbar.setDefaultTab(R.id.hot);
        }
        topbar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                if (tabId == R.id.hashtag) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.childfragmentContainer, new HashtagFragment(), "frag32")
                            .commit();
                } else if (tabId == R.id.people) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.childfragmentContainer, new PeopleFragment(), "frag32")
                            .commit();
                } else if (tabId == R.id.hot) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.childfragmentContainer, new HotFragment(), "frag32")
                            .commit();
                }
            }
        });
        topbar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(int tabId) {
                if (tabId == R.id.hashtag) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.childfragmentContainer, new HashtagFragment(), "frag32")
                            .commit();
                } else if (tabId == R.id.people) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.childfragmentContainer, new PeopleFragment(), "frag32")
                            .commit();
                } else if (tabId == R.id.hot) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.childfragmentContainer, new HotFragment(), "frag32")
                            .commit();
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_explore, container, false);
        ListView listView = (ListView) layout.findViewById(android.R.id.list);
        initTopBar();
        return layout;
    }



}