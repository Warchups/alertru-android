package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.BuildFragmentPagerAdapter;

public class BuildFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_build, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewPagerBuild);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayoutBuild);

        initFragments();

        initTabLayout();

        return view;
    }

    private void initFragments() {
        viewPager.setAdapter(new BuildFragmentPagerAdapter(getChildFragmentManager(), getContext()));
    }

    private void initTabLayout() {
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);

    }

}
