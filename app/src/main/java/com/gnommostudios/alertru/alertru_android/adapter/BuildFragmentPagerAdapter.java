package com.gnommostudios.alertru.alertru_android.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.view.fragments.ConfigFragment;
import com.gnommostudios.alertru.alertru_android.view.fragments.InformationFragment;

public class BuildFragmentPagerAdapter extends FragmentPagerAdapter {

    private int tabTitles[] = new int[] {R.string.settings, R.string.info};

    final int PAGE_COUNT = 2;

    private Context context;

    public BuildFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;

        switch (position) {
            case 0:
                f = new ConfigFragment();
                break;
            case 1:
                f = new InformationFragment();
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
        return context.getResources().getString(tabTitles[position]);
    }
}
