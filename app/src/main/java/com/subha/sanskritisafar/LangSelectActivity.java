package com.subha.sanskritisafar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LangSelectActivity extends AppCompatActivity {

    private String selectedLanguage = "";
    String isoCode;
    TextView getStartedButton;
    TextView [] langTextView = new TextView[8];
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);

        // Initialize Firebase components
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements and set up listeners for each language and button

        langTextView[0] = findViewById(R.id.textView3);
        langTextView[1] = findViewById(R.id.textView4);
        langTextView[2] = findViewById(R.id.textView5);
        langTextView[3] = findViewById(R.id.textView6);
        langTextView[4] = findViewById(R.id.textView7);
        langTextView[5] = findViewById(R.id.textView8);
        langTextView[6] = findViewById(R.id.textView9);
        langTextView[7] = findViewById(R.id.textView10);

        getStartedButton = findViewById(R.id.get_started_button);

        // Set click listeners for each language
        langTextView[0].setOnClickListener(v -> selectLanguage("English", 0));
        langTextView[1].setOnClickListener(v -> selectLanguage("Hindi", 1));
        langTextView[2].setOnClickListener(v -> selectLanguage("Bengali", 2));
        langTextView[3].setOnClickListener(v -> selectLanguage("Odia", 3));
        langTextView[4].setOnClickListener(v -> selectLanguage("Kannada", 4));
        langTextView[5].setOnClickListener(v -> selectLanguage("Tamil", 5));
        langTextView[6].setOnClickListener(v -> selectLanguage("Telugu", 6));
        langTextView[7].setOnClickListener(v -> selectLanguage("Malayalam", 7));

        // Handle Get Started Button click
        getStartedButton.setOnClickListener(v -> {
            if (!selectedLanguage.isEmpty()) {
                Intent intent = new Intent(LangSelectActivity.this, HomeActivity.class);
                intent.putExtra("SELECTED_LANGUAGE", isoCode);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LangSelectActivity.this, "Please select a language", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectLanguage(String language, int langNo) {
        selectedLanguage = language;
        switch (selectedLanguage) {
            case "English":
                isoCode = "en";
                break;
            case "Hindi":
                isoCode = "hi";
                break;
            case "Bengali":
                isoCode = "bn";
                break;
            case "Odia":
                isoCode = "or";
                break;
            case "Kannada":
                isoCode = "kn";
                break;
            case "Tamil":
                isoCode = "ta";
                break;
            case "Telugu":
                isoCode = "te";
                break;
            case "Malayalam":
                isoCode = "ml";
                break;
            default:
                isoCode = "en";
        }
        getStartedButton.setVisibility(View.VISIBLE); // Show the button once a language is selected
        langTextView[langNo].setBackgroundResource(R.drawable.lang_background_click);
        for (int i = 0; i < 8; i++) {
            if (i != langNo) {
                langTextView[i].setClickable(false);
            }
        }
        saveSelectedLanguage();
    }

    private void saveSelectedLanguage() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference.child(userId).child("preferredLanguage").setValue(selectedLanguage);

            // Get the Firestore instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a map to hold the language data
            Map<String, Object> languageData = new HashMap<>();
            languageData.put("preferredLanguage", selectedLanguage);

            // Save the preferred language to Firestore under the users collection
            db.collection("Users").document(userId)
                    .update(languageData)
                    .addOnSuccessListener(aVoid -> {
                        // Successfully saved the language
                        Toast.makeText(getApplicationContext(), "Language updated successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(getApplicationContext(), "Failed to update language.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
