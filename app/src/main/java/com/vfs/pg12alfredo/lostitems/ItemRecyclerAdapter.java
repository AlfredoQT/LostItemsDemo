package com.vfs.pg12alfredo.lostitems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemHolder> {

    private ArrayList<Item> items;
    private ItemRowActionsInterface itemRowActionsInterface;

    public ItemRecyclerAdapter(ArrayList<Item> items, ItemRowActionsInterface itemRowActionsInterface) {
        this.items = items;
        this.itemRowActionsInterface = itemRowActionsInterface;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ItemHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(items.get(position), itemRowActionsInterface);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // I took this idea from FriendlyPix app https://github.com/firebase/friendlypix/blob/d911f6b6cf33efd63fb09dcd53207995cd437841/android/app/src/main/java/com/google/firebase/samples/apps/friendlypix/FirebasePostQueryAdapter.java
    // Every time there is a change in an item in the database the interface will be called, so the holder is updated in real time by the fragment

}
