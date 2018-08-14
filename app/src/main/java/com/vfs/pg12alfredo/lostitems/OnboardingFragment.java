package com.vfs.pg12alfredo.lostitems;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class OnboardingFragment extends Fragment {

    private ImageView imageView;
    private TextView titleTextView;
    private TextView descriptionTextView;


    public OnboardingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        // Get the stuff
        imageView = view.findViewById(R.id.onboarding_model_image_view);
        titleTextView = view.findViewById(R.id.onboarding_model_title_text_view);
        descriptionTextView = view.findViewById(R.id.onboarding_model_description_text_view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        OnboardingModel onboardingModel = (OnboardingModel) getArguments().getParcelable("Model");

        imageView.setImageResource(onboardingModel.getImageHash());
        titleTextView.setText(onboardingModel.getTitle());
        descriptionTextView.setText(onboardingModel.getDescription());
    }
}
