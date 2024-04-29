package com.example.furniturewebshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getName();
    private FirebaseAuth fireAuth;

    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    EditText passwordConfirmET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameET = findViewById(R.id.usernameEditText);
        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);
        passwordConfirmET = findViewById(R.id.passwordConfirmEditText);
        fireAuth = FirebaseAuth.getInstance();

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if(secret_key != 69) {
            finish();
        }
    }

    public void register(View view) {
        String username = usernameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirm = passwordConfirmET.getText().toString();

        if(username.isEmpty() || password.isEmpty() || email.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill out everything!", Toast.LENGTH_SHORT).show();
            return;
        } else if(!password.equals(passwordConfirm)) {
            Log.e(TAG, "Password confirmation error: they don't match");
            Toast.makeText(RegisterActivity.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
            return;
        }
        fireAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    navigateToFurnitureList();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void navigateToFurnitureList(){
        Intent intent = new Intent(this, FurnitureListActivity.class);
        startActivity(intent);
    }

    public void alreadyRegistered(View view) {
        finish();
    }
}