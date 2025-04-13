package com.example.noted;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.ErrorInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText edtFirstName, edtLastName, edtEmail, edtPassword, edtConfirmPassword;
    private ImageView imgUserDp, imgEdtUserDp;
    private CheckBox boxShowPassword;
    private Button btnCreateAccount, btnLoginAccount;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();

        imgEdtUserDp = findViewById(R.id.imgEdtUserDp);
        imgUserDp = findViewById(R.id.user_dp);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmailLogin);
        edtPassword = findViewById(R.id.edtPasswordLogin1);
        edtConfirmPassword = findViewById(R.id.edtPasswordLogin);
        boxShowPassword = findViewById(R.id.boxShowPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnLoginAccount = findViewById(R.id.btnLoginAccount);
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        imgEdtUserDp.setOnClickListener(v -> selectImage());
        boxShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int inputType = isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;

            edtPassword.setInputType(inputType);
            edtConfirmPassword.setInputType(inputType);

            // Move cursor to end
            edtPassword.setSelection(edtPassword.getText().length());
            edtConfirmPassword.setSelection(edtConfirmPassword.getText().length());
        });
        btnLoginAccount.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this,Login_Activity.class)));
        btnCreateAccount.setOnClickListener(view -> {
            if (validateInputs()) {
                createAccount(edtEmail.getText().toString().trim(),
                        edtPassword.getText().toString().trim());
                // Replace this with Firebase or database logic
                Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Invalid email format");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Passwords do not match");
            return false;
        }

        if (password.length() < 6) {
            edtPassword.setError("Password should be at least 6 characters");
            return false;
        }

        return true;
    }
    private void createAccount(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SignupActivity.this,
                                "Account created for: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        if (user != null) {
                            uploadUserProfile(user.getUid());
                        }

                        // Navigate to main/dashboard activity
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imgUserDp.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadUserProfile(String userId) {
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        if (imageUri != null) {
//
//            MediaManager.get().upload(imageUri)
//                    .option("folder", "zyntra_profiles")
//                    .callback(new UploadCallback() {
//                        @Override
//                        public void onStart(String requestId) {}
//
//                        @Override
//                        public void onProgress(String requestId, long bytes, long totalBytes) {}
//
//                        @Override
//                        public void onSuccess(String requestId, Map resultData) {
//                            String imageUrl = resultData.get("secure_url").toString();
//
//                            saveUserToFirestore(db, userId, firstName, lastName,imageUrl);
//                        }
//
//                        @Override
//                        public void onError(String requestId, ErrorInfo error) {
//                            Toast.makeText(SignupActivity.this, "Cloudinary Upload Error: " + error.getDescription(), Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onReschedule(String requestId, ErrorInfo error) {}
//                    }).dispatch();
//        } else {
//            // No image selected
//
//        }
        saveUserToFirestore(db, userId, firstName, lastName, null);
    }

    private void saveUserToFirestore(FirebaseFirestore db, String userId, String firstName,
                                     String lastName, String imageUrl) {

        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("profileImageUrl", imageUrl);
        userData.put("email", edtEmail.getText().toString().trim());
        userData.put("isProfileCompleted", true);
        db.collection("users")
                .document(userId)
                .set(userData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                    userData.put("profileCompleted", true); // Only added if profile is saved successfully

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}