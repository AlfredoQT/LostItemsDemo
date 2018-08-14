package com.vfs.pg12alfredo.lostitems;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class OnboardingFragmentPagerAdapter extends FragmentPagerAdapter {

    private OnboardingModel[] onboardingModels;

    public OnboardingFragmentPagerAdapter(FragmentManager fm, OnboardingModel[] onboardingModels) {
        super(fm);
        this.onboardingModels = onboardingModels;
    }

    @Override
    public Fragment getItem(int position) {
        // Build the fragment
        OnboardingModel onboardingModel = onboardingModels[position];
        OnboardingFragment onboardingFragment = new OnboardingFragment();

        // Pass the arguments
        Bundle args = new Bundle();
        args.putParcelable("Model", onboardingModel);

        onboardingFragment.setArguments(args);

        return onboardingFragment;
    }

    @Override
    public int getCount() {
        return onboardingModels.length;
    }
}
