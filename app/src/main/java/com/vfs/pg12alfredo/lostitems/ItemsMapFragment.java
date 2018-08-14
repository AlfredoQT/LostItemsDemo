package com.vfs.pg12alfredo.lostitems;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap map;


    public ItemsMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_items_map, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Let's get the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_items_map);
        mapFragment.getMapAsync(this);

        // Listen to changes in the items collection
        FirestoreUtils.getItemsCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                // Clear the map
                map.clear();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Item item = document.toObject(Item.class).withId(document.getId());
                    LatLng position = new LatLng(item.getLocation().getLatitude(), item.getLocation().getLongitude());

                    // Set the color based on state
                    BitmapDescriptor markerBitmapDescriptor = item.isFound() ?
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            :
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);

                    // Sorry for the slow comparison
                    float alpha = FirestoreUtils.getCurretUserReference().getId().compareTo(item.getUser().getId()) == 0 ?
                            1.0f
                            :
                            0.5f;

                    // Add some nice markers
                    Marker marker = map.addMarker(new MarkerOptions()
                        .position(position).draggable(true)
                        .icon(markerBitmapDescriptor)
                            .alpha(alpha)
                        );

                    // I need this to keep information of the item inside the marker
                    marker.setTag(item);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLongClickListener(this);
        map.setOnMarkerDragListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Start the intent
        Intent intent = new Intent(getContext(), SetItemActivity.class);
        intent.putExtra("COORDS", latLng);

        startActivity(intent);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // Get the item bind to this marker
        Item item = (Item) marker.getTag();

        // Start the intent
        Intent intent = new Intent(getContext(), ToggleFoundActivity.class);
        intent.putExtra("ITEM", item.id);
        intent.putExtra("ITEM_NEW_LOCATION", marker.getPosition());
        startActivity(intent);
    }
}
