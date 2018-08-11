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

    private ArrayList<Item> items;
    private OnSetupViewHolder onSetupViewHolder;

    public ItemRecyclerAdapter(ArrayList<Item> items, OnSetupViewHolder onSetupViewHolder) {
        this.items = items;
        this.onSetupViewHolder = onSetupViewHolder;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ItemHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        // Simply tell the guy with the recycler view to setup the holder
        onSetupViewHolder.setupItem(holder, items.get(position));

    }

    @Override
    public int getItemCount() {
        return items.size();
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
