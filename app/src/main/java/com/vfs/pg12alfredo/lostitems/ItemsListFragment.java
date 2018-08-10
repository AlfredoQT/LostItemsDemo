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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsListFragment extends Fragment {

    FirebaseFirestore db;

    // Types of fragment
    public static final int TYPE_OWN = 0;
    public static final int TYPE_ALL = 1;

    // Just to not get lost
    private static final String TYPE_KEY = "TYPE";

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

    // Only because we are going to reuse the fragment, so we need different types
    // I use a static method because I need to set some arguments to the fragment
    // I should probably use an enum, but I can only put serialize fields on the bundle
    public static ItemsListFragment newInstance(int type) {
        ItemsListFragment fragment = new ItemsListFragment();

        // This arguments will be accessed on onActivityCreated
        Bundle args = new Bundle();

        args.putInt(TYPE_KEY, type);

        // Attach the args to the fragment
        fragment.setArguments(args);

        return fragment;
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

        // We are going to listen to some changes in the database depending on the type passed when creating the fragment
        int type = getArguments().getInt(TYPE_KEY);

        switch (type) {
            case TYPE_OWN:
                // Listen to changes of our items
                FirestoreUtils.getItemsCollection()
                    .whereEqualTo("user", FirestoreUtils.getCurretUserReference())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        }
                    });
                break;
            case TYPE_ALL:
                break;
            default:
                throw new RuntimeException("A type for ItemsListFragment must be specified");
        }
    }
}
