package com.example.noted;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;
public class NoteEditor extends AppCompatActivity {
    private Toolbar toolbar;
    private EditorHelper editorHelper;
    private EditText noteContent, noteTitle;
    private int DEFUALT_TEXT_SIZE = 18;
    private EditText editTextsizeMain;
    private NotesDatabaseHelper dbHelper;
    private ImageButton expandButton;
    private String noteId;
    private static final int CREATE_FILE_REQUEST_CODE = 1;
    long timestamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_editor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        noteId = getIntent().getStringExtra("NOTE_ID");
         toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        dbHelper = new NotesDatabaseHelper(this);
        editorHelper = new EditorHelper();
        editTextsizeMain = findViewById(R.id.edtTxtSizeMain);
        noteContent = findViewById(R.id.rich_text_editor);
        noteTitle = findViewById(R.id.notes_title);
        expandButton = findViewById(R.id.btn_expand);
        if (noteId == null || noteId.isEmpty()) {
            Toast.makeText(this, "Created New Note File", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Editing a Note File", Toast.LENGTH_SHORT).show();
            fetchNoteDetails();
        }
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
        findViewById(R.id.btn_bold).setOnClickListener(v -> editorHelper.applyTextFormat(1,noteContent));
        findViewById(R.id.btn_italic).setOnClickListener(v -> editorHelper.applyTextFormat(2,noteContent));
        findViewById(R.id.btn_underline).setOnClickListener(v -> editorHelper.applyTextFormat(3,noteContent));
        findViewById(R.id.btn_text_increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++DEFUALT_TEXT_SIZE;
                editTextsizeMain.setText(String.valueOf(DEFUALT_TEXT_SIZE));
                if(DEFUALT_TEXT_SIZE>4&&DEFUALT_TEXT_SIZE<30){
                    editorHelper.setTextSize(DEFUALT_TEXT_SIZE,noteContent);
                    }
                else {
                    editTextsizeMain.setError("Please enter a valid text size"); // Show error if size is invalid
                }
            }
        });
        findViewById(R.id.btn_decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --DEFUALT_TEXT_SIZE;
                editTextsizeMain.setText(String.valueOf(DEFUALT_TEXT_SIZE));
                if(DEFUALT_TEXT_SIZE>4&&DEFUALT_TEXT_SIZE<30){
                    editorHelper.setTextSize(DEFUALT_TEXT_SIZE,noteContent);
                }
                else {
                    editTextsizeMain.setError("Please enter a valid text size"); // Show error if size is invalid
                }
            }
        });
        findViewById(R.id.btn_alignment_center).setOnClickListener(v -> editorHelper.setTextAlignment(1,noteContent));
        findViewById(R.id.btn_alignment_justify).setOnClickListener(v -> editorHelper.setTextAlignment(2,noteContent));
        findViewById(R.id.btn_alignment_left).setOnClickListener(v -> editorHelper.setTextAlignment(3,noteContent));
        findViewById(R.id.btn_alignment_right).setOnClickListener(v -> editorHelper.setTextAlignment(4,noteContent));
        editTextsizeMain.setOnEditorActionListener((v, actionId, event) -> {
            String input = editTextsizeMain.getText().toString();
            if (!input.isEmpty()) {
                try {
                    int textSize = Integer.parseInt(input);
                    if (textSize > 4&&textSize<30) {
                        editorHelper.setTextSize(textSize,noteContent); // Call setTextSize with the input size
                        DEFUALT_TEXT_SIZE = textSize;
                    } else {
                        editTextsizeMain.setError("Please enter a valid text size"); // Show error if size is invalid
                    }
                } catch (NumberFormatException e) {
                    editTextsizeMain.setError("Invalid number format");
                }
            }
            return true; // To indicate the event is handled
        });
        findViewById(R.id.btn_text_color).setOnClickListener(v -> applyCustomColour());
    }
    private void showBottomSheetDialog() {
        // Initialize BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        // Inflate the custom layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        EditText edtTextsize = bottomSheetView.findViewById(R.id.edttxtsize);
        edtTextsize.setText(String.valueOf(DEFUALT_TEXT_SIZE));
        // Set up listeners for buttons in the Bottom Sheet
        bottomSheetView.findViewById(R.id.btn_minimize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView. findViewById(R.id.btn_bold).setOnClickListener(v -> editorHelper.applyTextFormat(1,noteContent));
        bottomSheetView.findViewById(R.id.btn_italic).setOnClickListener(v -> editorHelper.applyTextFormat(2,noteContent));
        bottomSheetView.findViewById(R.id.btn_underline).setOnClickListener(v -> editorHelper.applyTextFormat(3,noteContent));
        bottomSheetView.findViewById(R.id.btn_clear_format).setOnClickListener(v -> editorHelper.applyTextFormat(4,noteContent));
        bottomSheetView.findViewById(R.id.btn_text_increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++DEFUALT_TEXT_SIZE;
                if(DEFUALT_TEXT_SIZE>4&&DEFUALT_TEXT_SIZE<30){
                    editorHelper.setTextSize(DEFUALT_TEXT_SIZE,noteContent);
                    editTextsizeMain.setText(String.valueOf(DEFUALT_TEXT_SIZE));
                    edtTextsize.setText(String.valueOf(DEFUALT_TEXT_SIZE));
                    }
                else {
                    edtTextsize.setError("Please enter a valid text size"); // Show error if size is invalid
                }
            }
        });
        bottomSheetView.findViewById(R.id.btn_decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --DEFUALT_TEXT_SIZE;
                //edtTextsize.setText(DEFUALT_TEXT_SIZE);
                if(DEFUALT_TEXT_SIZE>4&&DEFUALT_TEXT_SIZE<30){
                    editorHelper.setTextSize(DEFUALT_TEXT_SIZE,noteContent);
                    editTextsizeMain.setText(String.valueOf(DEFUALT_TEXT_SIZE));
                    edtTextsize.setText(String.valueOf(DEFUALT_TEXT_SIZE));
                    }
                else {
                    edtTextsize.setError("Please enter a valid text size"); // Show error if size is invalid
                }
            }
        });
        edtTextsize.setOnEditorActionListener((v, actionId, event) -> {
            String input = edtTextsize.getText().toString();
            if (!input.isEmpty()) {
                try {
                    float textSizee = Float.parseFloat(input);// Convert input to float
                    int textSize = (int) textSizee;
                    if (textSize > 4&&textSize<30) {
                        editorHelper.setTextSize(DEFUALT_TEXT_SIZE,noteContent);
                        DEFUALT_TEXT_SIZE = textSize;

                    } else {
                        edtTextsize.setError("Please enter a valid text size"); // Show error if size is invalid
                    }
                } catch (NumberFormatException e) {
                    edtTextsize.setError("Invalid number format");
                }
            }
            return true; // To indicate the event is handled
        });
        bottomSheetView.findViewById(R.id.btn_alignment_center).setOnClickListener(v -> editorHelper.setTextAlignment(1,noteContent));
        bottomSheetView.findViewById(R.id.btn_alignment_justify).setOnClickListener(v -> editorHelper.setTextAlignment(2,noteContent));
        bottomSheetView.findViewById(R.id.btn_alignment_left).setOnClickListener(v -> editorHelper.setTextAlignment(3,noteContent));
        bottomSheetView.findViewById(R.id.btn_alignment_right).setOnClickListener(v -> editorHelper.setTextAlignment(4,noteContent));
        bottomSheetView.findViewById(R.id.btn_text_color_white).setOnClickListener(v ->editorHelper.applyColour("#ffffff",noteContent));
        bottomSheetView.findViewById(R.id.btn_text_color_black).setOnClickListener(v ->editorHelper.applyColour("#666666",noteContent));
        bottomSheetView.findViewById(R.id.btn_text_color_Grey).setOnClickListener(v ->editorHelper.applyColour("#000000",noteContent));
        bottomSheetView.findViewById(R.id.btn_text_color_custom).setOnClickListener(v ->applyCustomColour());
        // Show the dialog
        bottomSheetDialog.show();
    }
    private void fetchNoteDetails() {
        NotesModel note = dbHelper.getNoteById(noteId);
        if (note != null) {
            noteTitle.setText(note.getNoteTitle());
            Spanned formattedContent = Html.fromHtml(note.getNoteContent());
            noteContent.setText(formattedContent);
        }
    }
    private void applyCustomColour(){
        // Initial color to show in the color picker
        int initialColor = 0xFF000000; // Default black color

        // Create and show the AmbilWarnaDialog
        AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(NoteEditor.this, initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // Convert the selected color to a hex string
                String hexColor = String.format("#%06X", (0xFFFFFF & color));

                // Pass the selected color to the applyColor function
                editorHelper.applyColour(hexColor,noteContent);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // User canceled the color picker
            }
        });

        // Show the color picker dialog
        colorPickerDialog.show();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_editor_toolbar_menu, menu);
        return true;}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MenuClick", "Menu item clicked: " + item.getTitle());
        // Handle item selection
        if (item.getItemId() == R.id.saveNote) {
            String title = noteTitle.getText().toString().trim();
            String content = Html.toHtml(new SpannedString(noteContent.getText()));  // Convert rich text to HTML
            String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(createdAt);
                timestamp = date.getTime(); // This is what you want
                Log.d("TIME_IN_MILLIS", String.valueOf(timestamp));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Validate input
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("SaveNote", "Title: " + title);
                Log.d("SaveNote", "Content: " + content);
                try {
                    if (noteId == null || noteId.isEmpty()) {
                        String noteId = UUID.randomUUID().toString();
                        // New note
                        long result = dbHelper.insertNote(noteId,title, content, timestamp);
                        if (result != -1) {
                            SyncManager syncManager = new SyncManager(NoteEditor.this);
                            syncManager.syncNotes();
                            Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        // Editing an existing note
                        int result = dbHelper.updateNote(noteId, title, content,timestamp);
                        if (result>=0) {
                            SyncManager syncManager = new SyncManager(NoteEditor.this);
                            syncManager.syncNotes();
                            Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update note", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("SaveNote", "Error saving/updating note", e);
                    Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (item.getItemId() == R.id.addcategorynote) {
            // Handle category addition logic here
        } else if (item.getItemId()==R.id.deleteNote) {

            dbHelper.deleteNote(noteId);
            finish();
        }else if (item.getItemId() == R.id.exportNote) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Request for MANAGE_EXTERNAL_STORAGE permission on Android 11 and above
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/html"); // MIME type for HTML files
                intent.putExtra(Intent.EXTRA_TITLE, "MyRichText.html");

                startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
            } else {
                // For devices below Android 10, use WRITE_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission
                    Toast.makeText(this, "Plzz allow permisson to access storage from settings", Toast.LENGTH_LONG).show();
                } else {
                    // Permission already granted
                    exportNoteToHtml();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void exportNoteToHtml() {
        try {
            String title = noteTitle.getText().toString().trim();
            // Convert SpannableString to HTML
            String htmlEncodedString = Html.toHtml(new SpannedString(noteContent.getText()));

            // Get external storage directory
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) {
                dir.mkdirs(); // Create directory if it doesn't exist
            }

            // Create a new file
            File htmlFile = new File(dir, title + ".html");

            // Write HTML string to the file
            FileOutputStream fileOutputStream = new FileOutputStream(htmlFile);
            fileOutputStream.write(htmlEncodedString.getBytes());
            fileOutputStream.close();

            Toast.makeText(NoteEditor.this, "HTML file saved at: " + htmlFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(NoteEditor.this, "Error saving file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                exportNoteToHtml();
            }
        }
    }

}