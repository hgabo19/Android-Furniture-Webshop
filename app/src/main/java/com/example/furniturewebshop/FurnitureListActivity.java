package com.example.furniturewebshop;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class FurnitureListActivity extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth fireAuth;
    private RecyclerView recyclerView;
    private ArrayList<FurnitureItem> furnitureList;
    private FurnitureItemAdapter furnitureAdapter;

    private static final String LOG_TAG = FurnitureListActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_furniture_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        fireAuth = FirebaseAuth.getInstance();

        if (user != null) {
            Log.d(LOG_TAG, "Auth user");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user");
            finish();
        }
    }
}