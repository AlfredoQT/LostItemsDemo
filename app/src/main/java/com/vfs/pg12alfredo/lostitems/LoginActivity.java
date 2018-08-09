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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextView newAccountTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        newAccountTextView = findViewById(R.id.login_new_account);
        newAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.login_email_edit_text);
        passwordEditText = findViewById(R.id.login_password_edit_text);

        loginButton = findViewById(R.id.login_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check here if there is already an authenticated user
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            goToMain();
        }
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Success
                if (task.isSuccessful()) {
                    goToMain();
                } else {
                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
