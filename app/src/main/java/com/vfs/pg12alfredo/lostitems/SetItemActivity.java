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
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetItemActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap map;
    private Marker selectedMarker;

    private Toolbar toolbar;
    private ImageView selectImageView;
    private EditText nameEditText;
    private EditText descriptionEditText;

    // The image uri that we get from the picker
    private Uri selectedImageUri;

    // Firebase storage ref
    private StorageReference storageReference;

    // We need a reference to the user
    private FirebaseAuth auth;

    // Also to the store to save the item
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_item);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.set_item_map);
        mapFragment.getMapAsync(this);

        // Get the reference from firebase storage
        storageReference = FirebaseStorage.getInstance().getReference();

        auth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

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

        Bitmap bitmap = ((BitmapDrawable) selectImageView.getDrawable()).getBitmap();
        if (bitmap != null) {
            // Construct a byte array and upload it to the cloud storage
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Some high quality JPEG
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            // Child reference
            final String childReference = "images/" + UUID.randomUUID().toString();

            // Here it goes
            UploadTask uploadTask = storageReference.child(childReference).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetItemActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            })
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    saveToItemCollection(childReference);
                }
            });
        }
    }

    private void saveToItemCollection(String imageStorageReference) {
        // Get a reference to the authenticated user
        FirebaseUser currentUser = auth.getCurrentUser();

        Log.i("SAVE_ITEM_USER_ID", currentUser.getUid());

        // Setup the data
        LatLng selectedMarkerLatLng = selectedMarker.getPosition();

        Map<String, Object> data = new HashMap<>();
        data.put("name", nameEditText.getText().toString());
        data.put("description", descriptionEditText.getText().toString());
        data.put("image", imageStorageReference);
        data.put("found", false);
        data.put("location", new GeoPoint(selectedMarkerLatLng.latitude, selectedMarkerLatLng.longitude));
        data.put("user", db.collection("users").document(currentUser.getUid()));

        // Save
        db.collection("items").document(UUID.randomUUID().toString()).set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Go back
                            finish();
                            return;
                        }
                        Toast.makeText(SetItemActivity.this, "Could not save item", Toast.LENGTH_SHORT).show();
                    }
                });
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
}
