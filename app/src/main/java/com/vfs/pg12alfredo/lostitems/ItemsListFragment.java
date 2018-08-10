package com.vfs.pg12alfredo.lostitems;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsListFragment extends Fragment {

    private FloatingActionButton floatingActionButton;

    private RecyclerView recyclerView;

    // Defines the actions of an item, to communicate back to the activity
    public interface OnItemActionsListener {
        void onAddItemRequest();
    }

    // To communicate the actions back to the activity
    private OnItemActionsListener onItemActionsListener;

    public ItemsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Make sure that the activity holding the fragment implements the actions listener
        if (context instanceof OnItemActionsListener) {
            onItemActionsListener = (OnItemActionsListener) context;
        } else {
            // Throw an exception, the fragment needs to communicate the actions back
            throw new RuntimeException(context.toString() + " must implement OnItemActionsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // We don't need a reference to the listener anymore, clean up
        onItemActionsListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_items_list, container, false);
        floatingActionButton = view.findViewById(R.id.items_list_fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tell the listener that an item wants to be added
                onItemActionsListener.onAddItemRequest();
            }
        });

        recyclerView = view.findViewById(R.id.items_list_recycler_view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Here is where we get
    }
}
