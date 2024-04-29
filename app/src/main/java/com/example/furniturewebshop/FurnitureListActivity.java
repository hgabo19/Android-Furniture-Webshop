package com.example.furniturewebshop;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

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
    private TextView countTextView;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private CollectionReference mCartItems;

    private AlarmManager mAlarmManager;

    private PendingIntent mPendingIntent;

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

        fireAuth = FirebaseAuth.getInstance();
        user = fireAuth.getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Auth user " + user.getUid());
        } else {
            Log.d(LOG_TAG, "Unauthenticated user");
            finish();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNum));

        furnitureList = new ArrayList<>();
        furnitureAdapter = new FurnitureItemAdapter(this, furnitureList);
        recyclerView.setAdapter(furnitureAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");
        mCartItems = mFirestore.collection("CartItems");

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queryData();
    }

    private void queryData(){
        furnitureList.clear();
        mItems.orderBy("price", Query.Direction.ASCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                FurnitureItem item = document.toObject(FurnitureItem.class);
                item.setId(document.getId());
                furnitureList.add(item);
            }
            if(furnitureList.isEmpty()) {
                initalizeData();
                queryData();
            }
            furnitureAdapter.notifyDataSetChanged();
        });
        Query cartItemsQuery = mCartItems.whereEqualTo("userId", user.getUid());
    }

    private void initalizeData() {
        String[] furnitureNames = getResources().getStringArray(R.array.furniture_item_name);
        String[] furnitureDescriptions = getResources().getStringArray(R.array.furniture_item_description);
        String[] furniturePrices = getResources().getStringArray(R.array.furniture_item_price);

        TypedArray furnitureImageResources = getResources().obtainTypedArray(R.array.furniture_item_image);

        for (int i = 0; i < furnitureNames.length; i++){
            mItems.add(new FurnitureItem(
                    furnitureNames[i],
                    furnitureDescriptions[i],
                    furniturePrices[i],
                    furnitureImageResources.getResourceId(i, 0)));
        }

        furnitureImageResources.recycle();

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
        if(item.getItemId() == R.id.cart) {
            Log.d("Activity", "Cart clicked");
          return true;
        }
        else if (item.getItemId() == R.id.logout_button){
            if(!user.isAnonymous()){
                Log.d("Activity", "Logout clicked");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void deleteFromCart(FurnitureItem item){

        Query query = mCartItems.whereEqualTo("itemId", item._getId()).whereEqualTo("userId", user.getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);

                    int cartCount = document.getLong("cartCount").intValue();

                    mCartItems.document(document.getId()).delete();

                    updateIconSubtraction(cartCount);
                } else if(user.isAnonymous()) {
                    Toast.makeText(FurnitureListActivity.this, "Please login first!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(FurnitureListActivity.this, "You need to add this item to your cart first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addToCart(FurnitureItem item){
        if (user != null && !user.isAnonymous()) {
            String itemId = item._getId();
            String userId = user.getUid();

            Query query = mCartItems.whereEqualTo("itemId", itemId).whereEqualTo("userId", userId);
            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                    mFirestore.runTransaction(transaction -> {
                        DocumentSnapshot snapshot = transaction.get(document.getReference());
                        if (snapshot.exists()) {
                            int currentCartCount = snapshot.getLong("cartCount").intValue() + 1;
                            transaction.update(snapshot.getReference(), "cartCount", currentCartCount);

                        }
                        return null;
                    }).addOnCompleteListener(transactionTask -> {
                        if(transactionTask.isSuccessful()) {
                            updateIconAddition();
                        }
                        else {
                            Log.w("Firestore", "Transaction failed.", transactionTask.getException());
                        }
                    });
                } else {

                    CartItem cartItem = new CartItem(itemId, userId, 1);
                    mCartItems.add(cartItem)
                            .addOnSuccessListener(documentReference -> {
                                updateIconAddition();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(LOG_TAG, "Error adding item to cart", e);
                            });
                }
            }).addOnFailureListener(e -> {
                Log.e(LOG_TAG, "Error getting document reference", e);
            });
        } else {
            Toast.makeText(this, "Please login to add items to your cart!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.cart);
        FrameLayout rootview = (FrameLayout) menuItem.getActionView();
        circle = (FrameLayout) rootview.findViewById(R.id.red_circle);
        countTextView = (TextView) rootview.findViewById(R.id.cart_count_textview);
        rootview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateIconAddition() {
        cartItems = cartItems + 1;
        Log.d(LOG_TAG, "cartItems: "+ cartItems);
        if(cartItems > 0) {
            circle.setVisibility(View.VISIBLE);
            countTextView.setText(String.valueOf(cartItems));
            setAlarmManager();
        } else {
            countTextView.setText("");
        }
    }

    public void updateIconSubtraction(int cartCount) {
        cartItems = cartItems - cartCount;
        if(cartItems <= 0) {
            circle.setVisibility(View.GONE);
            countTextView.setText("");
            mAlarmManager.cancel(mPendingIntent);
        } else {
            countTextView.setText(String.valueOf(cartItems));
        }
    }

    private void setAlarmManager (){
        long repeatInterval = 60000 * 5;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent intent = new Intent(this, AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                mPendingIntent);
    }

}