package com.subha.sanskritisafar;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationDetailsActivity extends AppCompatActivity {

    String selectedLanguage;
    private String heritageSiteID;
    private String heritageSiteName, short_description, long_description, imageURL, imageUrl360;
    Map<String, ArrayList<String>> objects;
    ArrayList<String> insideObjects;
    ArrayList<String> outsideObjects;

    private TextToSpeech textToSpeech;
    private ImageButton readAloudButton;
    private ImageButton microphoneButton;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    // Declare a boolean to track TTS state
    private boolean isReading = false;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_location_details);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get data from intent
        selectedLanguage = getIntent().getStringExtra("SELECTED_LANGUAGE");
        heritageSiteID = getIntent().getStringExtra("location_id");
        
        fetchLocationData(heritageSiteID);

        // Bottom: Setup Tabs and ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Setup ViewPager with Fragment Adapter
        viewPager.setAdapter(new TabAdapter(this));

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Description");

                    break;
                case 1:
                    tab.setText("AR Objects");
                    break;
            }
        }).attach();

        // Back Button Functionality
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        // Screenshot Button Functionality
        ImageButton screenshotButton = findViewById(R.id.screenshot_button);
        screenshotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
            }
        });

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Log.d("TTS", "TextToSpeech initialized successfully.");
            } else {
                Log.e("TTS", "Initialization failed.");
            }
        });

        microphoneButton = findViewById(R.id.microphone_button);
        microphoneButton.setOnClickListener(v -> startVoiceInput());
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Speech input is not supported on your device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String userQuery = result.get(0);
                processQuery(userQuery);
            }
        }
    }

    private void processQuery(String userQuery) {

        // Initialize Retrofit and ApiService
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Create a request body
        RequestBody requestBody = new RequestBody(userQuery);

        // Make the API call
        Call<ResponseBody> call = apiService.generateResponse(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String apiResponse = response.body().getResponse();

                    if(!selectedLanguage.equals("en"))
                    {
                        Log.e("PRINTOUT", selectedLanguage);
                        LibreTranslate obj = new LibreTranslate();
                        obj.translate(apiResponse, selectedLanguage, new LibreTranslate.TranslationCallback() {
                            @Override
                            public void onTranslationComplete(String translatedText) {
                                showAlertDialog(userQuery, translatedText, selectedLanguage);
                                Log.d("API Response", translatedText);
                            }
                        });
                    }
                    else {
                        showAlertDialog(userQuery, apiResponse);
                        Log.d("API Response", apiResponse);
                    }
                } else {
                    Log.e("API Error", "Response was not successful");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API Error", t.getMessage());
            }
        });

    }

    private void showAlertDialog(String userQuery, String apiResponse) {
        readTextAloud(apiResponse, "en");
        userQuery = Character.toUpperCase(userQuery.charAt(0)) + userQuery.substring(1) + "?";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AI Reply");
        builder.setMessage(userQuery + "\n\n" + apiResponse);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showAlertDialog(String userQuery, String apiResponse, String language) {
        readTextAloud(apiResponse, language);
        userQuery = Character.toUpperCase(userQuery.charAt(0)) + userQuery.substring(1) + "?";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AI Reply");
        builder.setMessage(userQuery + "\n\n" + apiResponse);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void fetchLocationData(String locationId) {
        String collectionPath = "";
        if(Integer.parseInt(locationId) > 5)
            collectionPath = "TrendingLocations2";
        else
            collectionPath = "TrendingLocations";

        db.collection(collectionPath)
                .document(locationId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve data
                        heritageSiteName = documentSnapshot.getString("title");
                        long_description = documentSnapshot.getString("long_description");
                        imageUrl360 = documentSnapshot.getString("imageUrl360");
                        objects = (Map<String, ArrayList<String>>) documentSnapshot.get("objects");

                        if (objects != null) {
                            insideObjects = objects.get("Inside");
                            outsideObjects = objects.get("Outside");
                        }

                        // Set data to views
                        TextView siteNameTextView = findViewById(R.id.heritageSiteName);
                        siteNameTextView.setText(heritageSiteName != null ? heritageSiteName : "Welcome to 360 Vision");

                        setup360View();

                        TabAdapter tabAdapter = new TabAdapter(LocationDetailsActivity.this, long_description, insideObjects, outsideObjects);
                        ViewPager2 viewPager = findViewById(R.id.viewPager);
                        viewPager.setAdapter(tabAdapter);

                        // Find Read Aloud button and set a click listener
                        readAloudButton = findViewById(R.id.info_button);
                        readAloudButton.setOnClickListener(v -> {
                            // Example text to read aloud
                            // String textToRead = "This is the Taj Mahal, a UNESCO World Heritage Site located in Agra, India.";
                            String textToRead = long_description;
                            readTextAloud(textToRead, "en");
                        });

                    } else {
                        Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setup360View() {
        WebView webView = findViewById(R.id.webView);

        // Enable JavaScript and other settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Ensure links and redirects open inside WebView
        webView.setWebViewClient(new WebViewClient());

        // Load the local HTML file
        webView.loadUrl("file:///android_asset/index.html");
        String panoramaUrl = imageUrl360;

        // Wait for the page to load before injecting JavaScript
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                webView.evaluateJavascript("setPanorama('" + panoramaUrl + "');", null);
            }
        });
    }

    // Function to read text aloud
    private void readTextAloud(String text, String languageCode) {
        if (textToSpeech != null) {
            // Set the language dynamically
            Locale locale = new Locale(languageCode);
            int result = textToSpeech.setLanguage(locale);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported: " + languageCode);
                return;
            }

            if (textToSpeech.isSpeaking()) {
                // If already speaking, stop the TTS
                textToSpeech.stop();
                isReading = false;  // Update the state
            } else {
                // If not speaking, start reading the text
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                isReading = true;  // Update the state
            }
        }
        else {
            Log.e("TTS", "TextToSpeech is not initialized.");
        }
    }

    @Override
    protected void onDestroy() {
        // Shutdown Text-to-Speech to release resources
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    // TabAdapter to manage fragments
    private static class TabAdapter extends FragmentStateAdapter {
        private String longDescription;
        private ArrayList<String> insideObjects;
        private ArrayList<String> outsideObjects;

        public TabAdapter(@NonNull AppCompatActivity activity){
            super(activity);
            longDescription = "";
        }

        public TabAdapter(@NonNull AppCompatActivity activity, String longDescription, ArrayList<String> insideObjects, ArrayList<String> outsideObjects) {
            super(activity);
            this.longDescription = longDescription;
            this.insideObjects = insideObjects;
            this.outsideObjects = outsideObjects;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new DescriptionFragment(longDescription); // Pass the long description
                case 1:
                    return ArObjectsFragment.newInstance(insideObjects, outsideObjects); // Pass inside and outside objects
                default:
                    return new DescriptionFragment("This tab gives the detailed description of the heritage site.");
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Number of tabs
        }
    }

    // Method to capture the screenshot
    private void takeScreenshot() {
        // Get the root view of the activity (this can be the whole screen or a specific layout)
        View rootView = findViewById(android.R.id.content).getRootView();

        // Create a bitmap of the view
        Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        rootView.draw(canvas);  // Draw the view on the canvas

        // Save the bitmap to a file
        saveBitmapToGallery(bitmap);
    }

    // Method to save the screenshot bitmap to storage
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveBitmapToGallery(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "AR_Screenshot_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);  // Save to the Pictures directory

        ContentResolver resolver = getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try (OutputStream outputStream = resolver.openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();

            // Notify the user
            Toast.makeText(this, "Screenshot saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save screenshot", Toast.LENGTH_SHORT).show();
        }
    }
}

