package com.vfs.pg12alfredo.lostitems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemHolder> {

    // I used to store the items themselves
    // But whenever I did a manual update on one, I would still pass the old reference
    // So I just decide to store the ids
    // I could pass to the interface method a new item, but then this array of items would be outdated
    // So just ids again
    private ArrayList<String> itemIds;
    private OnSetupViewHolder onSetupViewHolder;

    public ItemRecyclerAdapter(ArrayList<String> itemIds, OnSetupViewHolder onSetupViewHolder) {
        this.itemIds = itemIds;
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
        FirestoreUtils.getItemsCollection().document(itemIds.get(position))
            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    // Now call the method from the interface, which will be executed on the fragment containing the recycler view
                    Item item = documentSnapshot.toObject(Item.class).withId(documentSnapshot.getId());
                    onSetupViewHolder.setupItem(holder, item);
                }
            });
    }

    @Override
    public int getItemCount() {
        return itemIds.size();
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
