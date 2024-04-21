package com.example.furniturewebshop;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private int gridNum = 2;
    private FrameLayout circle;
    private int cartItems = 0;

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNum));

        furnitureList = new ArrayList<>();
        furnitureAdapter = new FurnitureItemAdapter(this, furnitureList);
        recyclerView.setAdapter(furnitureAdapter);

        initalizeData();
    }

    private void initalizeData() {
        String[] furnitureNames = getResources().getStringArray(R.array.furniture_item_name);
        String[] furnitureDescriptions = getResources().getStringArray(R.array.furniture_item_description);
        TypedArray furniturePrices = getResources().obtainTypedArray(R.array.furniture_item_price);

        TypedArray furnitureImageResources = getResources().obtainTypedArray(R.array.furniture_item_image);
        this.furnitureList.clear();

        for (int i = 0; i < furnitureNames.length; i++){
            this.furnitureList.add(new FurnitureItem(furnitureNames[i], furnitureDescriptions[i], furniturePrices.getResourceId(i, 0), furnitureImageResources.getResourceId(i, 0)));
        }

        furnitureImageResources.recycle();
        furnitureAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.furniture_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                furnitureAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.logout_button:
//                Log.d("Activity", "Logout clicked");
//                FirebaseAuth.getInstance().signOut();
//                finish();
//                return true;
//            case R.id.cart:
//                Log.d("Activity", "Cart clicked");
//                return true;
//            case R.id.settings_button:
//                Log.d("Activity", "Settings clicked");
//                return true;
//            default:
//        }
//        switch (item.getItemId()) {
//            case R.
//        }
        if(item.getItemId() == R.id.cart) {
            Log.d("Activity", "Cart clicked");
          return true;
        }
        else if (item.getItemId() == R.id.logout_button){
            Log.d("Activity", "Logout clicked");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        }
        else if (item.getItemId() == R.id.settings_button){
            Log.d("Activity", "Settings clicked");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.cart);
        FrameLayout rootview = (FrameLayout) menuItem.getActionView();
        circle = (FrameLayout) rootview.findViewById(R.id.red_circle);
        rootview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateIcon() {
        cartItems = cartItems + 1;
        if(cartItems > 0) {
            circle.setVisibility(View.VISIBLE);
        }
    }
}