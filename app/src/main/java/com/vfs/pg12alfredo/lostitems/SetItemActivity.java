package com.vfs.pg12alfredo.lostitems;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

public class SetItemActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, UploadItemTaskFragment.UploadItemTaskCallbacks {

    private GoogleMap map;
    private Marker selectedMarker;

    private Toolbar toolbar;
    private ImageView selectImageView;
    private EditText nameEditText;
    private EditText descriptionEditText;

    // The image uri that we get from the picker
    private Uri selectedImageUri;

    // The reference to the fragment that is going to take care of the async task of uploading an item
    private UploadItemTaskFragment uploadItemTaskFragment;

    private static final String UPLOAD_ITEM_TASK_FRAGMENT_TAG = "UPLOAD_ITEM_TASK_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_item);

        // Since the upload item task fragment is retained on config changes
        // ... we can get the existing one when the activity restarts
        FragmentManager fm = getSupportFragmentManager();
        uploadItemTaskFragment = (UploadItemTaskFragment) fm.findFragmentByTag(UPLOAD_ITEM_TASK_FRAGMENT_TAG);

        // Create it programmatically the first time: https://developer.android.com/guide/components/fragments
        if (uploadItemTaskFragment == null) {
            uploadItemTaskFragment = new UploadItemTaskFragment();
            // Commit the fragment
            fm.beginTransaction().add(uploadItemTaskFragment, UPLOAD_ITEM_TASK_FRAGMENT_TAG).commit();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.set_item_map);
        mapFragment.getMapAsync(this);

        // Set the toolbar
        toolbar = findViewById(R.id.set_item_toolbar);
        setActionBar(toolbar);

        nameEditText = findViewById(R.id.set_item_name_edit_text);
        descriptionEditText = findViewById(R.id.set_item_desc_edit_text);

        selectImageView = findViewById(R.id.set_item_image_view);
        selectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntentForPhotoPicker();
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
    public void onMapClick(LatLng latLng) {
        selectedMarker.setPosition(latLng);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_set_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_set_item_add:
                onItemAdd();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onItemAdd() {
        if (selectedImageUri == null) {
            Toast.makeText(SetItemActivity.this, "Select an image", Toast.LENGTH_LONG).show();
            return;
        }

        // TODO: Validate other fields

        // Prepare everything fo
        Bitmap bitmap = ((BitmapDrawable) selectImageView.getDrawable()).getBitmap();
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        GeoPoint location = new GeoPoint(selectedMarker.getPosition().latitude, selectedMarker.getPosition().longitude);

        // Let the fragment take care of everything, pretty SOLID
        // TODO: Add a progress dialog
        uploadItemTaskFragment.uploadItem(bitmap, name, description, location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 300: // Will only be called if check for read permission returns false, before opening the request dialog
                // Now check for read permission returns true
                sendIntentForPhotoPicker();
                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // This time we come from the picker
        switch (requestCode) {
            case 200:
                // Get the data
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = data.getData();
                    // Set the image to this guy
                    selectImageView.setImageURI(selectedImageUri);
                }
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkForReadPermission() {
        if (ContextCompat.checkSelfPermission(SetItemActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 300);
            return false;
        }
        return true;
    }

    public void sendIntentForPhotoPicker() {
        if (checkForReadPermission()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select a picture"), 200);
        }
    }

    // The callback that we get from the upload item task fragment
    @Override
    public void onItemUploaded(final String error) {
        // Run on the main thread, the method is call on an async task
        SetItemActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Handle any errors given on the task
                if (error != null) {
                    Toast.makeText(SetItemActivity.this, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                // Go back
                finish();
            }
        });
    }
}
