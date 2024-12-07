package com.subha.sanskritisafar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText nameField, phoneField, emailField, passwordField;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Setup Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        nameField = findViewById(R.id.editTextName);
        phoneField = findViewById(R.id.editPhone);
        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);

        //Initialize button
        register = findViewById(R.id.register_button);
        register.setOnClickListener(v -> registerUser());

    }

    public void openLoginPage(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void registerUser() {
        String name = nameField.getText().toString();
        String phone = phoneField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
//        System.out.println(name + " " + phone + " " + email + " " + password);
        if (validateInputs(name, phone, email, password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // User is created, now save the additional info
                            String userId = mAuth.getCurrentUser().getUid();
                            uploadUserData(userId, name, phone, email);
                            User user = new User(name, phone, email);
                            mDatabase.child("users").child(userId).setValue(user)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Successfully saved to database
                                            Toast.makeText(RegisterActivity.this, "User Registered!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Sign-Up Failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateInputs(String name, String phone, String email, String password) {
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadUserData(String userId, String name, String phone, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map to hold user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("email", email);

        // Upload data to Firestore under the 'users' collection
        db.collection("Users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // Successfully uploaded data
                    Toast.makeText(RegisterActivity.this, "User Registered!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(RegisterActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                });
    }

}
