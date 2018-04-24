package com.gnommostudios.alertru.alertru_android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gnommostudios.alertru.alertru_android.view.fragments.ConfigFragment;
import com.gnommostudios.alertru.alertru_android.view.fragments.DataUserFragment;
import com.gnommostudios.alertru.alertru_android.view.fragments.InformationFragment;
import com.gnommostudios.alertru.alertru_android.view.fragments.AlertListFragment;
import com.gnommostudios.alertru.alertru_android.view.fragments.SearchFragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 5;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;

        switch (position) {
            case 0:
                f = new SearchFragment();
                break;
            case 1:
                f = new DataUserFragment();
                break;
            case 2:
                f = new AlertListFragment();
                break;
            case 3:
                f = new ConfigFragment();
                break;
            case 4:
                f = new InformationFragment();
                break;
        }

        return f;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
