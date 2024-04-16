package com.example.furniturewebshop;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getName();

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

        // Get the intent
        // Bundle bundle = getIntent().getExtras();
        // bundle.getInt("SECRET_KEY", 0);
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

        if(!password.equals(passwordConfirm)) {
            Log.e(TAG, "Password confirmation error: they don't match");
        } else {
            Log.i(TAG, "Welcome new member: " + username + ", email: " + email);
        }
        // TODO
    }

    public void cancel(View view) {
        finish();
    }
}