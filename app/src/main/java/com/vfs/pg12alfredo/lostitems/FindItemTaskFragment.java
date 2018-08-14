package com.vfs.pg12alfredo.lostitems;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class FindItemTaskFragment extends Fragment {

    public interface FindItemTaskCallbacks {
        void onItemUpdated(String error);
    }

    private FindItemTaskCallbacks findItemTaskCallbacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Check if the activity I am being attached to implements the UploadItemTaskCallbacks interface
        if (context instanceof FindItemTaskFragment.FindItemTaskCallbacks) {
            // Store the interface reference to use it in the async task
            findItemTaskCallbacks = (FindItemTaskFragment.FindItemTaskCallbacks) context;
        } else {
            // Throw an error to stop the application, the task would return errors since the interface reference would be null
            throw new RuntimeException(context.toString() + " must implement UploadItemTaskCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // We no longer need the reference to the interface, avoiding memory leaks
        findItemTaskCallbacks = null;
    }

    public void findItem(String id, boolean found, GeoPoint location) {
        // Execute the async task
        FindItemTask findItemTask = new FindItemTask(id, found, location);
        findItemTask.execute();
    }

    private class FindItemTask extends AsyncTask<Void, Void, Void> {

        private String id;
        private boolean found;
        private GeoPoint location;

        public FindItemTask(String id, boolean found, GeoPoint location) {
            this.id = id;
            this.found = found;
            this.location = location;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Map<String, Object> data = new HashMap<>();

            data.put("found", this.found);
            data.put("location", this.location);

            FirestoreUtils.getItemsCollection().document(id)
                .set(data)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        findItemTaskCallbacks.onItemUpdated("Error updating item");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        findItemTaskCallbacks.onItemUpdated(null);
                    }
                });
            return null;
        }
    }
}
