package com.vfs.pg12alfredo.lostitems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
