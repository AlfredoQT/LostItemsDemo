package com.vfs.pg12alfredo.lostitems;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// I will add this fragment programmatically to the set item activity
// It will take care of uploading the item asynchronously
// Took the idea from Google's Firebase example https://github.com/firebase/friendlypix/tree/d911f6b6cf33efd63fb09dcd53207995cd437841/android/app/src/main/java/com/google/firebase/samples/apps/friendlypix
// It is super cool because it makes everything super modular and awesome
public class UploadItemTaskFragment extends Fragment {

    private UploadItemTaskCallbacks uploadItemTaskCallbacks;

    // This is how I will tell the activities that the items have been uploaded
    public interface UploadItemTaskCallbacks {
        // Passes an optional error back to the activities
        void onItemUploaded(String error);
    }

    public UploadItemTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // I am going to make asynchronous tasks on this fragment
        // Configuration changes can occur at any time, which could result on destroying this fragment
        // For instance, the fragment could be performing an async task while the user is rotating the screen
        // This would destroy the activity, destroying also the fragment
        // Then the fragment would run again the async task without knowing that one is already running, which results in wasting resources
        // That's why I retain the instance when config changes
        // I read about it here: https://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Check if the activity I am being attached to implements the UploadItemTaskCallbacks interface
        if (context instanceof UploadItemTaskCallbacks) {
            // Store the interface reference to use it in the async task
            uploadItemTaskCallbacks = (UploadItemTaskCallbacks) context;
        } else {
            // Throw an error to stop the application, the task would return errors since the interface reference would be null
            throw new RuntimeException(context.toString() + " must implement UploadItemTaskCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // We no longer need the reference to the interface, avoiding memory leaks
        uploadItemTaskCallbacks = null;
    }

    // Will be called on the activities that attach this fragment to upload the items
    public void uploadItem(Bitmap itemImage, String itemName, String itemDescription, GeoPoint itemLocation) {
        // Execute the async task
        UploadItemTask uploadItemTask = new UploadItemTask(itemImage, itemName, itemDescription, itemLocation);
        uploadItemTask.execute();
    }

    // We will not be accepting any parameters or return anything, so everything is void
    private class UploadItemTask extends AsyncTask<Void, Void, Void> {

        private Bitmap itemImage;
        private String itemName;
        private String itemDescription;
        private GeoPoint itemLocation;

        // Accept some stuff
        public UploadItemTask(Bitmap itemImage, String itemName, String itemDescription, GeoPoint itemLocation) {
            this.itemImage = itemImage;
            this.itemName = itemName;
            this.itemDescription = itemDescription;
            this.itemLocation = itemLocation;
        }

        // Major refactoring super update
        // The validation happens in the activity containing this fragment
        // The fragment performs the async task
        // They communicate through an interface, that's it
        @Override
        protected Void doInBackground(Void... voids) {
            // Get the references to the storage
            final StorageReference storageReference = FirestoreUtils.getStorageReference();

            // First step, store the image
            // Construct a byte array and upload it to the cloud storage
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Some high quality JPEG
            itemImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            // Child reference
            final String imageReference = "images/" + UUID.randomUUID().toString();

            // Here it goes
            UploadTask uploadTask = storageReference.child(imageReference).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Communicate back to the activity that there was an error
                    // TODO: Put this into string reources
                    uploadItemTaskCallbacks.onItemUploaded("Item upload failed");
                }
            })
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Map<String, Object> data = new HashMap<>();
                    data.put("name", itemName);
                    data.put("description", itemDescription);
                    data.put("image", imageReference);
                    data.put("found", false);
                    data.put("location", itemLocation);
                    data.put("user", FirestoreUtils.getCurretUserReference());

                    // Save
                    FirestoreUtils.getItemsCollection().document(UUID.randomUUID().toString())
                        .set(data)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                uploadItemTaskCallbacks.onItemUploaded("Item upload failed");
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // No errors!
                                uploadItemTaskCallbacks.onItemUploaded(null);
                            }
                        });
                }
            });

            return null;
        }
    }
}
