package com.example.customviewdemo.indicator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.customviewdemo.R;

public class AaFragmentDemo extends Fragment {

    public static AaFragmentDemo instance;

    public static AaFragmentDemo getInstance(String title) {
        instance = new AaFragmentDemo();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.test_fragment_demo, null);
        Bundle bundle = getArguments();
        ((TextView) root.findViewById(R.id.text)).setText(bundle.getString("title"));
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.time_zone_base_search_menu, menu);
//
//        final MenuItem searchMenuItem = menu.findItem(R.id.time_zone_search_menu);
//        SsSearchView mSearchView = (SsSearchView) searchMenuItem.getActionView();
//
//        mSearchView.setQueryHint("123");
//
//        searchMenuItem.expandActionView();
//        mSearchView.setIconified(false);
//        mSearchView.setActivated(true);
//        mSearchView.setQuery("", true /* submit */);
    }
}
