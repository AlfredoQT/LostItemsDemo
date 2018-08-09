package com.vfs.pg12alfredo.lostitems;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private TextView alreadyMemberTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signupButton;

    // Firebase Auth
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Shared instance
        auth = FirebaseAuth.getInstance();

        alreadyMemberTextView = findViewById(R.id.signup_already_member);
        alreadyMemberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

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
                    // TODO: Redirect the user to another view
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

}
