package com.vfs.pg12alfredo.lostitems;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UniqueStringName", Context.MODE_PRIVATE);

        // Check for the pref. The first time the key does not exist, so is false by default
        boolean completed =  sharedPreferences.getBoolean(Constants.COMPLETED_ONBOARDING_PREF_NAME, false);

        // Go to the onboarding
        if (!completed) {
            Intent intent = new Intent(SplashScreenActivity.this, OnboardingActivity.class);
            startActivity(intent);
            return;
        }

        // Go to the main app
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
