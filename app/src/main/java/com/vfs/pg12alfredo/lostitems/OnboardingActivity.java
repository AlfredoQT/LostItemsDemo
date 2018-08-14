package com.vfs.pg12alfredo.lostitems;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class OnboardingActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private Button startButton;
    private OnboardingModel[] onboardingModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.onboarding_view_pager);

        onboardingModels = new OnboardingModel[3];

        onboardingModels[0] = new OnboardingModel(R.drawable.logo, "Register lost items", "Register your lost items in the cloud. Find them later.");
        onboardingModels[1] = new OnboardingModel(R.drawable.people_talking, "Find others' items", "Find others' items and let them find yours!");
        onboardingModels[2] = new OnboardingModel(R.drawable.cool_map, "Add items all around the globe", "See where you lost your items and where people lost the items around the globe!");

        viewPager.setAdapter(new OnboardingFragmentPagerAdapter(getSupportFragmentManager(), onboardingModels));
        viewPager.addOnPageChangeListener(this);

        startButton = findViewById(R.id.onboarding_start_button);
        startButton.setVisibility(View.GONE);

        // Listen to button click
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the defaults
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UniqueStringName", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.COMPLETED_ONBOARDING_PREF_NAME, true);
                // Save
                editor.apply();

                // Go to main app
                Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // Enable the button
        if (position == onboardingModels.length - 1) {
            startButton.setVisibility(View.VISIBLE);
            Log.i("ONBOARDING", "Show button");
        } else {
            startButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
