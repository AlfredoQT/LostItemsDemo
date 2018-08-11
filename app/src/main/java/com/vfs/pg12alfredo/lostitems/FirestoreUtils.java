package com.vfs.pg12alfredo.lostitems;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

// A bunch of static methods just to make my life easier
// Idea from example FriendlyPix: https://github.com/firebase/friendlypix/tree/d911f6b6cf33efd63fb09dcd53207995cd437841/android/app/src/main/java/com/google/firebase/samples/apps/friendlypix
public class FirestoreUtils {

    public static String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Safety check
        if (currentUser == null) {
            return null;
        }

        return currentUser.getUid();
    }

    public static FirebaseFirestore getDatabaseInstance() {
        return FirebaseFirestore.getInstance();
    }

    public static CollectionReference getUsersCollection() {
        return getDatabaseInstance().collection("users");
    }

    public static CollectionReference getItemsCollection() {
        return getDatabaseInstance().collection("items");
    }

    public static DocumentReference getCurretUserReference() {
        return getUsersCollection().document(getCurrentUserId());
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
