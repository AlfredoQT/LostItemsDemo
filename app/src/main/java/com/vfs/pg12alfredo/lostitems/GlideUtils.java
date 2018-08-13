package com.vfs.pg12alfredo.lostitems;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

public class GlideUtils {

    // Loads an image into an image view
    public static void loadImage(StorageReference storageReference, ImageView imageView) {
        // Load the image with Glide
        // TODO: Add a placeholder to show while the image loads

        Glide
            .with(imageView.getContext())
            .using(new FirebaseImageLoader())
            .load(storageReference)
            .centerCrop()
            .crossFade()
            .into(imageView);
    }

}
