package com.gnommostudios.alertru.alertru_android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gnommostudios.alertru.alertru_android.view.fragments.InformationFragment;
import com.gnommostudios.alertru.alertru_android.view.fragments.LegendFragment;

public class MoreFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Info", "Leyenda"};

    public MoreFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;

        switch (position) {
            case 0:
                f = new InformationFragment();
                break;
            case 1:
                f = new LegendFragment();
                break;
        }

        return f;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}