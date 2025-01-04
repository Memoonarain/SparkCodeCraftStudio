package com.example.noted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LauncherActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY = 1000;
    Button button;
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
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
        sharedPreferences = getSharedPreferences("Intro Check", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                setFirstLaunch(false);

                startActivity(intent);
                finish();
            }
        });
        if (isFirstLaunch()){
            findViewById(R.id.firstLuanch).setVisibility(View.VISIBLE);
            findViewById(R.id.logoImg).setVisibility(View.INVISIBLE);
        }else {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);

                startActivity(intent);
                finish();
            }, SPLASH_DELAY);
        }

    }
    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        editor.putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch);
        editor.apply();
    }
}