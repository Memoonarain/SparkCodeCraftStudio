package com.example.noted;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.widget.EditText;

import yuku.ambilwarna.AmbilWarnaDialog;

public class EditorHelper {

    public EditorHelper() {
    }

    public void applyTextFormat(int formatType,EditText editText) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(editText.getText());
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start == end) return; // No selection

        switch (formatType) {
            case 1: // Bold
                spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case 2: // Italic
                spannable.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case 3: // Underline
                spannable.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case 4: // Clear formatting
                spannable.removeSpan(new StyleSpan(android.graphics.Typeface.BOLD));
                spannable.removeSpan(new StyleSpan(android.graphics.Typeface.ITALIC));
                spannable.removeSpan(new UnderlineSpan());
                break;
        }

        editText.setText(spannable);
        editText.setSelection(start, end);
    }

    public void setTextSize(float textSize,EditText editText) {
        editText.setTextSize(textSize);

    }

    public void setTextAlignment(int alignmentType,EditText editText) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(editText.getText());
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start == end) return; // No selection

        Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
        switch (alignmentType) {
            case 1: // Center
                alignment = Layout.Alignment.ALIGN_CENTER;
                break;
            case 2: // Justify
                alignment = Layout.Alignment.ALIGN_CENTER; // Android does not support justify directly
                break;
            case 3: // Left
                alignment = Layout.Alignment.ALIGN_NORMAL;
                break;
            case 4: // Right
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
                break;
        }

        spannable.setSpan(new AlignmentSpan.Standard(alignment), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(spannable);
        editText.setSelection(start, end);
    }

   public void applyColour(String color,EditText editText) {
        int colorInt = Color.parseColor(color);
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start == end) return; // No selection
        SpannableStringBuilder spannable = new SpannableStringBuilder(editText.getText());

        spannable.setSpan(new ForegroundColorSpan(colorInt), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(spannable);
        editText.setSelection(start, end);
    }
}
