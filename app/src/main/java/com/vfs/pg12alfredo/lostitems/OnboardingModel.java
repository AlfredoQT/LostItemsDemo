package com.vfs.pg12alfredo.lostitems;

import android.os.Parcel;
import android.os.Parcelable;

public class OnboardingModel implements Parcelable {

    private int imageHash;
    private String title;
    private String description;

    public OnboardingModel(int imageHash, String title, String description) {
        this.imageHash = imageHash;
        this.title = title;
        this.description = description;
    }

    private OnboardingModel(Parcel in) {
        // Read the fields from the source
        imageHash = in.readInt();
        title = in.readString();
        description = in.readString();
    }

    public int getImageHash() {
        return imageHash;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // Create the parcelable object
    public static final Creator<OnboardingModel> CREATOR = new Creator<OnboardingModel>() {
        @Override
        public OnboardingModel createFromParcel(Parcel in) {
            return new OnboardingModel(in);
        }

        @Override
        public OnboardingModel[] newArray(int size) {
            return new OnboardingModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write to the parcel, I guess it has to be in order
        dest.writeInt(imageHash);
        dest.writeString(title);
        dest.writeString(description);
    }
}
