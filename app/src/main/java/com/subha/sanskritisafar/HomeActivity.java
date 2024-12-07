package com.subha.sanskritisafar;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.location.Address;
import android.location.Geocoder;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import org.osmdroid.views.overlay.Marker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private String selectedLanguage;

    private MapView map;
    private MyLocationNewOverlay locationOverlay;
    private SearchView locationSearchView;

    private String selectedHeritageSiteID;

    private RecyclerView locationSearchResults;
    private LinearLayout recommendationSection;
    private SuggestionsAdapter suggestionsAdapter;
    private ArrayList<String> suggestionsList = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<String, List<String>> cachedSuggestions = new HashMap<>();
    private static final int DEBOUNCE_DELAY_MS = 300;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_REQUEST = 101;

    TextToSpeech textToSpeech;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    private LinearLayout exploreButton;
    private LinearLayout profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        selectedLanguage = getIntent().getStringExtra("SELECTED_LANGUAGE");

        // Load OpenStreetMap configuration
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));

        setContentView(R.layout.activity_home);

        // Initialize the MapView
        map = findViewById(R.id.map);
        locationSearchView = findViewById(R.id.locationSearchBar);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // Set up the map to show the user's location
        setupLocationOverlay();

        // Add compass overlay
        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);


        // Initialize the LinearLayout buttons
        exploreButton = findViewById(R.id.explore_btn);
        profileButton = findViewById(R.id.profile_btn);

        // Set OnClickListener for Explore button
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to HomeActivity (or the activity you want for Home)
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
                // Optional: Finish the current activity so user cannot return to it
                finish();
            }
        });

        // Set OnClickListener for Profile button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                // Optional: Finish the current activity so user cannot return to it
                finish();
            }
        });

        locationSearchResults = findViewById(R.id.location_search_results);
        recommendationSection = findViewById(R.id.recommendation_section);
        suggestionsAdapter = new SuggestionsAdapter(suggestionsList, suggestion -> {
            // Handle suggestion click
            Toast.makeText(HomeActivity.this, "Selected: " + suggestion, Toast.LENGTH_SHORT).show();
            locationSearchView.setQuery(suggestion, false);
            locationSearchResults.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recommendationSection.getLayoutParams();
            params.setMargins(20, 290, 20, 20);
            recommendationSection.setLayoutParams(params);
        });
        locationSearchResults.setLayoutManager(new LinearLayoutManager(this));
        locationSearchResults.setAdapter(suggestionsAdapter);
        locationSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(HomeActivity.this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
                locationSearchResults.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recommendationSection.getLayoutParams();
                params.setMargins(20, 290, 20, 20);
                recommendationSection.setLayoutParams(params);
                searchLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty())
                {
                    locationSearchResults.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recommendationSection.getLayoutParams();
                    params.setMargins(20, 290, 20, 20);
                    recommendationSection.setLayoutParams(params);
                }
                if(newText.length() > 2)
                    fetchLocationSuggestions(newText);
                return false;
            }
        });


        ImageButton microphoneButton = findViewById(R.id.microphone_button);
        ImageButton cameraButton = findViewById(R.id.camera_button);
        ImageButton trendingButton = findViewById(R.id.trending_button);

        microphoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Step 1: Play the instruction audio
                playAudioInstruction();
                // Step 2: Take voice input
                startVoiceInput();
            }
        });

        cameraButton.setOnClickListener(v -> {
            cameraButton.setOnClickListener(a -> checkCameraPermission());
        });

        trendingButton.setOnClickListener(v -> {
//            Toast.makeText(HomeActivity.this, "Trending Button Clicked", Toast.LENGTH_SHORT).show();
            // TODO: Implement functionality to open new activity with trending items
            Intent intent = new Intent(HomeActivity.this, TrendingActivity.class);
            intent.putExtra("SELECTED_LANGUAGE", selectedLanguage);
            startActivity(intent);
        });

        // Fetch and display the recommendation
        fetchRecommendations();
        
        // User Clicks recommended card
        CardView recommendationCard = findViewById(R.id.recommendation_card);
        recommendationCard.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ARActivity.class);
            intent.putExtra("heritage_site_id", selectedHeritageSiteID);
            intent.putExtra("SELECTED_LANGUAGE", selectedLanguage);
            startActivity(intent);
        });

    }

    private void fetchRecommendations() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("recommendations");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Recommendation> recommendations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recommendation recommendation = snapshot.getValue(Recommendation.class);
                    recommendations.add(recommendation);
                }

                // Select one random recommendation
                if (!recommendations.isEmpty()) {
                    Recommendation selected = recommendations.get(new Random().nextInt(recommendations.size()));
                    selectedHeritageSiteID = selected.getId();
                    displayRecommendation(selected);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });

    }

    private void displayRecommendation(Recommendation recommendation) {
        TextView nameView = findViewById(R.id.recommendation_name);
        TextView descriptionView = findViewById(R.id.recommendation_description);
        ImageView imageView = findViewById(R.id.recommendation_image);

        nameView.setText(recommendation.getTitle());
        descriptionView.setText(recommendation.getShort_description());

        // If you use Glide or Picasso to load images
        Glide.with(this).load(recommendation.getImageUrl()).into(imageView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupLocationOverlay();
        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupLocationOverlay() {
        // Check if permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Initialize the location overlay
            locationOverlay = new MyLocationNewOverlay(map);
            locationOverlay.enableMyLocation(); // Enable location tracking
            locationOverlay.runOnFirstFix(() -> {
                // Center map on the user's current location when location is first acquired
                runOnUiThread(() -> {
                    GeoPoint currentLocation = locationOverlay.getMyLocation();
                    if (currentLocation != null) {
                        map.getController().setCenter(currentLocation);
                        map.getController().setZoom(10);  // Set an appropriate zoom level
                    }
                });
            });
            map.getOverlays().add(locationOverlay);
        }
    }

    public void setupLocationOverlay(View view) {
        setupLocationOverlay();
    }

    private void searchLocation(String locationName) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            // Get the list of possible addresses for the given location name
            addresses = geocoder.getFromLocationName(locationName, 1);

            if (addresses != null && !addresses.isEmpty()) {
                // Get the latitude and longitude of the first address in the list
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                // Move the map to the specified location
                GeoPoint point = new GeoPoint(latitude, longitude);
                map.getController().animateTo(point);

                // Optionally, add a marker at the searched location
                Marker marker = new Marker(map);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle("Searched Location");
                map.getOverlays().add(marker);
                map.invalidate(); // Refresh the map
            } else {
                // Handle the case where no address was found
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to geocode location", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchLocationSuggestions(String query) {

        if (cachedSuggestions.containsKey(query)) {
            suggestionsList.clear();
            suggestionsList.addAll(cachedSuggestions.get(query));
            locationSearchResults.setVisibility(View.VISIBLE);
            suggestionsAdapter.notifyDataSetChanged();
            return;
        }

        handler.removeCallbacksAndMessages(null); // Clear previous callbacks
        handler.postDelayed(() -> {
            new Thread(() -> {
                try {
                    String url = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json&limit=7";
                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();

                    JSONArray jsonArray = new JSONArray(jsonData);
                    List<String> newSuggestions = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject location = jsonArray.getJSONObject(i);
                        String displayName = location.getString("display_name");
                        newSuggestions.add(displayName);
                    }

                    // Cache the results
                    cachedSuggestions.put(query, newSuggestions);

                    // Update the UI
                    runOnUiThread(() -> {
                        suggestionsList.clear();
                        suggestionsList.addAll(newSuggestions);

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recommendationSection.getLayoutParams();
                        params.setMargins(20, 50, 20, 20);
                        recommendationSection.setLayoutParams(params);

                        locationSearchResults.setVisibility(View.VISIBLE);
                        suggestionsAdapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }, DEBOUNCE_DELAY_MS);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_REQUEST && resultCode == RESULT_OK && data != null) {
            Bitmap capturedImage = (Bitmap) data.getExtras().get("data");

            // Create an Intent to start the new activity
            Intent intent = new Intent(HomeActivity.this, ImageDetailActivity.class);

            // Pass the captured image as an extra (use a Bundle)
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            capturedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra("capturedImage", byteArray);

            // Start the new activity
            startActivity(intent);
        }

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String placeName = result.get(0);
            locationSearchView.setQuery(placeName, true);
        }
    }

    private void playAudioInstruction() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int langResult = textToSpeech.setLanguage(Locale.US);
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Language not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        textToSpeech.speak("Please say the name of the place you want to visit!", TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }

}