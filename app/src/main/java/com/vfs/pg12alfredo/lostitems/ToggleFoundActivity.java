package com.vfs.pg12alfredo.lostitems;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import java.util.Map;

import javax.annotation.Nullable;

public class ToggleFoundActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, FindItemTaskFragment.FindItemTaskCallbacks {

    private String itemId;
    private GoogleMap map;
    private Marker selectedMarker;
    private TextView stateTextView;
    private Button updateButton;

    private FindItemTaskFragment findItemTaskFragment;

    private static final String FIND_ITEM_TASK_FRAGMENT = "FIND_ITEM_TASK_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle_found);

        // Get the data from the intent
        itemId = getIntent().getStringExtra("ITEM");

        stateTextView = findViewById(R.id.toggle_found_state_text_view);
        updateButton = findViewById(R.id.toggle_found_update_button);

        // Since the upload item task fragment is retained on config changes
        // ... we can get the existing one when the activity restarts
        FragmentManager fm = getSupportFragmentManager();
        findItemTaskFragment = (FindItemTaskFragment) fm.findFragmentByTag(FIND_ITEM_TASK_FRAGMENT);

        // Create it programmatically the first time: https://developer.android.com/guide/components/fragments
        if (findItemTaskFragment == null) {
            findItemTaskFragment = new FindItemTaskFragment();
            // Commit the fragment
            fm.beginTransaction().add(findItemTaskFragment, FIND_ITEM_TASK_FRAGMENT).commit();
        }

        // Set the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.toggle_found_map);
        mapFragment.getMapAsync(this);

        // Listen to any live changes on updates
        FirestoreUtils.getItemsCollection().document(itemId)
            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    // To avoid the serialization process, we only need some data
                    Map<String, Object> data = documentSnapshot.getData();
                    final boolean found = (boolean) data.get("found");
                    final GeoPoint location = (GeoPoint) data.get("location");

                    // Update the marker
                    LatLng markerLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    selectedMarker.setPosition(markerLocation);
                    map.moveCamera(CameraUpdateFactory.newLatLng(markerLocation));
                    map.animateCamera(CameraUpdateFactory.zoomTo(14.0f));

                    stateTextView.setText(found ? "Where did you lose me?" : "Where did you find me?");

                    // Set the listener
                    updateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LatLng markerCoords = selectedMarker.getPosition();
                            GeoPoint newLocation = new GeoPoint(markerCoords.latitude, markerCoords.longitude);
                            // Start the task
                            // With the toggle
                            findItemTaskFragment.findItem(documentSnapshot.getId(), !found, newLocation);
                        }
                    });
                }
            });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add default marker
        LatLng sydney = new LatLng(-34, 151);
        selectedMarker = map.addMarker(new MarkerOptions().position(sydney));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.animateCamera(CameraUpdateFactory.zoomTo(14.0f));

        map.setOnMapClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO: Find a way to remove the listener
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Update the selected marker
        selectedMarker.setPosition(latLng);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
    }

    @Override
    public void onItemUpdated(String error) {
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
