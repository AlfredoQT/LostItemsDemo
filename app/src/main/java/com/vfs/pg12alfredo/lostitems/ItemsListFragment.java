package com.vfs.pg12alfredo.lostitems;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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

    // Have to put this guy right here, because a it needs to be final if it is set in another scope
    // But finals cannot be initialized...
    private ItemRecyclerAdapter itemRecyclerAdapter;

    // Defines the actions of an item, to communicate back to the activity
    public interface OnItemActionsListener {
        void onAddItemRequest();
    }

    // To communicate the actions back to the activity
    private OnItemActionsListener onItemActionsListener;

    // Some hack to not have to fetch the for each item every time
    private Map<DocumentReference, User> usersInItems = new HashMap<>();

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

        recyclerView = view.findViewById(R.id.items_list_recycler_view);

        return view;
    }

    // This is called after the activity holding the fragment is created
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // I will interact with the UI elements here since the activity is created right here
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tell the listener that an item wants to be added
                onItemActionsListener.onAddItemRequest();
            }
        });

        // Set the layout manager for the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        // We are going to fetch items from the database depending on the type passed when creating the fragment
        int type = getArguments().getInt(TYPE_KEY);
        switch (type) {
            case TYPE_OWN: // Get the current user items
                FirestoreUtils.getItemsCollection()
                    .whereEqualTo("user", FirestoreUtils.getCurretUserReference())
                    .get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("ITEMS_LIST_FRAGMENT", e.toString());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // The items list to store
                            List<String> items = new ArrayList<>();
                            // Go to every one of them
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                items.add(document.getId());
                            }

                            // Create the adapter and set it to the recycler view
                            ItemRecyclerAdapter itemRecyclerAdapter = new ItemRecyclerAdapter(items, new ItemRecyclerAdapter.OnSetupViewHolder() {
                                @Override
                                public void setupItem(ItemHolder viewHolder, Item item) {
                                    setupItemHolder(viewHolder, item);
                                }
                            });
                            recyclerView.setAdapter(itemRecyclerAdapter);
                        }
                    });
                break;
            case TYPE_ALL:
                break;
            default:
                throw new RuntimeException("A type for ItemsListFragment must be specified");
        }
    }

    public void setupItemHolder(final ItemHolder itemHolder, final Item item){
        // I just make a little awesome optimization with this!!!
        // So it basically says that if the user has been fetched, don't go fetch him anymore
        if (!usersInItems.containsKey(item.getUser())) {
            // Log.i("HELLO", String.valueOf(++count));
            item.getUser()
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ITEMS_LIST_FRAGMENT", "ERROR FETCHING USER " + e.toString());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class).withId(documentSnapshot.getId());

                        setItemHolderView(itemHolder, item, user);

                        // Performance bit
                        usersInItems.put(item.getUser(), user);
                    }
                });
        } else {
            setItemHolderView(itemHolder, item, usersInItems.get(item.getUser()));
        }
    }

    private void setItemHolderView(ItemHolder itemHolder, Item item, User user) {
        // Create the views!!
        itemHolder.setItemImage(FirestoreUtils.getStorageReference().child(item.getImage()));
        itemHolder.setUserName(user.getName());
        itemHolder.setItemName(item.getName());
        itemHolder.setItemLocation(item.getLocation());
        itemHolder.setItemDescription(item.getDescription());

        // Only make the update button available for the current user posts
        itemHolder.shouldItemUpdate(user.id.compareTo(FirestoreUtils.getCurrentUserId()) == 0);
    }

}
