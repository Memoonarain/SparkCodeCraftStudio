package com.example.noted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launcher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    FirebaseFirestore.getInstance().collection("users")
                            .document(user.getUid())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    SyncManager syncManager = new SyncManager(LauncherActivity.this);
                                    syncManager.syncNotes();
                                    Boolean isCompleted = documentSnapshot.getBoolean("isProfileCompleted");
                                    if (isCompleted != null && isCompleted) {
                                        // Profile is complete, go to MainActivity
                                        startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                                    } else {
                                        // Profile not complete
                                        startActivity(new Intent(LauncherActivity.this, ProfileUpdateActivity.class));
                                        Toast.makeText(LauncherActivity.this, "Profile not Completed...", Toast.LENGTH_SHORT).show();
                                    }
                                } else {Toast.makeText(LauncherActivity.this, "Profile not Found...", Toast.LENGTH_SHORT).show();
                                    // No profile found
                                    startActivity(new Intent(LauncherActivity.this, ProfileUpdateActivity.class));
                                }
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(LauncherActivity.this, "Error checking profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LauncherActivity.this, ProfileUpdateActivity.class));
                                finish();
                            });
                } else {
                    // Not logged in
                    startActivity(new Intent(LauncherActivity.this, Login_Activity.class));
                    finish();
                }

            }
        }, 3000);
    }
}