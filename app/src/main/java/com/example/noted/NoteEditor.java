package com.example.noted;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class NoteEditor extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText noteContent, noteTitle;
    private int DEFUALT_TEXT_SIZE = 18;
    private EditText editTextsizeMain;
    private ImageButton expandButton, boldButton, italicButton, underlineButton, increaseSize, decreaseSize, alignCentre, alignJustify, alignLeft, alignRight, colourCustom;
    
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


         toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        boldButton = findViewById(R.id.btn_bold);
        italicButton = findViewById(R.id.btn_italic);
        underlineButton = findViewById(R.id.btn_underline);
        increaseSize = findViewById(R.id.btn_text_increase);
        decreaseSize = findViewById(R.id.btn_decrease);
        alignCentre = findViewById(R.id.btn_alignment_center);
        alignJustify = findViewById(R.id.btn_alignment_justify);
        alignLeft = findViewById(R.id.btn_alignment_left);
        alignRight = findViewById(R.id.btn_alignment_right);
        colourCustom = findViewById(R.id.btn_text_color);
        noteContent = findViewById(R.id.rich_text_editor);
        noteTitle = findViewById(R.id.notes_title);
        editTextsizeMain = findViewById(R.id.edtTxtSizeMain);
        expandButton = findViewById(R.id.btn_expand);

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
        boldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyTextFormat(1);
            }
        });
        italicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyTextFormat(2);
            }
        });
        underlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyTextFormat(3);
            }
        });
        increaseSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++DEFUALT_TEXT_SIZE;
                editTextsizeMain.setText(String.valueOf(DEFUALT_TEXT_SIZE));
                if(DEFUALT_TEXT_SIZE>4&&DEFUALT_TEXT_SIZE<30){
                    setTextSize(DEFUALT_TEXT_SIZE);
                    }
                else {
                    editTextsizeMain.setError("Please enter a valid text size"); // Show error if size is invalid
                }
            }
        });
        decreaseSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --DEFUALT_TEXT_SIZE;
                editTextsizeMain.setText(String.valueOf(DEFUALT_TEXT_SIZE));
                if(DEFUALT_TEXT_SIZE>4&&DEFUALT_TEXT_SIZE<30){
                    setTextSize(DEFUALT_TEXT_SIZE);
                }
                else {
                    editTextsizeMain.setError("Please enter a valid text size"); // Show error if size is invalid
                }
            }
        });
        alignCentre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextAlignment(1);
            }
        });
        alignJustify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextAlignment(2);
            }
        });
        alignLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextAlignment(3);
            }
        });
        alignRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextAlignment(4);
            }
        });
        editTextsizeMain.setOnEditorActionListener((v, actionId, event) -> {
            String input = editTextsizeMain.getText().toString();
            if (!input.isEmpty()) {
                try {
                    int textSize = Integer.parseInt(input);
                    if (textSize > 4&&textSize<30) {
                        setTextSize(textSize); // Call setTextSize with the input size
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
        bottomSheetView.findViewById(R.id.btn_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyTextFormat(1);
                bottomSheetDialog.dismiss(); // Close dialog
            }
        });
        bottomSheetView.findViewById(R.id.btn_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyTextFormat(2);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.btn_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyTextFormat(3);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.btn_clear_format).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyTextFormat(4);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.btn_text_increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++DEFUALT_TEXT_SIZE;
                if(DEFUALT_TEXT_SIZE>4&&DEFUALT_TEXT_SIZE<30){
                    setTextSize(DEFUALT_TEXT_SIZE);
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
                    setTextSize(DEFUALT_TEXT_SIZE);
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
                        setTextSize(textSize); // Call setTextSize with the input size
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

        bottomSheetView.findViewById(R.id.btn_alignment_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextAlignment(1);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.btn_alignment_justify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextAlignment(2);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.btn_alignment_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextAlignment(3);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.btn_alignment_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextAlignment(4);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.btn_text_color_white).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyColor("#ffffff");
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.btn_text_color_black).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyColor("#000000");
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.btn_text_color_Grey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyColor("#666666");
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.btn_text_color_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //write code to open pallete and whatever colour user slecet, that colour's code sent to applyColor function to set that color
                bottomSheetDialog.dismiss();
            }
        });
        // Show the dialog
        bottomSheetDialog.show();
    }

    private void setTextAlignment(int alignment) {

        int start = noteContent.getSelectionStart();
        int end = noteContent.getSelectionEnd();
        if (start<end){
            if (alignment==1){
                SpannableStringBuilder ssb = new SpannableStringBuilder(noteContent.getText());
                ssb.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                noteContent.setText(ssb);
                noteContent.setSelection(end);
            }else if (alignment==2){
                SpannableStringBuilder ssb = new SpannableStringBuilder(noteContent.getText());
                ssb.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                noteContent.setText(ssb);
                noteContent.setSelection(end);
            }else if (alignment==3){
                SpannableStringBuilder ssb = new SpannableStringBuilder(noteContent.getText());
                ssb.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                noteContent.setText(ssb);
                noteContent.setSelection(end);
            }else if (alignment==4){
                SpannableStringBuilder ssb = new SpannableStringBuilder(noteContent.getText());
                ssb.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                noteContent.setText(ssb);
                noteContent.setSelection(end);
            }
        }
    }

    private void setTextSize(int textSize) {
        int start = noteContent.getSelectionStart();
        int end = noteContent.getSelectionEnd();

        if (start < end) { // Apply to selected text
            SpannableStringBuilder ssb = new SpannableStringBuilder(noteContent.getText());
            ssb.setSpan(
                    new android.text.style.RelativeSizeSpan((float) textSize / DEFUALT_TEXT_SIZE),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            noteContent.setText(ssb);
        } else { // Apply to all text if nothing is selected
            noteContent.setTextSize(textSize);
        }
    }


    private void applyTextFormat(int textFormat){
    int start = noteContent.getSelectionStart();
    int end = noteContent.getSelectionEnd();

    if (start < end) {
        //For BOlD
        if (textFormat == 1) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(noteContent.getText());
            ssb.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            noteContent.setText(ssb);
            noteContent.setSelection(end); // Maintain cursor position
        }
        //For Italic
        else if (textFormat == 2) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(noteContent.getText());
            ssb.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            noteContent.setText(ssb);
            noteContent.setSelection(end); // Maintain cursor position
        }
        //For UnderLine
        else if (textFormat == 3) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(noteContent.getText());
            ssb.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            noteContent.setText(ssb);
            noteContent.setSelection(end); // Maintain cursor position
        }
        //Remove all formats
        else {
            String plainText = noteContent.getText().toString();
            noteContent.setText(plainText); // Reset to plain text
            noteContent.setTypeface(Typeface.DEFAULT);
            noteContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Reset text size
            noteContent.setGravity(Gravity.START); // Reset alignment
        }

    }
}

    private void applyColor(String color) {
        int start = noteContent.getSelectionStart();
        int end = noteContent.getSelectionEnd();
        int colorInt = Color.parseColor(color);
        if (start < end) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(noteContent.getText());
            ssb.setSpan(new ForegroundColorSpan(colorInt), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            noteContent.setText(ssb);
            noteContent.setSelection(end);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_editor_toolbar_menu, menu);
        return true;
    } @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.saveNote) {

        } else if (item.getItemId()==R.id.addcategorynote) {


        } else if (item.getItemId()==R.id.deleteNote) {

        } else if (item.getItemId() == R.id.exportNote) {

        }
        return super.onOptionsItemSelected(item);

    }
}