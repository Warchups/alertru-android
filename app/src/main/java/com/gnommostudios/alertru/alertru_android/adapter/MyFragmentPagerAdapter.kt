package com.gnommostudios.alertru.alertru_android.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.gnommostudios.alertru.alertru_android.view.fragments.ConfigFragment
import com.gnommostudios.alertru.alertru_android.view.fragments.DataUserFragment
import com.gnommostudios.alertru.alertru_android.view.fragments.AlertListFragment
import com.gnommostudios.alertru.alertru_android.view.fragments.InformationFragment
import com.gnommostudios.alertru.alertru_android.view.fragments.SearchFragment

class MyFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    internal val PAGE_COUNT = 5

    override fun getItem(position: Int): Fragment? {
        var f: Fragment? = null

        when (position) {
            0 -> f = DataUserFragment()
            1 -> f = SearchFragment()
            2 -> f = AlertListFragment()
            3 -> f = ConfigFragment()
            4 -> f = InformationFragment()
        }

        return f
    }

    override fun getCount(): Int {
        return PAGE_COUNT
    }

}
