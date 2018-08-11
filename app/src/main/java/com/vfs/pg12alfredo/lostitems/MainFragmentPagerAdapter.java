package com.vfs.pg12alfredo.lostitems;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    // List of fragments passed to the adapter
    private ArrayList<Fragment> fragments;

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<Fragment>();
    }

    @Override
    public Fragment getItem(int position) {
        // We no longer need a horrible switch!
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    // A public method to add fragments to the adapter
    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }
}
