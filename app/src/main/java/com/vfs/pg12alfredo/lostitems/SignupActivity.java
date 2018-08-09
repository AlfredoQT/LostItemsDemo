package com.vfs.pg12alfredo.lostitems;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private TextView alreadyMemberTextView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signupButton;

    // Firebase Auth
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Shared instance
        auth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        alreadyMemberTextView = findViewById(R.id.signup_already_member);
        alreadyMemberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        nameEditText = findViewById(R.id.signup_name_edit_text);
        emailEditText = findViewById(R.id.signup_email_edit_text);
        passwordEditText = findViewById(R.id.signup_password_edit_text);

        signupButton = findViewById(R.id.signup_signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

    }

    private void createUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        // Tell firebase to create the user
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // User successfully created
                if (task.isSuccessful()) {
                    writeUser(auth.getCurrentUser());
                } else {
                    // Show the guy a toast
                    Toast.makeText(SignupActivity.this, "Signup failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private  void writeUser(FirebaseUser user) {
        String name = nameEditText.getText().toString();
        // Write the data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        Log.i("WRITE_USER", user.getUid());
        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        goToMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("WRITE_USER", e.toString());
                        Toast.makeText(SignupActivity.this, "Signup failed", Toast.LENGTH_LONG).show();
                    }
                });
    }

}
