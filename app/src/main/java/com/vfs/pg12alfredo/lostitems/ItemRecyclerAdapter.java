package com.vfs.pg12alfredo.lostitems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

import javax.annotation.Nullable;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemHolder> {

    // Just store the ids of the items
    // I stored the items and every time there was a single change in one document of the items collection
    // ...everything would be re rendered
    // So for instance, if one of the items was set to found, everything will be fetched again from the server
    // Definitely not what I wanted
    // So I just listen to single changes and instead get everything on the fragment's onResume
    // I also added a SwipeRefreshLayout to get new stuff
    private List<String> itemsIds;
    private OnSetupViewHolder onSetupViewHolder;

    public ItemRecyclerAdapter(List<String> itemsIds, OnSetupViewHolder onSetupViewHolder) {
        this.itemsIds = itemsIds;
        this.onSetupViewHolder = onSetupViewHolder;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ItemHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, int position) {

        // Listen to any change of each document alone, not to any of them
        String documentToListen = itemsIds.get(position);

        FirestoreUtils.getItemsCollection()
            .document(documentToListen)
            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    // Call the interface method
                    // Will update a SINGLE item in the recycler view contained in the items list fragment
                    Item item = documentSnapshot.toObject(Item.class).withId(documentSnapshot.getId());
                    onSetupViewHolder.setupItem(holder, item);
                }
            });
    }

    @Override
    public int getItemCount() {
        return itemsIds.size();
    }

    // I took this idea from FriendlyPix app https://github.com/firebase/friendlypix/blob/d911f6b6cf33efd63fb09dcd53207995cd437841/android/app/src/main/java/com/google/firebase/samples/apps/friendlypix/FirebasePostQueryAdapter.java
    // Every time there is a change in an item in the database the interface will be called, so the holder is updated in real time by the fragment
    // At least I hope so...
    // I'm going to do the setup of the holders in the items list fragment, it is quite a big setup
    // I'll just expose everything on the holder itself
    // Call this on binding
    // This interface thing is pretty cool, communication seems straightforward
    public interface OnSetupViewHolder {
        void setupItem(ItemHolder viewHolder, Item item);
    }

}
