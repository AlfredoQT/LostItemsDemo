package com.vfs.pg12alfredo.lostitems;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Button clearPreferencesButton;
    private Button signoutButton;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        clearPreferencesButton = view.findViewById(R.id.settings_clear_preferences_button);
        signoutButton = view.findViewById(R.id.settings_signout_button);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        clearPreferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPreferences();
            }
        });

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Signout the user
                FirebaseAuth.getInstance().signOut();

                // Go to login
                startActivity(new Intent(getContext(), LoginActivity.class));

                getActivity().finish();
            }
        });
    }

    private void clearPreferences() {
        // Set the defaults
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("UniqueStringName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.COMPLETED_ONBOARDING_PREF_NAME, false);
        // Save
        editor.apply();

        Toast.makeText(getContext(), "Preferences cleared", Toast.LENGTH_SHORT).show();
    }

}
