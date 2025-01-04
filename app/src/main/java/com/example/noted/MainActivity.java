package com.example.noted;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noted.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GridLayoutManager gridLayoutManager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddNote;
    private TextView addNoteText;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();


    }

    private void setup() {
        intializeIds();
        clicklistener();

        // Set up the Toolbar
        setSupportActionBar(toolbar);
        NotesDatabaseHelper dbHelper = new NotesDatabaseHelper(this);
        List<NotesModel> notesList = dbHelper.fetchAllNotes();
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        NotesAdapter adapter = new NotesAdapter(notesList, this);
        // Set the LayoutManager to GridLayoutManager with 2 columns
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        // Configure ActionBarDrawerToggle for opening/closing the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void intializeIds() {
        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);
        recyclerView = findViewById(R.id.recycler_view);
        fabAddNote = findViewById(R.id.fab_add_note);
        addNoteText = findViewById(R.id.add_note_text);
    }

    private void clicklistener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            handleNavigationItemClick(item);
            return true;
        }
    });

        fabAddNote.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if (addNoteText.getVisibility() == View.GONE) { addNoteText.setVisibility(View.VISIBLE); } else { addNoteText.setVisibility(View.GONE); } } });
        addNoteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NoteEditor.class));
            }
        });
    }

    private void handleNavigationItemClick(MenuItem item) {
        if (item.getItemId() == R.id.nav_account) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null){
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
            else {

                startActivity(new Intent(MainActivity.this, Login_Activity.class));
            }
        } else if (item.getItemId() == R.id.nav_settings) {

            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (item.getItemId()==R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged Out from the account successfully", Toast.LENGTH_LONG).show();
        }else {

        }
        // Close the drawer after an item is clicked
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        // Close the drawer if open, otherwise follow the default back behavior
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
