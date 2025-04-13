package com.example.noted;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noted.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    private RecyclerView recyclerView, categoryRecyclerView;
    private FloatingActionButton fabAddNote;
    private TextView addNoteText;
    private FirebaseAuth mAuth;
    private NotesDatabaseHelper dbHelper;
    private CategoriesAdapter categoriesAdapter;
    private List<NotesModel> categoryList;

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
        dbHelper = new NotesDatabaseHelper(this);
        List<NotesModel> notesList = dbHelper.fetchAllNotes();
        categoryList = dbHelper.fetchAllCategories();
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();categoryList = dbHelper.fetchAllCategories();
        categoriesAdapter = new CategoriesAdapter(categoryList, this);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoriesAdapter);
        NotesAdapter adapter = new NotesAdapter(notesList, this);
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
        categoryRecyclerView = findViewById(R.id.recycler_view_categories);
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
        findViewById(R.id.btnAddCattegory).setOnClickListener(v -> {
            showCategoryDialog();
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
         if (item.getItemId() == R.id.nav_settings) {

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

    public void showCategoryDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_enter_category, null);

        // Initialize dialog components
        EditText editTextCategoryName = dialogView.findViewById(R.id.editTextCategoryName);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnOk = dialogView.findViewById(R.id.btnOk);

        // Create and configure the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Handle Cancel button click
        btnCancel.setOnClickListener(v -> {
            alertDialog.dismiss(); // Close the dialog
        });

        // Handle OK button click
        btnOk.setOnClickListener(v -> {
            String categoryName = editTextCategoryName.getText().toString().trim();
            if (categoryName.length() < 3) {
                editTextCategoryName.setError("Minimum 3 characters required");
            } else {
                dbHelper.insertCategory(categoryName);  
                categoryList.clear(); // Clear the old list
                categoryList.addAll(dbHelper.fetchAllCategories()); // Add updated categories

                // Notify the adapter of data changes
                categoriesAdapter.notifyDataSetChanged();

                // Close the dialog and show a toast
                Toast.makeText(this, "Category added: " + categoryName, Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }

}
