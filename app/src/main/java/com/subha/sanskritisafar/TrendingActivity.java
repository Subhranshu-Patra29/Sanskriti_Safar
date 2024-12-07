package com.subha.sanskritisafar;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class TrendingActivity extends AppCompatActivity {

    String selectedLanguage;
    private static final String TAG = "TrendingSectionActivity";
    private RecyclerView trendingRecyclerView;
    private TrendingLocationAdapter adapter;
    private List<TrendingLocation> trendingLocations;
    private boolean isMoreDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_trending);

        selectedLanguage = getIntent().getStringExtra("SELECTED_LANGUAGE");

        // Initialize RecyclerView
        trendingRecyclerView = findViewById(R.id.trending_recycler_view);
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        trendingLocations = new ArrayList<>();
        adapter = new TrendingLocationAdapter(this, trendingLocations, location -> {
            // Handle card click
//            Toast.makeText(this, "Clicked: " + location.getId(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LocationDetailsActivity.class);
            intent.putExtra("location_id", location.getId());
            intent.putExtra("SELECTED_LANGUAGE", selectedLanguage);
            startActivity(intent);
        });
        trendingRecyclerView.setAdapter(adapter);

        // Fetch data from Firestore
        fetchTrendingLocations();

        // Find the button in your activity
        Button showMoreButton = findViewById(R.id.show_more_button);
        // Initially hide the button
        showMoreButton.setVisibility(View.GONE);

        // Add a scroll listener to the RecyclerView
        trendingRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if the RecyclerView has reached the bottom
                if (!recyclerView.canScrollVertically(1) && !isMoreDataLoaded) {
                    // Show the "Show More" button when at the bottom
                    showMoreButton.setVisibility(View.VISIBLE);
                } else {
                    // Hide the button when not at the bottom
                    showMoreButton.setVisibility(View.GONE);
                }
            }
        });

// Handle the button click
        showMoreButton.setOnClickListener(v -> {
            // Load more data
            loadMoreData();

            // Hide the button after loading more data
            showMoreButton.setVisibility(View.GONE);

            isMoreDataLoaded = true;

            adapter = new TrendingLocationAdapter(this, trendingLocations, location -> {
                // Handle card click
//            Toast.makeText(this, "Clicked: " + location.getId(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, LocationDetailsActivity.class);
                intent.putExtra("location_id", location.getId());
                intent.putExtra("SELECTED_LANGUAGE", selectedLanguage);
                startActivity(intent);
            });
            trendingRecyclerView.setAdapter(adapter);
        });


    }

    private void fetchTrendingLocations() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("TrendingLocations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getString("id");
                            String title = document.getString("title");
                            String description = document.getString("short_description");
                            String imageUrl = document.getString("imageUrl");

                            // Create a TrendingLocation object and add it to the list
                            trendingLocations.add(new TrendingLocation(id, title, description, imageUrl));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    // Method to load more data
    private void loadMoreData() {
        // Add logic to fetch and add more data to your RecyclerView's adapter
        List<TrendingLocation> moreLocations = fetchMoreLocations();
        trendingLocations.addAll(moreLocations);
        adapter.notifyDataSetChanged();
    }

    // Mock method for fetching more data
    private List<TrendingLocation> fetchMoreLocations() {
        List<TrendingLocation> moreLocations = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("TrendingLocations2")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getString("id");
                            String title = document.getString("title");
                            String description = document.getString("short_description");
                            String imageUrl = document.getString("imageUrl");

                            // Create a TrendingLocation object and add it to the list
                            trendingLocations.add(new TrendingLocation(id, title, description, imageUrl));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
        return moreLocations;
    }
}

