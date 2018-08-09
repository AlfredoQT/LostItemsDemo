package com.vfs.pg12alfredo.lostitems;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ItemsListFragment();
            case 1:
                return new ItemsMapFragment();
            case 2:
                return new SettingsFragment();
            default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
