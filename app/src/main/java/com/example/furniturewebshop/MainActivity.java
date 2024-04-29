package com.example.furniturewebshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 69;
    private static final String PREFERENCES_KEY = MainActivity.class.getPackage().toString();
    EditText usernameET;
    EditText passwordET;
    Button loginBtn;
    Button registerBtn;
    Button guestBtn;
    private FirebaseAuth fireAuth;

    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameET = findViewById(R.id.EditTextUsername);
        passwordET = findViewById(R.id.EditTextPassword);
        loginBtn = findViewById(R.id.loginButton);
        registerBtn = findViewById(R.id.registerButton);
        guestBtn = findViewById(R.id.guestLoginButton);

        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        usernameET.setAnimation(slideInAnimation);
        passwordET.setAnimation(slideInAnimation);
        loginBtn.setAnimation(fadeInAnimation);
        registerBtn.setAnimation(fadeInAnimation);
        guestBtn.setAnimation(fadeInAnimation);


        preferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        fireAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void login(View view) {
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please provide your email and password!", Toast.LENGTH_SHORT).show();
            return;
        }

        fireAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    navigateToFurnitureList();
//                    Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Login failed"  + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    public void navigateToFurnitureList(){
        Intent intent = new Intent(this, FurnitureListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", usernameET.getText().toString());
        editor.putString("password", passwordET.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void guestLogin(View view) {
        fireAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
//                Log.d(LOG_TAG, "Anonym user loged in successfully");
                navigateToFurnitureList();
            } else {
//                Log.d(LOG_TAG, "Anonym user log in fail");
                Toast.makeText(MainActivity.this, "User log in fail: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}