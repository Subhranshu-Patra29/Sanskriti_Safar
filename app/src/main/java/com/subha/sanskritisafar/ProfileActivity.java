package com.subha.sanskritisafar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // UI Components
    private ImageView profilePicture;
    private TextView nameTextView, emailTextView, phoneTextView, languageTextView;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private LinearLayout exploreButton;
    private LinearLayout profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Bind UI Components
        profilePicture = findViewById(R.id.profile_picture);
        nameTextView = findViewById(R.id.profile_name);
        emailTextView = findViewById(R.id.profile_email);
        phoneTextView = findViewById(R.id.profile_phone);
        languageTextView = findViewById(R.id.profile_language);

        // Fetch and Display User Data
        if (currentUser != null) {

            // Fetch the profile picture URL
            Uri photoUrl = currentUser.getPhotoUrl();

            // Check if photoUrl is not null and load the image
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)  // URL from Firebase
                        .into(profilePicture); // ImageView where the profile picture will be shown
            } else {
                // Optionally, load a default image if no profile picture is set
                Glide.with(this)
                        .load(R.drawable.profile2)  // Your default image
                        .into(profilePicture);
            }

            String uid = currentUser.getUid(); // Unique ID of the user
            emailTextView.setText(currentUser.getEmail());

            fetchUserData(uid);
            fetchChatHistory();
            
            
            // Initialize the LinearLayout buttons
            exploreButton = findViewById(R.id.explore_btn);
            profileButton = findViewById(R.id.profile_btn);

            // Set OnClickListener for Explore button
            exploreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to HomeActivity (or the activity you want for Home)
                    Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
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
                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    // Optional: Finish the current activity so user cannot return to it
                    finish();
                }
            });
        }

    }

    private void fetchChatHistory() {
        // Get reference to the ScrollView and LinearLayout inside it
        ScrollView chatHistoryScrollView = findViewById(R.id.chat_history_scroll);
        LinearLayout chatMessagesLayout = findViewById(R.id.chat_messages_layout);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ChatbotConversations")
                .orderBy("timestamp") // Assuming you have a timestamp field to order by
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            // Clear any previous messages
                            chatMessagesLayout.removeAllViews();

                            // Iterate over the documents in the query snapshot
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String prompt = document.getString("prompt");
                                String response = document.getString("response");

                                // Create TextViews for each message
                                TextView chatMessage = new TextView(ProfileActivity.this);
                                chatMessage.setText(prompt + "\n\n" + response);
                                chatMessage.setTextSize(16);
                                chatMessage.setTextColor(Color.parseColor("#3b3b3b"));
                                chatMessage.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);

                                // Set padding for the TextView to avoid text touching the borders
                                chatMessage.setPadding(16, 16, 16, 16);

                                // Add borders using a GradientDrawable
                                GradientDrawable border = new GradientDrawable();
                                border.setShape(GradientDrawable.RECTANGLE);
                                border.setCornerRadius(8); // Set rounded corners
                                border.setStroke(2, getResources().getColor(android.R.color.darker_gray)); // Set border color and width
                                chatMessage.setBackground(border);


                                // Add each message to the LinearLayout inside the ScrollView
                                chatMessagesLayout.addView(chatMessage);
                            }
                        }
                    } else {
                        // Handle the error
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    // Fetch User Data from Firebase Realtime Database
    private void fetchUserData(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch user data from Firestore
        db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");
                        String language = documentSnapshot.getString("preferredLanguage");

                        // Update the UI with the fetched data (e.g., set the TextViews)
                        nameTextView.setText(name);
                        phoneTextView.setText(phone);
                        emailTextView.setText(email);
                        languageTextView.setText(language);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                });
    }
}
